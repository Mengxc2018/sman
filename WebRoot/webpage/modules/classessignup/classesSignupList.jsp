<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>约跑报名管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>约跑报名管理列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="classesSignup" action="${ctx}/classessignup/classesSignup/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<span>报名编号：</span>
				<form:input path="bmbh" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>&nbsp;&nbsp;
			<span>报名人：</span>
				<form:input path="bmrname" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>&nbsp;&nbsp;
			<span>报名班级：</span>
				<form:input path="classesname" htmlEscape="false" maxlength="200"  class=" form-control input-sm"/>&nbsp;&nbsp;
			<span>联系手机：</span>
				<form:input path="lxsj" htmlEscape="false" maxlength="15"  class=" form-control input-sm"/>
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<shiro:hasPermission name="classessignup:classesSignup:add">
				<table:addRow url="${ctx}/classessignup/classesSignup/form" title="约跑报名管理" height="85%" width="85%"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="classessignup:classesSignup:edit">
			    <table:editRow url="${ctx}/classessignup/classesSignup/form" title="约跑报名管理" id="contentTable" height="85%" width="85%"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="classessignup:classesSignup:del">
				<table:delRow url="${ctx}/classessignup/classesSignup/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="classessignup:classesSignup:import">
				<table:importExcel url="${ctx}/classessignup/classesSignup/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="classessignup:classesSignup:export">
	       		<table:exportExcel url="${ctx}/classessignup/classesSignup/export"></table:exportExcel><!-- 导出按钮 -->
	       	</shiro:hasPermission>
	       <button class="btn btn-primary btn-outline btn-sm " data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		
			</div>
		<div class="pull-right">
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="search()" ><i class="fa fa-search"></i> 查询</button>
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()" ><i class="fa fa-refresh"></i> 重置</button>
		</div>
	</div>
	</div>
	
	<!-- 表格 -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th> <input type="checkbox" class="i-checks"></th>
				<th  class="sort-column bmbh">报名编号</th>
				<th  class="sort-column bmrname">报名人</th>
				<th  class="sort-column classesname">报名班级</th>
				<th  class="sort-column ksrq">开始日期</th>
				<th  class="sort-column bmzs">报名周数</th>
				<th  class="sort-column lxsj">联系手机</th>
				<th  class="sort-column qyj">契约金</th>
				<th  class="sort-column yedk">余额抵扣</th>
				<th  class="sort-column sjynqyj">实际缴纳契约金</th>
				<th  class="sort-column createDate">报名时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="classesSignup">
			<tr>
				<td> <input type="checkbox" id="${classesSignup.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看约跑报名管理', '${ctx}/classessignup/classesSignup/form?id=${classesSignup.id}','85%', '85%')">
					${classesSignup.bmbh}
				</a></td>
				<td>
					${classesSignup.bmrname}
				</td>
				<td>
					${classesSignup.classesname}
				</td>
				<td>
					${classesSignup.ksrq}
				</td>
				<td>
					${classesSignup.bmzs}
				</td>
				<td>
					${classesSignup.lxsj}
				</td>
				<td>
					${classesSignup.qyj}
				</td>
				<td>
					${classesSignup.yedk}
				</td>
				<td>
					${classesSignup.sjynqyj}
				</td>
				<td>
					<fmt:formatDate value="${classesSignup.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<shiro:hasPermission name="classessignup:classesSignup:view">
						<a href="#" onclick="openDialogView('查看约跑报名管理', '${ctx}/classessignup/classesSignup/form?id=${classesSignup.id}','85%', '85%')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="classessignup:classesSignup:edit">
    					<a href="#" onclick="openDialog('修改约跑报名管理', '${ctx}/classessignup/classesSignup/form?id=${classesSignup.id}','85%', '85%')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="classessignup:classesSignup:del">
						<a href="${ctx}/classessignup/classesSignup/delete?id=${classesSignup.id}" onclick="return confirmx('确认要删除该约跑报名管理吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
		<!-- 分页代码 -->
	<table:page page="${page}"></table:page>
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>