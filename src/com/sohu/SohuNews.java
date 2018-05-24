package com.sohu;

import com.sohu.bean.NewsBean;

import com.sohu.db.ConnectionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.filters.NodeClassFilter; 

/**
 * ����ָ��Url��ץȡ����
 * 
 */
public class SohuNews 
{

    private Parser parser = null;                //���������ҳ�Ķ���
    private List newsList = new ArrayList();     //���ڴ���ֶ���Ϣ������
    private NewsBean bean = new NewsBean();      //�����ݿ��ֶζ�Ӧ��JavaBean
    private ConnectionManager manager = null;    //���ݿ�������
    private PreparedStatement pstmt = null;      //ִ��sql�����Ҫʹ�õĶ���

    /**
     * ���캯��
     */
    public SohuNews() 
    {
    	//��ԭ���ݿ��е��������
    	initDB();
    }

    /**
     * ��JavaBean�е��ֶζ�Ӧ��ӵ�������
     * @param newsBean
     * @return
     */
    public List getNewsList(final NewsBean newsBean) 
    {
        List list = new ArrayList();
        
        String newsTitle   = newsBean.getNewsTitle();
        String newsAuthor  = newsBean.getNewsAuthor();
        String newsContent = newsBean.getNewsContent(); 
        String newsURL     = newsBean.getNewsURL();
        String newsDate    = newsBean.getNewsDate();
        
        
        list.add(newsTitle);
        list.add(newsAuthor);
        list.add(newsContent);
        list.add(newsURL);
        list.add(newsDate);
        
        return list;
    }

    /**
     *  �趨JavaBean�и��ֶε�ֵ
     * @param newsTitle   ���ű������Ƣ�
     * @param newsauthor  ���α༭
     * @param newsContent ��������
     * @param newsDate    ��������
     * @param url         ��ҳ���ӵ�ַ
     */
    public void setNews( String newsTitle, 
    		             String newsAuthor, 
    		             String newsContent,
    		             String URL, 
    		             String newsDate ) 
    {
        bean.setNewsTitle(newsTitle);
        bean.setNewsAuthor(newsAuthor);
        bean.setNewsContent(newsContent);
        bean.setNewsURL(URL);
        bean.setNewsDate(newsDate);
    }

    /**
     * ��JavaBean�е��ֶ�д�뵽���ݿ���
     */
    protected void newsToDataBase() 
    {

        //�����߳�ִ��д�����ݿ�Ĳ�������Ϊ��Ҫ֧�ֶ��û���
        Thread thread = new Thread( new Runnable()
        {

            public void run() 
            {
                boolean sucess = saveToDB(bean);
                if (sucess != false) 
                {
                    System.out.println("д���ݿ����ʧ�ܣ�");
                }
            }
        } );
        //�����߳�
        thread.start();
    }

    /**
     *  ������ݿ�ԭ�����ݣ������Զ���ŵ���ʼֵ��Ϊ1
     */
    public void initDB()
    {
    	String sql = "truncate table news";
    	manager = new ConnectionManager();
    	
    	try 
        {
        	//�����ݿ⽨������
            pstmt = manager.getConnection().prepareStatement(sql);
            pstmt.execute();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally 
        {
            try 
            {
                pstmt.close();
                manager.close();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    /**
     * д���ݿ������ʵ�֣�sql����ִ�У�
     * @param bean
     * @return
     */
    public boolean saveToDB(NewsBean bean) 
    {
        boolean flag = true;
        String sql = "insert into news(newsTitle,newsAuthor,newsContent,newsURL,newsDate) values(?,?,?,?,?)";
        manager = new ConnectionManager();
        String titleLength = bean.getNewsTitle();
        //�����ű��ⳤ�ȵ�����
        if (titleLength.length() > 60)     
        { 
            return flag;
        }
        try 
        {
        	//�����ݿ⽨������
            pstmt = manager.getConnection().prepareStatement(sql);
            
            //��JavaBean�л�ȡ�ֶΣ�д�����ݿ���
            pstmt.setString(1, bean.getNewsTitle());
            pstmt.setString(2, bean.getNewsAuthor());
            pstmt.setString(3, bean.getNewsContent());
            pstmt.setString(4, bean.getNewsURL());
            pstmt.setString(5, bean.getNewsDate());
            flag = pstmt.execute();

        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally 
        {
            try 
            {
                pstmt.close();
                manager.close();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return flag;
    }

    /**
     * ����ҳ�ϻ�ȡ���ŵı���
     * @param titleFilter
     * @param parser
     * @return
     */
    private String getTitle(NodeFilter titleFilter, Parser parser) 
    {
        String titleName = "";
        try 
        {
            //����titleFilter�ķ������ҳ���й���
            NodeList titleNodeList = (NodeList) parser.parse(titleFilter);
            //��ȡ���ű����ַ���
            for ( int i = 0; i < titleNodeList.size(); i++) 
            {   
                HeadingTag title = (HeadingTag) titleNodeList.elementAt(i);
                titleName = title.getStringText();
            }

        } 
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return titleName;
    }

    /**
     * ��ȡ���ŵ����α༭
     * @param newsauthorFilter
     * @param parser
     * @return
     */
    private String getNewsAuthor(NodeFilter newsauthorFilter, Parser parser) 
    {
        String newsAuthor = "";
        try 
        {
        	//����newsauthorFilter�ķ������ҳ���й���
            NodeList authorList = (NodeList) parser.parse(newsauthorFilter);
            
            //��ȡ���α༭�ַ���
            for (int i = 0; i < authorList.size(); i++) 
            {   
                Div authorSpan = (Div) authorList.elementAt(i);
                newsAuthor = authorSpan.getStringText();
            }

        } 
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newsAuthor;

    }

    /*
     * ��ȡ����ʱ��
     */
    private String getNewsDate(NodeFilter dateFilter, Parser parser) 
    {
        String newsDate = "";
        try 
        {
            NodeList dateList = (NodeList) parser.parse(dateFilter);
            for (int i = 0; i < dateList.size(); i++) 
            {
            	Div dateTag =  (Div)dateList.elementAt(i);
                newsDate = dateTag.getStringText();
            }
        } 
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newsDate;
    }

    /**
     * ��ȡ��������
     * @param newsContentFilter
     * @param parser
     * @return content 
     */
    private String getNewsContent(NodeFilter newsContentFilter, Parser parser) 
    {
        String content = "";
        StringBuilder builder = new StringBuilder();

        try 
        {
        	//����ָ���Ĺ��˷�������ҳ
            NodeList newsContentList = (NodeList) parser.parse(newsContentFilter);
            //  System.out.println(newsContentList.size());     ����ʹ��
            
            //��ȡ���ĵ�����
            for (int i = 0; i < newsContentList.size(); i++) 
            {
            	Div newsContenTag = (Div) newsContentList.elementAt(i);
                builder = builder.append(newsContenTag.getStringText());
            }
            content = builder.toString();  //����ת����
         
            
            if (content != null) 
            {  
                parser.reset();
                //ѡ����뷽ʽ
                parser = Parser.createParser(content, "gb2312");
                //����paser�����нڵ㣬��ȡ���������
                StringBean sb = new StringBean();
                sb.setCollapse(true);
                parser.visitAllNodesWith(sb);
                content = sb.getStrings();
               
                //��ȥ���������е�һЩ�̶���ʽ�������滻Ϊ��
                if ( content != null )
                {
                    content = content.replaceAll("\\\".*[a-z].*\\}", "");           
                    content = content.replace("[����˵����]", "");
                }


            } 
            else 
            {
               System.out.println("û�еõ��������ݣ�");
            }

        } 
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }
    
    /**
     * ��ָ����url���з�������ȡ���е����ű��⡢���α༭�����ߡ��������ڡ���������
     * @param url ָ����url
     */
    public void parser(String url) 
    {
        try {
            parser = new Parser(url);
            
            //���������
            NodeFilter titleFilter = new TagNameFilter("h1");
                   
            //���Ĺ�����
            NodeFilter contentFilter1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "contentText"));
            NodeFilter contentFilter2 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "explain"));
            NodeFilter contentFilter = new OrFilter(contentFilter1,contentFilter2);
          //NodeFilter contentFilter = new TagNameFilter("textarea");    ���ƻƺ� �ķ���
                    
            //�������ڹ�����
            NodeFilter newsdateFilter1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "sourceTime"));
            NodeFilter newsdateFilter2 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "r"));
            NodeFilter newsdateFilter = new AndFilter(newsdateFilter2, new HasParentFilter(newsdateFilter1));
            
            //���߹�����
            NodeFilter newsauthorFilter1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "editUsr"));
            NodeFilter newsauthorFilter2 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "editor"));
            NodeFilter newsauthorFilter = new OrFilter(newsauthorFilter1,newsauthorFilter2);
            
            String newsTitle = getTitle(titleFilter, parser);
            System.out.println(newsTitle);
            parser.reset();                    //���ý�����
            String newsContent = getNewsContent(contentFilter, parser);
            System.out.println(newsContent);   //������
            
            parser.reset();
            String newsDate = getNewsDate(newsdateFilter, parser);
            System.out.println(newsDate);      //������
            parser.reset();
            String newsauthor = getNewsAuthor(newsauthorFilter, parser);
            System.out.println(newsauthor);    //������
        
            

            //���������ϻ�ȡ���ֶ�д�뵽newsBean��
            setNews(newsTitle, newsauthor, newsContent, newsDate, url);
                               
            //��newsBean�е��ֶ�д�뵽���ݿ���
            this.newsToDataBase();
            
        }
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*  ���Խ������ƻƺ׵Ĳ���
    public void parser(String url)
    {
    	try
    	{
    		Parser parser=new Parser(url); 
    		NodeFilter filter = new NodeClassFilter(TableTag.class);
    		NodeList list=parser.extractAllNodesThatMatch(filter); 
            if(list.elementAt(0) instanceof TableTag)//����ȡ����һ������������ҳ�ж��ǲ���Ϊ���˿����Լ�����, ������ֿ�ָ�� 
            { 
                TableTag table=(TableTag) list.elementAt(1);//�����ж�����õ��ĵ�һ��Ϊtable 
                TableRow [] tr=table.getRows();//�õ���table���е�tr 
                for(TableRow r:tr)//��������tr 
                { 
                    TableColumn [] tc=r.getColumns(); 
                    for(TableColumn c:tc)//�������е�td 
                    { 
                        System.out.print(c.getStringText() + "@@@"); 
                    } 
                } 
            } 

    	}
        catch (ParserException ex) 
        {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
    
    //����parser�Ĺ���
    public static void main(String[] args) {
        SohuNews news = new SohuNews();
        news.parser("http://news.sina.com.cn/zl/zatan/blog/2015-08-10/08474244/1300857112/4d8981180102vmhi.shtml");   
    }
}
