<html>
	<body>
		
		<%@ page contentType="text/html;charset=gb2312"%>
		<%@ page language="java" import="java.sql.*"%>
	  <%@ page import="com.microsoft.sqlserver.jdbc.SQLServerDriver" %>
		<jsp:useBean id="data" class="com.sohu.bean.searchBean" scope="request" />
		

		<%! String searchName = null; %>
		
								                      <%-- 以下部分实现"搜索引擎的检索功能" --%>
		<%

        //检索时的排序类型
		    int sorted_type=0;
		    
		    //获取输入的检索关键字
		    searchName=request.getParameter("searchName");
		    String str_sorted_type = request.getParameter("sorted_type");
		    
		    if (str_sorted_type != null)
		    {
		        sorted_type = Integer.valueOf(str_sorted_type).intValue();  
		    }
		    
		    data.executeUpdate("truncate table dbo.sortedNews");
		    ResultSet RS = data.executeQuery("Select * FROM dbo.news");		    		     
		    		    
		    int counter = 0;
		    while( RS.next() )
		    {
		        //从数据库获取相应字段
		        String newstitle = data.strtochn(RS.getString("newstitle"));			        
	          String newsauthor = data.strtochn(RS.getString("newsauthor"));
	          String newscontent = data.strtochn(RS.getString("newscontent"));
            String newsurl = data.strtochn(RS.getString("newsurl"));
            String newsdate = data.strtochn(RS.getString("newsdate"));
            
            //去除字符串中多余的空格
            searchName = searchName.trim();
            
            //进行字符串的匹配
            Integer isTitleFind =newstitle.indexOf(searchName);
            Integer isContentFind = newscontent.indexOf(searchName);
            
            //字符编码的转换
            String transtitle = new String(newstitle.getBytes("ISO-8859-1"));
            String transAuthor = new String(newsauthor.getBytes("ISO-8859-1"));
            String transContent = new String(newscontent.getBytes("ISO-8859-1"));
            String transUrl = new String(newsurl.getBytes("ISO-8859-1"));
            String transDate = new String(newsdate.getBytes("ISO-8859-1"));
            String transDate_int = transDate.replaceAll("[^0-9]", "");
	          
	          
	          if ( (isTitleFind != -1 || isContentFind != -1) && !searchName.trim().equals("") )
	          {
	             //将匹配的记录写入sortedNews表中
	             String sql = "insert into dbo.sortedNews(newstitle,newsauthor,newscontent,newsurl,newsdate,newsdate_int)";
	             sql += " values('";
	             sql += transtitle;
	             sql += "','";
	             sql += transAuthor;
	             sql += "','";
	             sql += transContent;
	             sql += "','";
	             sql += transUrl;
	             sql += "','";
	             sql += transDate;
	             sql += "','";
	             sql += transDate_int;
	             sql += "')";
	             
	            data.executeUpdate(sql);   
	            counter ++;
	          }
		    }
		    
		    //数据库记录排列
		    if (sorted_type == 1)
		    {
				    ResultSet tmpRS = data.executeQuery("select * from dbo.sortedNews order by newsdate_int");   
				    data.executeUpdate("truncate table dbo.sortedNews");
				    
				    while( tmpRS.next() )
				    {
				        //从数据库获取相应字段
				        String newstitle = data.strtochn(tmpRS.getString("newstitle"));			        
			          String newsauthor = data.strtochn(tmpRS.getString("newsauthor"));
			          String newscontent = data.strtochn(tmpRS.getString("newscontent"));
		            String newsurl = data.strtochn(tmpRS.getString("newsurl"));
		            String newsdate = data.strtochn(tmpRS.getString("newsdate"));
		            
		            //去除字符串中多余的空格
		            searchName = searchName.trim();
		            
		            //进行字符串的匹配
		            Integer isTitleFind =newstitle.indexOf(searchName);
		            Integer isContentFind = newscontent.indexOf(searchName);
		            
		            //字符编码的转换
		            String transtitle = new String(newstitle.getBytes("ISO-8859-1"));
		            String transAuthor = new String(newsauthor.getBytes("ISO-8859-1"));
		            String transContent = new String(newscontent.getBytes("ISO-8859-1"));
		            String transUrl = new String(newsurl.getBytes("ISO-8859-1"));
		            String transDate = new String(newsdate.getBytes("ISO-8859-1"));
		            String transDate_int = transDate.replaceAll("[^0-9]", "");
			          
			         	         
		            String sql = "insert into dbo.sortedNews(newstitle,newsauthor,newscontent,newsurl,newsdate,newsdate_int)";
		            sql += " values('";
		            sql += transtitle;
		            sql += "','";
		            sql += transAuthor;
		            sql += "','";
		            sql += transContent;
		            sql += "','";
		            sql += transUrl;
		            sql += "','";
		            sql += transDate;
		            sql += "','";
		            sql += transDate_int;
		            sql += "')";
		           
		           data.executeUpdate(sql);   	          
				    }
				}
			 
		    

		    
		    //传递参数给dividePage.jsp
		    application.setAttribute("searchName",searchName);
		    application.setAttribute("totalSortedNum",counter);
        
		%>
		
    <jsp:forward page="dividePage.jsp"/>
		
	</body>
</html>