package com.sohu.crawler;

import com.sohu.SohuNews;
import java.util.HashSet;
import java.util.Set;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 *  解析网页中存在的连接 
 *  @author Bob Hu
 *  @note 该类仅仅解析指定的url页面中存在的符合过滤条件的链接
 */
public class LinkParser 
{   
	/**
	 * 从指定的url中解析出所有存在的连接，并将其存放在一个set集合中
	 * @param url
	 * @param filter
	 * @return
	 */
    public static Set<String> extracLinks(String url, LinkFilter filter) 
    {
        Set<String> links = new HashSet<String>();
        try 
        {   
            //创建一个解析器对象，传入url地址
            Parser parser = new Parser(url);
            //设定编码方式
            parser.setEncoding("gb2312");
            
            //过滤 <frame> 标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
            NodeFilter frameFilter = new NodeFilter() 
            {   
            	//设定匹配的法则，该节点是否以"frame src="开头
                public boolean accept(Node node) 
                {
                    if (node.getText().startsWith("frame src=")) 
                    {
                        return true;
                    }
                    else 
                    {
                        return false;
                    }
                }
            };
           //OrFilter 来设置过滤 <a> 标签和 <frame> 标签，两个标签是 or 的关系
            OrFilter linkFilter = new OrFilter( new NodeClassFilter(LinkTag.class),
            		                            frameFilter );
            // 得到所有经过过滤的标签
            NodeList list = parser.extractAllNodesThatMatch(linkFilter);
            for (int i = 0; i < list.size(); i++) 
            {
                Node tag = list.elementAt(i);
                if (tag instanceof LinkTag)   //即<a>标签
                {
                    LinkTag link = (LinkTag) tag;
                    String linkUrl = link.getLink();  //url
                    //System.out.println(linkUrl);    //测试用
                    
                    //将符合过滤条件的链接添加到set集合中，供后面的解析使用
                    if (filter.accept(linkUrl)) 
                    {
                        links.add(linkUrl);
                    }
                } 
                else    // <frame>标签
                {
                	//提取 frame 里 src 属性的链接如 <frame src="xxx.html"/>
                    String frame = tag.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);
                    int end = frame.indexOf(" ");
                    if (end == -1) 
                    {
                        end = frame.indexOf(">");
                    }
                    String frameUrl = frame.substring(5, end - 1);
                    
                    //将符合过滤条件的链接添加到set集合中，供后面的解析使用
                    if (filter.accept(frameUrl)) 
                    {
                        links.add(frameUrl);
                    }
                }
            }
        } 
        catch (ParserException e) 
        {
            e.printStackTrace();
        }
        return links;
    }
    /**
     * 解析指定url操作的实现
     * 该函数中调用了本类中extracLinks函数抽取网页中的链接，然后调用了SohuNews类中的parse函数，
     * 提取指定链接中的字段信息，从而完成抓取的任务
     * @param url
     */
    public void doParser(String url) 
    {
        SohuNews news = new SohuNews();
        Set<String> links = LinkParser.extracLinks(url, new LinkFilter() 
        {
        	//设置过滤链接的条件
            public boolean accept(String url) 
            {
            	//http://www.byhh.net/cgi-bin/bbstcon?board=EI&file=M.1265631155.A&start=1798
                if (url.matches("http://news.sohu.com/[\\d]+/n[\\d]+.shtml")) 
            	//if (url.matches("http://www.byhh.net/cgi-bin/bbstdoc\\?board=EI&start=[0-9]+")) 
            	{
                    return true;
                }
            	else 
            	{
                    return false;
                }
            }
        } );
        
        //执行完extracLinks函数后，set集合中已经存放了我们需要进行解析的url
        //下面遍历该set集合，对里面所有的url进行解析
        for (String link : links) 
        {
            System.out.println(link);  //测试使用
            news.parser(link);         //去掉以后即不再写数据库，直接打印到控台上
        }
    }

    //主函数，单独测试时使用
    public static void main(String[] args) 
    {
        String url = "http://news.sohu.com/";
        LinkParser parser = new LinkParser();
        parser.doParser(url);

    }
}

