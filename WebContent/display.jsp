<html>
	<body>
		<%@ page language="java" import="java.sql.*" %>
		<%@ page import="com.microsoft.sqlserver.jdbc.SQLServerDriver" %>
		<jsp:useBean id="searchBean" scope="application" class="com.sohu.bean.searchBean" />
			
			                                          <%-- ���²�������������ݵ���ʾ --%>
		<%		                                    
		//��ȡsort.jsp�м���������title
		String title = request.getParameter("title"); 
		//��ȡsort.jsp�е�searchName�����û�����ļ�������
		String searchName=request.getParameter("searchName"); 

    String sql="Select * FROM dbo.news where newstitle='";
    sql += title;
    sql += "'";
    
    //�����ַ�������ת���������ܴ����ݿ��м���������
    String tansSql = new String(sql.getBytes("ISO-8859-1"));
    
    //ͨ��title��������������
		ResultSet RS = searchBean.executeQuery(tansSql);
		
		//��ʾ��������,����"�����ؼ���"�ú�ɫ��ʾ
    if(RS.next())
    {
	 	    String textContent =  searchBean.strtochn(RS.getString("newscontent"));
	 	    //���ֱ���������ԭ�е����ĸ�ʽ���ᶪʧ�������Ҫ����
	 	    %>		<%=textContent.replace(" ","&nbsp;")
	 	                        .replace("\r\n","<br/>")
	 	                        .replace(searchName,"<font color=\"red\">" + searchName+ "</font>")%>    <%  
	  }
	  else
		{
	  	   out.print("û����������");
		}
		%>
	</body>
</html>