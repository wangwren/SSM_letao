<html>
	<head>
		<title>测试freemarker</title>
	</head>
	<body>
		<table border="1">
			<tr>
				<th>序号</th>
				<th>学号</th>
				<th>姓名</th>
				<th>年龄</th>
				<th>住址</th>
			</tr>
			<!-- 循环 -->
			<#list stuList as stu>
				<!-- 判断 -->
				<#if stu_index % 2 == 0>
					<tr bgcolor="red">
				<#else>
					<tr bgcolor="green">
				</#if>
						<td>${stu_index}</td><!-- 循环下标 -->
						<td>${stu.id}</td>
						<td>${stu.name}</td>
						<td>${stu.age}</td>
						<td>${stu.address}</td>
					</tr>
			</#list>
		</table>
		日期类型的处理:${date?date}<br/>
		日期类型的处理:${date?datetime}<br/>
		日期类型的处理:${date?string("yyyy/MM/dd HH:mm:ss")}<br/>
		
		<br/>
		null值的处理:${val!} <!-- 如果val不存在或者为空，取值需要加上!，否则报错 -->
		<br/>
		if判断时val为空:
		<#if val??><!-- 如果val不为空 -->
			val有值...
		<#else>
			val为空...		
		</#if>
	</body>
</html>