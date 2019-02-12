package com.wangwren.order.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.IDUtils;
import com.wangwren.mapper.TbOrderItemMapper;
import com.wangwren.mapper.TbOrderMapper;
import com.wangwren.mapper.TbOrderShippingMapper;
import com.wangwren.order.jedis.JedisClient;
import com.wangwren.order.pojo.OrderInfo;
import com.wangwren.order.service.OrderService;
import com.wangwren.pojo.TbOrder;
import com.wangwren.pojo.TbOrderExample;
import com.wangwren.pojo.TbOrderExample.Criteria;
import com.wangwren.utils.FastDFSClient;
import com.wangwren.pojo.TbOrderItem;
import com.wangwren.pojo.TbOrderItemExample;
import com.wangwren.pojo.TbOrderShipping;
/**
 * 订单处理
 * @author wwr
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	private static AlipayTradeService tradeService;
	
	static {
		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	/**
	 * 图片服务器地址
	 */
	@Value("${PICTUER_SERVICE_URL}")
	private String PICTUER_SERVICE_URL;
	
	/**
	 * redis中订单号的key
	 */
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	
	/**
	 * 订单号自动增长的初始值
	 */
	@Value("${ORDER_ID_BEGIN_VALUE}")
	private String ORDER_ID_BEGIN_VALUE;
	
	/**
	 * 订单详情在redis中的key
	 */
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	private String ORDER_ITEM_ID_GEN_KEY;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	/**
	 * 创建订单，返回订单号
	 * @return
	 */
	@Override
	public LetaoResult createOrder(OrderInfo orderInfo) {

		//生成订单号，使用时间戳(取当前时间的毫秒值)再加上redis的自动增长
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			//如果key不存在，则设置初始值
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_BEGIN_VALUE);
		}
		Long genOrderId = IDUtils.genOrderId();
		//自增
		String reOderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		//订单id
		String orderId = genOrderId.toString() + reOderId;
		//订单信息完善
		orderInfo.setOrderId(orderId);
		//全场包邮
		orderInfo.setPostFee("0");
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		//订单创建更新时间
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//将订单信息插入表中
		orderMapper.insert(orderInfo);
		
		//从OrderInfo中获取OrderItem订单的商品信息
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		//循环完善信息，插入数据库中
		for (TbOrderItem orderItem : orderItems) {
			//获得明细主键
			String otId = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
			orderItem.setId(otId);
			orderItem.setOrderId(orderId);
			
			//插入数据库
			orderItemMapper.insert(orderItem);
		}
		
		//获取收货地址信息，完善
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		//插入表中
		orderShippingMapper.insert(orderShipping);
		
		//最后返回订单号
		return LetaoResult.ok(orderId);
	}

	/**
	 * 支付宝支付
	 * @return
	 */
	@Override
	public LetaoResult pay(String orderId,Long userId) {
		Map<String,String> resultMap = new HashMap<>();
		//查看订单是否存在
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderId);
		criteria.andUserIdEqualTo(userId);
		List<TbOrder> list = orderMapper.selectByExample(example);
		if(list == null || list.size() == 0) {
			return LetaoResult.build(400, "订单不存在！！！");
		}
		
		//如果订单存在，肯定是只有一条数据
		TbOrder order = list.get(0);
		resultMap.put("orderNo", order.getOrderId());
		
		
		
		
		//处理支付宝支付的各种参数

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderId();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("乐淘商城扫码支付，订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单:").append(outTradeNo).append("，购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        
        //通过订单id获取商品信息
        TbOrderItemExample itemExample = new TbOrderItemExample();
        com.wangwren.pojo.TbOrderItemExample.Criteria criteria2 = itemExample.createCriteria();
        criteria2.andOrderIdEqualTo(orderId);
        List<TbOrderItem> itemList = orderItemMapper.selectByExample(itemExample);
        //循环遍历商品信息，封装至GoodsDetail
        for(TbOrderItem orderItem : itemList) {
        	//商品id，名称，单价(单位为分)，数量
        	GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getItemId(), orderItem.getTitle(), orderItem.getPrice() / 100, orderItem.getNum());
        	goodsDetailList.add(goods1);
        }
        
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl("http://wangwren.nat300.top/order/alipay_callback.action")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        
        

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	LOGGER.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //支付成功，将二维码传到服务器上
                
                // 需要修改为运行机器上的路径
                //String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",response.getOutTradeNo());
                String qrPath = String.format("E:/BaiduNetdiskDownload/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(qrPath);
                
                //上传到图片服务器
				FastDFSClient fastDFSClient;
				String url = null;
				try {
					fastDFSClient = new FastDFSClient("classpath:properties/client.conf");
					//指定文件路径上传，试试吧
	                url = fastDFSClient.uploadFile(qrPath);
	                url = PICTUER_SERVICE_URL + url;
	                System.out.println("二维码地址:" + url);
				} catch (Exception e) {
					LOGGER.error("二维码上传异常",e);
				}
                
                LOGGER.info("qrPath:" + qrPath);
                //将url地址传到map中
                resultMap.put("qrUrl", url);
                
                return LetaoResult.ok(resultMap);

            case FAILED:
            	LOGGER.error("支付宝预下单失败!!!");
            	return LetaoResult.build(400, "支付宝预下单失败!!!");

            case UNKNOWN:
            	LOGGER.error("系统异常，预下单状态未知!!!");
            	return LetaoResult.build(400, "系统异常，预下单状态未知!!!");

            default:
            	LOGGER.error("不支持的交易状态，交易返回异常!!!");
            	return LetaoResult.build(400, "不支持的交易状态，交易返回异常!!!");
        }
    
	}
	
	/**
	 * 简单打印应答
	 * @param response
	 */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
        	LOGGER.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
            	LOGGER.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            LOGGER.info("body:" + response.getBody());
        }
    }
}
