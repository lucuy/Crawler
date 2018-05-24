package com.sohu;

import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.util.*;
import java.io.IOException;

import com.sohu.crawler.*;
import com.sohu.*;

/**
 * 网络爬虫程序的用户界面类
 * @author Bob Hu
 *
 */
public class CrawlerUI extends JFrame
{
	//该线程供抓取网页使用
    static Thread thread = null;
    
    //一组静态文本，说明编辑框的作用
    public JLabel crawlingUrlLabel = null;
    public JLabel inputUrlLabel = null;
    public JLabel inputTimeIntervalLabel = null;
    public JLabel timeElapsceLabel = null;
    public JLabel crawlingNumLabel = null;
    
    //一组编辑框，提供用户输入
    public JTextField crawlingUrl = null;
    public static JTextField inputUrl = null;
    public static JTextField inputTimeInterval = null;
    public static JTextField timeElapsce = null;
    public static JTextField crawlingNumText = null;
    
    //一组按钮
    public static JButton startButton = null;
    public static JButton stopButton = null;
    public static JButton exitButton = null;
    
    //用户界面对象
    public static CrawlerUI crawlerUI =null;
    
    //两个定时器
    //定时器 timer：完成数据采集工作，实现定时采集功能
    //定时器 auxTimer: 定时刷新一个计数器，显示下一次"定时采集"的剩余时间
	public Timer timer = new Timer();	
	public Timer auxTimer = new Timer();
	
	//计数器，用于记录下一次"定时采集的时间"
	public static Integer counter = 0;
	public static boolean isCounterInit = false;
	
	//记录当前爬取的url数量
	public static Long crawlingNum = 0L; 
	
    public CrawlerUI()
    {
    	crawlerUI = this; 
    }
    
    /**
     *  "网络爬虫"界面创建函数
     */
    public void create()
    {
    	//标题栏
		JFrame frame = new JFrame("网络爬虫");
		//布局管理器
		frame.setLayout(new FlowLayout()); 
		
		//创建静态文本框
		crawlingUrlLabel =       new JLabel("正在抓取的 url: ");
		inputUrlLabel =          new JLabel("请输入需要抓取的入口url：");
		inputTimeIntervalLabel = new JLabel("请设定定时抓取间隔(s)：");
		timeElapsceLabel =       new JLabel("定时抓取剩余时间(s)：    ");
		crawlingNumLabel =       new JLabel("已爬取url条数(1s刷新):    "); 
		
		//创建供数据输入的编辑框
		inputUrl =          new JTextField("",25);
        inputTimeInterval = new JTextField("",10);    
        crawlingUrl =       new JTextField("",25);  crawlingUrl.setEditable(false); //不能编辑
    	timeElapsce =       new JTextField("",10);  timeElapsce.setEditable(false);
    	crawlingNumText =   new JTextField("",10);  crawlingNumText.setEditable(false);
		
    	//创建按钮
	    startButton =  new JButton("开始");		
	    stopButton =   new JButton("停止"); stopButton.setEnabled(false);  //禁用该按钮
	    exitButton =   new JButton("退出");

        //添加按钮的消息响应
	    startButton.addActionListener(new Listener_ok());
	    stopButton.addActionListener(new Listener_cancel());		    
	    exitButton.addActionListener(new Listener_exit());
		
	    //将所有的组件添加到界面
		frame.add(inputUrlLabel);
		frame.add(inputUrl);
		
		frame.add(crawlingUrlLabel);
		frame.add(crawlingUrl);
		
		frame.add(inputTimeIntervalLabel);
		frame.add(inputTimeInterval);
		
		frame.add(timeElapsceLabel);
		frame.add(timeElapsce);
		frame.add(crawlingNumLabel);
		frame.add(crawlingNumText);
		
		frame.add(startButton);		
		frame.add(stopButton);		
		frame.add(exitButton);
		
		//设置窗口的大小
		frame.setSize(320,280);
		
		//获取窗口的坐标
		Toolkit    tk  = Toolkit.getDefaultToolkit();	
		int Width  = tk.getScreenSize().width;
	    int Height = tk.getScreenSize().height;

	    //设置窗口在屏幕上的显示位置
	    frame.setLocation((Width - 320) / 2, (Height - 280) / 2);
	    
	    //点击标题栏上的"X"能够退出程序
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   
	    //不使能最大化按钮
	    frame.setResizable(false);
	    
	    //窗口创建后可见
		frame.setVisible(true);
    }    

    /**
     * 停止按钮监听类
     * @author Bob Hu
     *
     */
	class Listener_cancel implements ActionListener
	{
		//实现接口函数
		public void actionPerformed(ActionEvent e)
		{				
			thread.stop();
			
			//重新使能相应按钮
			startButton.setEnabled(true);
			inputUrl.setEnabled(true);
            inputTimeInterval.setEditable(true);
			
			//改变定时抓取计数器的初始化标志
			isCounterInit = false;
			crawlingNum = 0L;

			//使用这个方法退出任务
	        timer.cancel();
	        auxTimer.cancel();	        	
		}
	}
	
	/**
	 * 退出按钮监听类
	 * @author Bob Hu
	 *
	 */
	class Listener_exit implements ActionListener
	{
		//实现接口函数
		public void actionPerformed(ActionEvent e)
		{
			//程序退出
			System.exit(1);		
		}
	}
	
	/**
	 * 开始按钮监听类
	 * @author Bob Hu
	 *
	 */
    class Listener_ok implements ActionListener		
    {    	
    	public void actionPerformed(ActionEvent e)		
    	{
            //获取用户输入的定时间隔
    		String strTimeInterval = inputTimeInterval.getText();
    		
    		//获取输入的url
    		String inputContent = inputUrl.getText();
    		
    		//判断输入的url是否合法
    		boolean isInputUrlValid =( inputContent.equals("http://news.sohu.com") );
    		isInputUrlValid  = isInputUrlValid 
    		                || inputContent.matches("http://news.sohu.com/[\\d]+/n[\\d]+.shtml");
    		
    		if (!isInputUrlValid)
    		{
    			//弹出对话框提示
		    	JOptionPane.showMessageDialog(null,
		    			                     "支持的url只能是http://news.sohu.com"+
		    			                     "及其子链接。子链接的形式为"+
		    			                     "http://news.sohu.com/[\\d]+/n[\\d]+.shtml",
		    			                     "url非法", 
		    			                     JOptionPane.PLAIN_MESSAGE);
    		}
    		else if (   !strTimeInterval.matches("[\\d]+") 
		        || Long.parseLong(strTimeInterval)==0L )
		    {
		    	//弹出对话框提示
		    	JOptionPane.showMessageDialog(null,
		    			                     "请在设定定时抓取时间间隔一栏输入正整数",
		    			                     "错误", 
		    			                     JOptionPane.PLAIN_MESSAGE);
		    }
		    else
		    {
	            //重新创建定时器
	    		timer = new Timer();
	    		auxTimer = new Timer();
	    		
	    		//相应按键和编辑框的使能情况变化
	            stopButton.setEnabled(true);
	            inputTimeInterval.setEditable(false);
	            
	    		
	            //将String类型的时间间隔转换成long型
	    		long longTimeInterval = Long.parseLong(strTimeInterval);
	    		
	    		//立即执行该任务，间隔时间为第二个参数
	        	timer.schedule(new MyTask(), 0, longTimeInterval * 1000);
	        	auxTimer.schedule(new TimerCounter(), 0, 1000);  
		    }
    	}
    }
    
    /**
     * 定时器timer使用的类
     * @author Bob Hu
     *
     */
    static class MyTask extends java.util.TimerTask
    {
    	@Override
    	public void run() 
    	{		
    		if (thread != null)
    		{
    			thread.stop();
    		}
    		
    		//相应按钮的使能
    		startButton.setEnabled(false);
    		inputUrl.setEnabled(false);
    		
    		//获取url入口地址
    		final String inputContent = inputUrl.getText();
    		
    		final Crawler crawler = new Crawler(crawlerUI);
    		
            //启动线程，进行网页的解析
            thread = new Thread (new Runnable() 
            {
                public void run() 
                {
                	crawler.crawling(new String[]{inputContent});
                }
            } );
          
            thread.start();  
    	}
   	}
    
    /**
     * 定时器auxTimer使用的类
     * @author Bob Hu
     *
     */
    static class TimerCounter extends java.util.TimerTask
    {
    	@Override
    	public void run()
    	{   
    		//显示已经抓取的url数量
    		String strCrawlingNum = crawlingNum.toString();
    		crawlingNumText.setText(strCrawlingNum);
    		
    		// 第一次进入定时器，获取定时间隔
    		if (!isCounterInit)
    		{
    		    String strTimeInterval = inputTimeInterval.getText();    		    	
    		    counter = Integer.valueOf(strTimeInterval).intValue();
    		    isCounterInit = true;
    		}
    		
    		//计数器递减，并显示在界面上，完成倒计时的功能
    		counter--;
    		String strCounter = counter.toString();
    		timeElapsce.setText(strCounter);
    		
    		//第二轮定时开始
    		if(counter==0)
    		{
    			//获取用户输入的定时间隔
    			String strTimeInterval = inputTimeInterval.getText();
    			counter = Integer.valueOf(strTimeInterval).intValue();
    			
    			crawlingNum = 0L;
    		}
    	}
    }    
    
    public static void main(String[]args)
    {
        CrawlerUI crawlerUI = new CrawlerUI();
        crawlerUI.create();
    }
}
    


