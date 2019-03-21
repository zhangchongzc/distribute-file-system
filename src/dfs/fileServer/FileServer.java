package dfs.fileServer;
 

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
 
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
import java.util.concurrent.LinkedBlockingQueue;

import dfs.fileStorage.StrogeOpServer;
import dfs.utils.Config;
import dfs.utils.ConvertMapArray;
 

public class FileServer {
	public static LinkedBlockingQueue<Map<String, Object> >queue=
			new LinkedBlockingQueue<>();//保存Node的注册信息，构建Node类来存储
	public static List<StorageNode> nodelist=new ArrayList<>();//存储节点信息
	public static void saveNodeFile() throws FileNotFoundException
	{
		FileOutputStream fileOutputStream=new FileOutputStream(new File("NodeMsg.txt"));
		PrintStream out=new PrintStream(fileOutputStream);
		for(int i=0;i<nodelist.size();i++)
		{
			StorageNode node=nodelist.get(i);
			out.println(node.getNodename()+","+node.getNodeIP()+","+
			node.getNodePort()+","+node.getConnectdate().toString()+","+node.getRootFolder()+","+node.getSize()+","+
					node.getUsesize());
		}
	}
	public static void main(String[]args) throws NumberFormatException, IOException
	{
		//创建多个线程，执行不同的任务，线程均为死循环
		new Thread(){
			//文件操作，返回uuid，或返回文件所在node的ip，port
			public void run()
			{
				try {
					//读入文件中已存在的文件，供服务端使用
					FileopProtocol.fileslist.addAll(FileopProtocol.getFilelist("FilesMsg.txt"));
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//初始化
				new Opserver(Integer.parseInt(new Config().getValue("FileServer.properties", "port").trim()), 
						new ThreadSupport(new FileopProtocol()));
			 
			}
		}.start();
		new Thread()
		{
			//和strogeNOde的通信，通知注册node
			//处理udp队列
			public void run()
			{   Map<String, Object> infomap; 
			 try{
			 
				while(true)
				{	
					
					//是否超时
					for(int i=0;i<nodelist.size();i++)
					{
						if((new Date().getTime())-nodelist.get(i).getConnectdate().getTime()>60000)
				    	{
				    		System.out.println("节点："+nodelist.get(i).getNodename()+" 失效");
				    		nodelist.remove(i);
				    	}
					}
					 
					//更新
					saveNodeFile();
					
					while(queue.size()!=0)
				  {   
				
					infomap=queue.take();
					  System.out.println("take queue");
					String nodename=(String) infomap.get("nodename");
					double presentsize=(double)infomap.get("presentsize");
				    if(nodename!=null)
					 {
						for(int i=0;i<nodelist.size();i++)
							{
								if(nodename.equals(nodelist.get(i).getNodename()))
								{
									System.out.println("节点："+nodelist.get(i).getNodename()+" 通知活着");
									nodelist.get(i).setConnectdate(new Date());
									nodelist.get(i).setUsesize(presentsize);//动态更新大小
									//更新
									
								}
						    
							}
					   
						
					}
				  
					
				}
					saveNodeFile();
			}}catch(Exception e)
			  {
				e.getStackTrace();
		     }}
		}.start();
		//接收udp
		System.out.println(Integer.parseInt(new Config()
				.getValue("FileServer.properties", "port").trim()));
		DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(new Config()
				.getValue("FileServer.properties", "port").trim()));
		System.out.println("Chat Server started.");
		while (true)
		{
			byte[] buffer = new byte[1024 * 16];
			DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(recvPacket);

			InetSocketAddress remoteAddress = new InetSocketAddress(recvPacket.getAddress(),
					recvPacket.getPort());
		

			byte[] data = recvPacket.getData();
			byte[] recvData = new byte[recvPacket.getLength()];
			System.arraycopy(buffer, 0, recvData, 0, recvData.length);

			Map<String, Object> map = ConvertMapArray.convertByteArrayToMap(recvData);

			if (map.containsKey("isconnected")) { 
				boolean b = (boolean) map.get("isconnected");
				if (b) { // 加入节点，注册
					System.out.println("注册！");
					nodelist.add(new StorageNode((String)map.get("nodename"), (String)map.get("nodeip"), Integer.parseInt((String)
							map.get("nodeport")), (boolean) map.get("isconnected"), 
							new Date(), (String)map.get("rootfolder"),(double)map.get("volume"),(double)
							map.get("presentsize")));
				} 
			}
			else
			{ 
				queue.add(map);
			}

		}
	}
	
}
