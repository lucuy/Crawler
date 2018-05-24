<html>
	<body>
		
		<%@ page contentType="text/html;charset=gb2312"%>
		<%@ page language="java" import="java.sql.*"%>
	    <%@ page import="com.microsoft.sqlserver.jdbc.SQLServerDriver" %>
		<jsp:useBean id="data" class="com.sohu.bean.searchBean" scope="request" />
		
		
						                      <%-- 以下部分实现检索结果的分页显示 --%>
		<%	
		     //获取检索关键字和检索出的总记录条数	     
		     String searchName = (String)application.getAttribute("searchName");
		     Integer totalSortedNum  = (Integer)application.getAttribute("totalSortedNum");		
		     
		     //字符编码转换
		     String transSearchName = new String(searchName.getBytes("ISO-8859-1"));
		         
		     //每一页有10条记录  
		     int pages = 0;
		     int pageSize = 10;
		     //数据集
		     ResultSet sortedRS;
		     String space = "&nbsp;&nbsp;&nbsp";    //定义空格所代表的字符串
		     String pages_str = request.getParameter("pages");
		     
		     if (pages_str == null )
		     {
		         pages_str = "0";
		     }
		     
		     pages = new Integer(pages_str).intValue();
		     
		     //该页的第一条数据
		     String bottom;
		     //该页的最后一条数据
		     String top;
		     //执行的sql语句
		     String sql = "";
		     
		     //计算出当前页面的起始记录编号和末位记录编号
		     bottom = String.valueOf( pages * pageSize + 1 );
		     top =    String.valueOf( pages * pageSize +pageSize );
		     
		     //根据起始记录编号和末位记录编号，从数据库中检索出对应的记录
		     sql = "select * from dbo.sortedNews where newsid>=" + bottom +" and newsid<=" + top;
		     
		     //防止边界问题，如：totalSortedNum=103,则最后一页应该为显示的记录编号应该为101-103，而非101-110
		     String checkedTop = Integer.valueOf(top).intValue() < totalSortedNum.intValue() ? top : totalSortedNum.toString() ;
		     
		     //显示总共检索出的记录条数
		     out.print("总共检索出 <b>" + totalSortedNum + " </b>条记录，");
		     		     
		     //当前页面显示的记录编号范围
		     if (totalSortedNum != 0)
		     {
		         out.print("以下是第 <b>" + bottom + "</b>-<b>" + checkedTop + "</b> 条记录");
		     }
		     %>   <%=space%><%=space%><%=space%><%=space%><%=space%><%=space%><%=space%><%=space%>
		          <a href="sort.jsp?searchName=<%=transSearchName%>&sorted_type=1">按发表日期先后排序</a><br><br>      <%
		     
		     //执行sql语句
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
		         		        
		         //将标题的"检索关键字"用红色显示
		         String outStr= newstitle.replace(transSearchName,"<font color=\"red\">" + transSearchName+ "</font>");
		     
		         %>  <a href="display.jsp?title=<%=newstitle%>&searchName=<%=transSearchName%>"> <%= outStr %>  </a> 
	              <br> <%= newsauthor%>  <%= space %> 
	              发表时间： <%= newsdate %> <br> url: <font color="green"><b> <%= newsurl %></b> </font><br><br>
		         <%
		     }
		     
		     //计算页数与除以10后留下的页数
		     int total_page;
		     
		     //count(*)取得记录集
		     sql = "select ceiling(count(*)/" + pageSize + ") as count1 from dbo.sortedNews"; 		     
		     sortedRS = data.executeQuery( sql );
		     
		     //该语句必须使用，否则将有异常，提示找不到当前行
		     sortedRS.next();
		     total_page = sortedRS.getInt( 1 );
		     %>
		     
		     <%-- 格式控制，分页链接居中 --%>
		     <div align=center>
		     <%
		     for (int i=0; i<=total_page; i++)
		     {
		         String N_url = "<a href=dividePage.jsp?pages=" + i + ">", url1= "</a>";
		         out.print( N_url + "第" + (i + 1) + "页" + url1 + space );
		     } 
		     %>
		    </div>
		    
		    <%
		    sortedRS.close();
		%>
		
	</body>
</html>