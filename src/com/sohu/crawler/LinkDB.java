package com.sohu.crawler;

import java.util.HashSet;

import java.util.Set;

/**
 *  用来保存已经访问的 url 和待爬取的 url 的类，提供url出队入队操作。
 *  @author Bob Hu
 */
public class LinkDB 
{

    //定义一个set集合，存放已经访问的url
    private static Set<String> visitedUrl = new HashSet<String>();
    //定义一个队列，存放待访问的url
    private static Queue<String> unVisitedUrl = new Queue<String>();

    public static Queue<String> getUnVisitedUrl() 
    {
        return unVisitedUrl;
    }
    
    public static void addVisitedUrl(String url) 
    {
        visitedUrl.add(url);
    }

    public static void removeVisitedUrl(String url) 
    {
        visitedUrl.remove(url);
    }

    public static String unVisitedUrlDeQueue() 
    {
        return unVisitedUrl.deQueue();
    }

    /**
     * 将url添加到待访问的队列中
     * @param url
     */
    public static void addUnvisitedUrl(String url) 
    {
    	//保证url非空，且集合中不能有重复的url
        if ( url != null 
             && !url.trim().equals("") 
        	 && !visitedUrl.contains(url) 
        	 && !unVisitedUrl.contians(url) ) 
        {
            unVisitedUrl.enQueue(url);
        }
    }

    public static int getVisitedUrlNum() 
    {
        return visitedUrl.size();
    }

    public static boolean unVisitedUrlsEmpty() 
    {
        return unVisitedUrl.empty();
    }
    
    public static void clearUnVisitedUrl()
    {
    	unVisitedUrl.clear();
    }
    
    public static void clearVisitedUrl()
    {
    	visitedUrl.clear();
    }
}

