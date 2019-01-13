package fastdfsclient;


import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * FastDFS图片服务器测试
 * @author wwr
 *
 */
public class FastDFSClientTest {

	@Test
	public void testFastDFSClient() throws Exception {
		//添加fastdfs的jar包
		//创建配置文件，配置tracker服务器地址
		//加载配置文件
		ClientGlobal.init("E:\\workspace\\eclipsespace\\letao-manager-web\\src\\main\\resources\\resource\\client.conf");
		
		//创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		
		//使用TrackerClient对象获取TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		
		//创建一个StorageServer的引用，赋值为null就行
		StorageServer storageServer = null;
		
		//创建一个StorageClient对象，使用这个对象上传图片，参数为trackerServer，storageServer
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		
		//storageClient对象上传文件，注意第一个参数，在win10下，复制粘贴不好使，手动写路径
		String[] strings = storageClient.upload_file("C:/Users/wwr/Desktop/11.jpg", "jpg", null);
		
		for(String string : strings) {
			System.out.println(string);
		}
	}
}
