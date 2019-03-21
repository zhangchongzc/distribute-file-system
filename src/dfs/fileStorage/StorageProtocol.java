package dfs.fileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import org.omg.PortableInterceptor.DISCARDING;

import dfs.interfac.FileStrategy;
import dfs.utils.ConvertMapArray;

public class StorageProtocol implements FileStrategy {
   
	@Override
	public void service(Socket socket) {
		// TODO Auto-generated method stub
		 StorageOperator storageOperator=new StorageOperator();
		 int command=0;
		 DataInputStream bis = null;
	
			
		
		 try{
				while(true)		
				{	
					command=0;
					if(socket!=null){
					bis = new DataInputStream(socket.getInputStream());
					command=bis.read();
					}
					switch (command) {
					case 1:
					{   int length=bis.readInt();
						System.out.println("Length"+length);
						byte[] byteArray=new byte[length];
						bis.read(byteArray,0, length);
						Map<String, Object> map=ConvertMapArray.convertByteArrayToMap(byteArray);
						FileOutputStream fos=new FileOutputStream((String)map.get("router")+"/"+(String)map.get("fileName"));
						int i=0;
						while((i=bis.read())!=-1){
						 
							fos.write(i);
						 
							}
						fos.close();
						socket.close();
						
						//进行备份
						new Thread(){
							public void run() {
								new StorageOperator().fileupload((String)map.get("backuprouter")+"/"+(String)map.get("fileName"),
										(String)map.get("router")+"/"+(String)map.get("fileName"), 
										(String)map.get("Ip"), 
										(int)map.get("Port"));
							}
							
							
						}.start();
					
						
					}break;
					case 2://下载
					{ 
						System.out.println("Download file__");
						String filename="";
						 int i=bis.readInt();
						 System.out.println("length"+i);
						 byte[]mapp=new byte[i];
						 bis.read(mapp, 0, i);
						 Map<String, Object>map=ConvertMapArray.convertByteArrayToMap(mapp);
						 filename=(String) map.get("filepath");
					   
						System.out.println("the file "+filename+"will be downloaded");
						FileInputStream fiStream=new FileInputStream(new File(filename));
						DataOutputStream objectOutputStream=new DataOutputStream(socket.getOutputStream());
						bis=new DataInputStream(fiStream);
						System.out.println("Filelenght"+(int) new File(filename).length());
						objectOutputStream.writeInt((int) new File(filename).length());
						byte []buf=new byte[(int) new File(filename).length()];
						bis.read(buf, 0,(int) new File(filename).length() );
						objectOutputStream.write(buf);
						objectOutputStream.flush();
						objectOutputStream.close();
						socket.close();
						
					}
					break;
					case 3:{//删除
						//System.out.println("remove fileMain");
						String filename="";
						 int i=bis.readInt();
						 System.out.println("length"+i);
						 byte[]mapp=new byte[i];
						 bis.read(mapp, 0, i);
						 Map<String, Object>map=ConvertMapArray.convertByteArrayToMap(mapp);
						 filename=(String) map.get("filepath");
						System.out.println("the file "+filename+"will be removed");
						File file =new File(filename);
						
						if(file.exists()){
						if(file.delete()){
						System.out.println("主存文件删除成功");
						}
						else{
						System.out.println("主存文件删除失败");
						}
						}
						else {
						System.out.println("主存文件不存在");
						}
						socket.close();
						new Thread(){
						public void run() {
							System.out.println((String)map.get("backuprouter")+"/"+(String)map.get("backfilename")+123);
						new StorageOperator().removefile((String)map.get("backuprouter")+"/"+(String)map.get("backfilename"), 
						(String)map.get("backupip"), 
						(int)map.get("backupport"));
						}


						}.start();
                       
						}
						break;
                    case 4://back up
                    {
                    	int length=bis.readInt();
						System.out.println("Length"+length);
						byte[] byteArray=new byte[length];
						bis.read(byteArray,0, length);
						Map<String, Object> map=ConvertMapArray.convertByteArrayToMap(byteArray);
						FileOutputStream foStream=new FileOutputStream(new File((String)map.get("fileName")) );
						int i=0;
						while((i=bis.read())!=-1){
							 
							foStream.write(i);
						 
							}
						System.out.println("备份 "+(String)map.get("fileName")+"结束");
						foStream.close();//结束
						
                    }
                 
                    	break;
                    case 5:{
                    	String filename="";
                    	 int i=bis.readInt();
                    	 //System.out.println("length"+i);
                    	 byte[]mapp=new byte[i];
                    	 bis.read(mapp, 0, i);
                    	 Map<String, Object>map=ConvertMapArray.convertByteArrayToMap(mapp);
                    	 filename=(String) map.get("filename");
                    	System.out.println("the file "+filename+"will be removed");
                    	File file =new File(filename);
                    	if(file.exists()){
                    	if(file.delete()){
                    	System.out.println("备份文件删除成功");
                    	}
                    	else{
                    	System.out.println("备份文件删除失败");
                    	}
                    	}
                    	else {
                    	System.out.println("备份文件不存在");
                    	}
                    	socket.close();
                    	
                    }break;
					default:
						
						break;
					}
		 }
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
	}

}