<html>
	<body>
		<%@ page language="java" import="java.sql.*" %>
		<%@ page import="com.microsoft.sqlserver.jdbc.SQLServerDriver" %>
		<jsp:useBean id="searchBean" scope="application" class="com.sohu.bean.searchBean" />
			
			                                          <%-- 以下部分完成正文内容的显示 --%>
		<%		                                    
		//获取sort.jsp中检索出来的title
		String title = request.getParameter("title"); 
		//获取sort.jsp中的searchName，即用户输入的检索内容
		String searchName=request.getParameter("searchName"); 

    String sql="Select * FROM dbo.news where newstitle='";
    sql += title;
    sql += "'";
    
    //进行字符编码间的转换，否则不能从数据库中检索出数据
    String tansSql = new String(sql.getBytes("ISO-8859-1"));
    
    //通过title检索出正文内容
		ResultSet RS = searchBean.executeQuery(tansSql);
		
		//显示正文内容,并将"检索关键字"用红色显示
    if(RS.next())
    {
	 	    String textContent =  searchBean.strtochn(RS.getString("newscontent"));
	 	    //如果直接输出，则原有的正文格式将会丢失，因此需要处理
	 	    %>		<%=textContent.replace(" ","&nbsp;")
	 	                        .replace("\r\n","<br/>")
	 	                        .replace(searchName,"<font color=\"red\">" + searchName+ "</font>")%>    <%  
	  }
	  else
		{
	  	   out.print("没有正文内容");
		}
		%>
	</body>
</html>