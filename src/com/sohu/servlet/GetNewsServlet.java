package com.sohu.servlet;

import com.sohu.SohuNews;

import com.sohu.crawler.Crawler;
import com.sohu.crawler.LinkParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/**
 *   一个servlet类，用于jsp网页和web服务器的交互
 *   @author Bob Hu
 * 
 */
public class GetNewsServlet extends HttpServlet 
{
    SohuNews news = new SohuNews();
    Thread thread = null;
    List newsList = new ArrayList();
        
    /*   这种方法只能完成解析一个url页面中的所有链接，不能递归执行
    protected void processRequest( HttpServletRequest request, 
    		                       HttpServletResponse response )
                                   throws ServletException, IOException 
    {
    	//设定编码方式
        response.setContentType("text/html;charset=UTF-8");
        //生成解析器对象
        final LinkParser parser = new LinkParser();
        // 获取网页中输入的url入口地址
        final String url = request.getParameter("newsfield");  
        System.out.println(url+"入口");    //测试使用
        
        //启动线程，进行网页的解析
        thread = new Thread (new Runnable() 
        {
            public void run() 
            {
                parser.doParser(url);
            }
        } );
        
        thread.start();             
    }
*/
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, 
            HttpServletResponse response )
            throws ServletException, IOException 
    {
    	//设定编码方式
        response.setContentType("text/html;charset=UTF-8");
        //生成解析器对象
   //   final  Crawler crawler = new Crawler();
        // 获取网页中输入的url入口地址
        final String url = request.getParameter("newsfield");  
        System.out.println(url+"入口");    //测试使用
        
        //启动线程，进行网页的解析
        thread = new Thread (new Runnable() 
        {
            public void run() 
            {
                //crawler.crawling(new String[]{url});
            }
        } );
        
        thread.start();
        
        request.getSession().setAttribute("newsList", newsList);
        //网页重定位
       // response.sendRedirect("detail.jsp");
    }
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                         throws ServletException, IOException 
    {
        processRequest(request, response); 
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                          throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "Short description";
    }// </editor-fold>
}
