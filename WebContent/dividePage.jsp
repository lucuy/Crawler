<html>
	<body>
		
		<%@ page contentType="text/html;charset=gb2312"%>
		<%@ page language="java" import="java.sql.*"%>
	    <%@ page import="com.microsoft.sqlserver.jdbc.SQLServerDriver" %>
		<jsp:useBean id="data" class="com.sohu.bean.searchBean" scope="request" />
		
		
						                      <%-- ���²���ʵ�ּ�������ķ�ҳ��ʾ --%>
		<%	
		     //��ȡ�����ؼ��ֺͼ��������ܼ�¼����	     
		     String searchName = (String)application.getAttribute("searchName");
		     Integer totalSortedNum  = (Integer)application.getAttribute("totalSortedNum");		
		     
		     //�ַ�����ת��
		     String transSearchName = new String(searchName.getBytes("ISO-8859-1"));
		         
		     //ÿһҳ��10����¼  
		     int pages = 0;
		     int pageSize = 10;
		     //���ݼ�
		     ResultSet sortedRS;
		     String space = "&nbsp;&nbsp;&nbsp";    //����ո���������ַ���
		     String pages_str = request.getParameter("pages");
		     
		     if (pages_str == null )
		     {
		         pages_str = "0";
		     }
		     
		     pages = new Integer(pages_str).intValue();
		     
		     //��ҳ�ĵ�һ������
		     String bottom;
		     //��ҳ�����һ������
		     String top;
		     //ִ�е�sql���
		     String sql = "";
		     
		     //�������ǰҳ�����ʼ��¼��ź�ĩλ��¼���
		     bottom = String.valueOf( pages * pageSize + 1 );
		     top =    String.valueOf( pages * pageSize +pageSize );
		     
		     //������ʼ��¼��ź�ĩλ��¼��ţ������ݿ��м�������Ӧ�ļ�¼
		     sql = "select * from dbo.sortedNews where newsid>=" + bottom +" and newsid<=" + top;
		     
		     //��ֹ�߽����⣬�磺totalSortedNum=103,�����һҳӦ��Ϊ��ʾ�ļ�¼���Ӧ��Ϊ101-103������101-110
		     String checkedTop = Integer.valueOf(top).intValue() < totalSortedNum.intValue() ? top : totalSortedNum.toString() ;
		     
		     //��ʾ�ܹ��������ļ�¼����
		     out.print("�ܹ������� <b>" + totalSortedNum + " </b>����¼��");
		     		     
		     //��ǰҳ����ʾ�ļ�¼��ŷ�Χ
		     if (totalSortedNum != 0)
		     {
		         out.print("�����ǵ� <b>" + bottom + "</b>-<b>" + checkedTop + "</b> ����¼");
		     }
		     %>   <%=space%><%=space%><%=space%><%=space%><%=space%><%=space%><%=space%><%=space%>
		          <a href="sort.jsp?searchName=<%=transSearchName%>&sorted_type=1">�����������Ⱥ�����</a><br><br>      <%
		     
		     //ִ��sql���
		     sortedRS = data.executeQuery( sql );
		     
		     String newstitle;
		     String newsauthor;
		     String newsdate;
		     String newsurl;
		     while (sortedRS.next())
		     {
		         newstitle = sortedRS.getString("newstitle");
		         newsauthor = sortedRS.getString("newsauthor");
		         newsdate = sortedRS.getString("newsdate");
		         newsurl = sortedRS.getString("newsurl");
		         		        
		         //�������"�����ؼ���"�ú�ɫ��ʾ
		         String outStr= newstitle.replace(transSearchName,"<font color=\"red\">" + transSearchName+ "</font>");
		     
		         %>  <a href="display.jsp?title=<%=newstitle%>&searchName=<%=transSearchName%>"> <%= outStr %>  </a> 
	              <br> <%= newsauthor%>  <%= space %> 
	              ����ʱ�䣺 <%= newsdate %> <br> url: <font color="green"><b> <%= newsurl %></b> </font><br><br>
		         <%
		     }
		     
		     //����ҳ�������10�����µ�ҳ��
		     int total_page;
		     
		     //count(*)ȡ�ü�¼��
		     sql = "select ceiling(count(*)/" + pageSize + ") as count1 from dbo.sortedNews"; 		     
		     sortedRS = data.executeQuery( sql );
		     
		     //��������ʹ�ã��������쳣����ʾ�Ҳ�����ǰ��
		     sortedRS.next();
		     total_page = sortedRS.getInt( 1 );
		     %>
		     
		     <%-- ��ʽ���ƣ���ҳ���Ӿ��� --%>
		     <div align=center>
		     <%
		     for (int i=0; i<=total_page; i++)
		     {
		         String N_url = "<a href=dividePage.jsp?pages=" + i + ">", url1= "</a>";
		         out.print( N_url + "��" + (i + 1) + "ҳ" + url1 + space );
		     } 
		     %>
		    </div>
		    
		    <%
		    sortedRS.close();
		%>
		
	</body>
</html>