package com.sohu.crawler;

/**
 *  一个接口，实现其 accept() 方法用来对抽取的链接进行过滤。
 *  @author Bob Hu
 */
public interface LinkFilter 
{
	public boolean accept(String url);
}