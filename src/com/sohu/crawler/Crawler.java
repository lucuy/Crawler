package com.sohu.crawler;

import com.sohu.SohuNews;
import java.util.Set;

import com.sohu.*;

/**
 *   ͨ������url��ַ����ɵݹ�������ӵĹ��� 
 *   @note ����ͨ��ʹ��LinkDB�࣬����url���еĹ���
 *         ͨ��ʹ��LinkParser�࣬ʵ��ָ��url��ַ��ҳ�����
 *         �Ӷ����������url�ĵݹ����
 *   @author Bob Hu
 */
public class Crawler 
{   
    SohuNews news = new SohuNews();
    CrawlerUI crawlerUI = null;

    public Crawler(CrawlerUI crawlerUI)
    {
    	this.crawlerUI = crawlerUI;
    }
    public Crawler()
    {
    	
    }
    /**
     * ������ڵ�url��ַ�����������δ���ʶ�����
     * @param seeds
     */
    private void initCrawlerWithSeeds(String[] seeds) 
    {
    	//�ѷ��ʶ��к�δ���ʶ��еĳ�ʼ������ղ�����
    	LinkDB.clearUnVisitedUrl();
    	LinkDB.clearVisitedUrl();
    	
        for (int i = 0; i < seeds.length; i++) 
        {
            LinkDB.addUnvisitedUrl(seeds[i]);
        }
    }

    /**
     * ��ָ��url����ץȡ�ľ���ʵ�֣����������������֣�
     * 1����ȡ��ҳ�з��Ϲ������������ӵ�ַ���ݹ���ȡ��
     * 2���������˳�����url��ַ����ȡ��Ҫ���ֶ�
     * @param seeds   ��ʼ����url��ַ
     */
    public void crawling(String[] seeds) 
    {
        LinkFilter filter = new LinkFilter() 
        {
        	//���÷��Ϲ���url�Ĺ���
            public boolean accept(String url) 
            {
                if (url.matches("http://news.sina.com/[\\d]+/n[\\d]+.shtml")) 
                {
                    return true;
                }
                else 
                {
                    return false;
                }
            }
        };
      
        //����ڵ�url���뵽�����ʶ����У�Ϊ���濪ʼ������׼��
        initCrawlerWithSeeds(seeds);

        // whileѭ��ʵ�ֵݹ������ҳ���ӵĹ��ܣ�ͨ��ѭ�����������趨�ݹ�����
        while (!LinkDB.unVisitedUrlsEmpty() && LinkDB.getVisitedUrlNum() < 1) 
        {       
            //�Ӵ����ʶ���ȡ��url��ַ��׼������
            String visitUrl = LinkDB.unVisitedUrlDeQueue();
            
            //�����ȥ��url��ַΪ�գ���������������������һ��url��ַ
            if (visitUrl == null) 
            {
                continue;
            }
            
           //��¼���ѷ��ʵ�url��ַ
            LinkDB.addVisitedUrl(visitUrl);
            
            // ��ȡ��urlҳ���д��ڵ�����
            Set<String> links = LinkParser.extracLinks(visitUrl, filter);
            
            //������url�����е�����
            for (String link : links) 
            {
            	//��url�е�ÿһ�����ӷ��뵽�����ʶ����У��Ӷ�ʵ�ֵݹ����
                LinkDB.addUnvisitedUrl(link);
                //��ȡ��url��������ֶ���Ϣ
                news.parser(link);
                //System.out.println(link);     //������
                crawlerUI.crawlingUrl.setText(link);
                crawlerUI.crawlingNum++;                
            }
        }
    }

    // ����������������ʱʹ��
    public static void main(String[] args) 
    {
         Crawler crawler = new Crawler();
         crawler.crawling(new String[]{"http://news.sina.com"});
    }
}

