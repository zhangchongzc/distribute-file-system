package dfs.fileStorage;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dfs.utils.Config;
import dfs.utils.ConvertMapArray;

public class FileNodeServer {

	//1.注册，向Server注册，Map，把信息传过去
	//名称，ip，端口，容量，实际容量，剩余容量，文件数量，是否可用等信息
	//2.保持和Server的联系
	//3.完成与Node文件上传，下载
	 public static double getDirSize(File file) {     
	        //判断文件是否存在     
	        if (file.exists()) {     
	            //如果是目录则递归计算其内容的总大小    
	            if (file.isDirectory()) {     
	                File[] children = file.listFiles();     
	                double size = 0;     
	                for (File f : children)     
	                    size += getDirSize(f);     
	                return  size;     
	            } else {//如果是文件则直接返回其大小,以“兆”为单位   
	                double size  = file.length() / 1024.0 / 1024.0;        
	                return size;     
	            }     
	        } else {     
	            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");     
	            return 0;     
	        }     
	    }     
	public  static void main(String[] args)
	{
		Config config=new Config(args[0]);//加载文件
		//arg[0]=NodeRoot/Node1/Storage1.properties
		String Nodeip=config.getValue("NodeIP");
		int port=Integer.parseInt(config.getValue("NodePort").trim());
		String FileServerIP=config.getValue("FileServerIP");
		int FileServerPort=Integer.parseInt(config.getValue("FileServerPort").trim());;
		new Thread()
		{
			/*
			 *注册并且每隔一段时间发包
			 */
			//1.注册
			//2.每隔1min发一次包
			public void run()
	    	{
				Map<String, Object> Nodeinfo=new HashMap<>();
				Nodeinfo.put("nodename", config.getValue("NodeName"));
				Nodeinfo.put("nodeip", config.getValue("NodeIP"));
				Nodeinfo.put("nodeport", config.getValue("NodePort"));
				Nodeinfo.put("rootfolder", config.getValue("RootFolder"));
				Nodeinfo.put("volume",Double.parseDouble(config.getValue("Volume")));
				Nodeinfo.put("presentsize", getDirSize(new File(config.getValue("RootFolder"))));
			
				Nodeinfo.put("isconnected", true);
				try {
					byte[] data =ConvertMapArray. convertMapToByteArray(Nodeinfo);

					DatagramSocket socket = new DatagramSocket();
					DatagramPacket packet = new DatagramPacket(data, data.length);
                    System.out.println(config.getValue("NodeName")+"正在注册");
					packet.setSocketAddress(new InetSocketAddress(FileServerIP, FileServerPort));
					socket.send(packet);

				} catch (Exception e) {
					e.printStackTrace();
				}
				//之后每隔30s传一次
				while(true)
				{
					try {
						Thread.sleep(30000);
						Nodeinfo.clear();
						Nodeinfo.put("nodename", config.getValue("NodeName"));
						Nodeinfo.put("msg", new Date());
						Nodeinfo.put("presentsize", getDirSize(new File(config.getValue("RootFolder"))));
						System.out.println(""+(getDirSize(new File(config.getValue("RootFolder")))+1));
						byte[] data =ConvertMapArray. convertMapToByteArray(Nodeinfo);
														
						DatagramSocket socket = new DatagramSocket();
						DatagramPacket packet = new DatagramPacket(data, data.length);
	                    System.out.println(config.getValue("NodeName")+"正在通知活着");
	                    
						packet.setSocketAddress(new InetSocketAddress(FileServerIP, FileServerPort));
						socket.send(packet);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//
				}
				
	    	}
			
		
			
		}.start();
	    new Thread(){
	    	public void run()
	    	{
	    		//接收用户请求，上传，下载，移除，同步操作，TCP,一样的想法
	    		new StrogeOpServer(port, new StorageThreadPoolSupport(new StorageProtocol()));
	    	}
	    }.start();

	
	}
}
