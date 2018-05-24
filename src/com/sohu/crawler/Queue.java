package com.sohu.crawler;

import java.util.LinkedList;

/**
 *  实现了一个简单的队列，在 LinkDb.java 中使用了此类。
 *  @author Bob Hu
 */
public class Queue<T> 
{
    private LinkedList<T> queue = new LinkedList<T>();
    
    public void clear()
    {
    	queue.clear();
    }

    public void enQueue(T t) 
    {
        queue.addLast(t);
    }

    public T deQueue() 
    {
        return queue.removeFirst();
    }

    public boolean isQueueEmpty() 
    {
        return queue.isEmpty();
    }

    public boolean contians(T t) 
    {
        return queue.contains(t);
    }

    public boolean empty() 
    {
        return queue.isEmpty();
    }
}
