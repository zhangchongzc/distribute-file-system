package dfs.client;

import dfs.utils.Config;

public class FileClient {

	public static void main(String[]args)
	{
		ClientFileOperator clientFileOperator=new ClientFileOperator();//构建操作类具体的上传下载删除逻辑在此类中实现
		Config config=new Config("FileServer.properties");//获取到fileServer的配置信息
		String ip=config.getValue("ip");
		int port=Integer.parseInt(config.getValue("port").trim());
		//读取FileServer.properties信息，进行通信
		if(args[0].equals("upload"))//通过控制台或者命令行输入具体的上传下载或删除来启动clientFileOperator执行
		{
			//上传
			//args[1]=filename
			clientFileOperator.fileupload(args[1], ip, port);
		}
		else if(args[0].equals("download"))
		{
			//args[1]=uuid
			clientFileOperator.filedownload(args[1], ip, port);
		
		}
		else if(args[0].equals("remove"))
		{
			//args[1]=uuid
			clientFileOperator.removefile(args[1], ip, port);
		}
	}
}
