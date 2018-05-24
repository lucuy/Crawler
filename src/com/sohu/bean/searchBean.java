package com.sohu.bean;
import java.sql.*;

/**
 * 数据库检索类
 * @author Bob Hu
 *
 */
public class searchBean
{
	private Connection con = null; 
    ResultSet rs = null;
    Statement stmt_query;
    Statement stmt_update;
    
    /**
     * 构造函数，完成数据库的连接工作
     */
    public searchBean()
    {
         try 
         {
             String driverName = "com.mysql.jdbc.Driver";
//        	 String usr = "sa";
//             String pwd = "123456";
        	 String url = "jdbc:mysql://192.168.1.246:3303/u17portal";

             Class.forName(driverName);
             con = DriverManager.getConnection(url);
             stmt_query = con.createStatement();
             stmt_update = con.createStatement();            
         }
         catch(java.lang.ClassNotFoundException e)
         { 
         	   System.err.println("faq()L" + e.getMessage());
         }  
         catch(java.sql.SQLException e1)
         {
         	  System.err.println("faq():" + e1.getMessage());
         }
     }

    /**
     * 断开与数据库的连接
     */
     public void close()
     {
     	if (con != null) 
	    {
	        try 
	        {
	            con.close();
	        }
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        } 
	        finally 
	        {
	            con = null;
	        }
	    }
     }
     
    /**
     * 根据指定的sql语句返回查询后的数据集 
     * @param sql 待指定的sql语句
     * @return
     */
    public ResultSet executeQuery(String sql)
	{
	    rs = null;
	    try
	    {
	        rs=stmt_query.executeQuery(sql);
	    }
	    catch(SQLException ex)
	    {
	   	    System.err.println("aq.executeQuery:" + ex.getMessage());
	    }
	    return rs;
	}
	
    /**
     * 字符编码转换，主要解决乱码问题
     * @param str
     * @return
     */
	public String strtochn(String str)
	{
		   byte[] byte1=str.getBytes();
		   String tmp="";
		   try
		   {
		   	    tmp= new String(byte1,"8859_1");
		   }
		   catch(Exception e)
		   {
		   	   System.err.println("strToChn异常" + e.getMessage() );   
		   }
		   return tmp;
	}
	
	/**
	 * 执行sql语句
	 * @param sql
	 * @return
	 */
	public boolean executeUpdate( String sql )
	{
		   try
		   {
		   	   stmt_update.executeUpdate( sql );
		   	   return true;
		   }
		   catch( SQLException e )
		   {
		       System.err.println( " aq.excuteUpdate: " + e.getMessage() );	
		   }
		   return false;
	}
}



