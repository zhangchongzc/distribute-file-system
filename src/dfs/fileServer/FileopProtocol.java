package dfs.fileServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dfs.interfac.FileStrategy;
import dfs.utils.ConvertMapArray;
import dfs.utils.UUIDutils;

public class FileopProtocol implements FileStrategy {
	public static List<MyFile> fileslist=new ArrayList<>();
    public void savefile(String filename) throws IOException {
    	FileOutputStream fileOutputStream=new FileOutputStream(new File(filename));
    	PrintStream out=new PrintStream(fileOutputStream);
    	for(int i=0;i<fileslist.size();i++)
    	{
    		MyFile myFile=fileslist.get(i);
    	   out.println(myFile.getFilename()+","+myFile.getUuid()+","+myFile.getStorgeip()+","+
    		myFile.getStorgeport()+","+myFile.getFilepath()+","+
    			   myFile.getBackupip()+","+myFile.getBackupPort()+","+myFile.getBackuppath()+
    			   ","+myFile.getFiletype());
    	}
    	
    	fileOutputStream.close();
	}
	public static List<MyFile> getFilelist(String filename) throws IOException, ClassNotFoundException
	{
		List<MyFile> list=new ArrayList<>();
		
		FileReader filereader=new FileReader(filename);
		BufferedReader reader=new BufferedReader(filereader);
		String myfile;
		while((myfile=reader.readLine())!=null)
		{
			
			String[] array=myfile.split(",");
			System.out.println("Read"+array.length);
			list.add(new MyFile(array[0], array[1], array[4], "001", array[2], Integer.parseInt(array[3].trim()), array[5],
					Integer.parseInt(array[6].trim()), array[7], array[8]));
		}
		return list;
		
	}
    public static MyFile searchFilelist(String uuid)
    {
    	for(int i=0;i<fileslist.size();i++)
    	{
    		if(fileslist.get(i).getUuid().equals(uuid))
    		{
    			return fileslist.get(i);
    		}
    	}
    	return null;
    }
    public static int searchFilelistindex(String uuid)
    {
    	for(int i=0;i<fileslist.size();i++)
    	{
    		if(fileslist.get(i).getUuid().equals(uuid))
    		{
    			return i;
    		}
    	}
    	return -1;
    } 
	@Override
	public void service(Socket socket) {
		// TODO Auto-generated method stub
		System.out.println("---Recieve from client -----");
	
		int command=0;
		//Fileoperator fileoperator=new Fileoperator();
   try{
 
	   		ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
	   			command = dis.readInt();
	   			System.out.println(command);
	   			//对客户端发来的请求进行操作,123分别进行不同的操作1上传，2下载 3.删除
	   			switch (command) {
	   			case 1 :
	   		{
			    Map<String, String> Properties=new HashMap<>();
			    //Use Map
			    //保存发送的信息，
			    String filename="";
			    InetAddress remoteAdress=socket.getInetAddress();
			    Map<String, Object> fileinfor=new HashMap<>();
			    fileinfor=(Map<String, Object>) dis.readObject();
						if(fileinfor.get("filename")!=null)
						{
							filename=(String) fileinfor.get("filename");
							System.out.println();
						}
						 ObjectOutputStream dos=new ObjectOutputStream(socket.getOutputStream());
						 if(dis.readChar()=='e')//结束读返回uuid
						 {
							 fileinfor.clear();
							 fileinfor.put("uuid", UUIDutils.getUUID());
							 List<StorageNode> nodes=new ArrayList<>();
							 nodes.addAll(FileServer.nodelist);//获取所有
							 StorageNode node=StorageNode.getMinUsingNode(nodes);//得到最小使用
							 StorageNode backnode=StorageNode.getMinUsingNode(nodes);
							 fileinfor.put("router", node.getRootFolder());
							 fileinfor.put("nodeip", node.getNodeIP());
							 fileinfor.put("nodeport",node.getNodePort());
							 fileinfor.put("ip", backnode.getNodeIP());
							 fileinfor.put("port",backnode.getNodePort());
							 fileinfor.put("brouter", backnode.getRootFolder());
							 dos.writeObject(fileinfor);
							 dos.flush();
							
							fileslist.add(new MyFile(filename, (String) fileinfor.get("uuid"),
									node.getRootFolder() , node.getNodename(),node.getNodeIP(),
									node.getNodePort(),backnode.getNodeIP(),backnode.getNodePort(), 
									backnode.getRootFolder(), filename.substring(filename.lastIndexOf('.'))));
							savefile("FileMsg.txt");
							 System.out.println("---Send to client -----");
							 socket.close();
						 }
						
			
				
			}break;
				//fileoperator.fileupload(filename, remoteAdress.getHostAddress(),socket.getPort());
			 /*
		     * 实现向Client节点发送文件信息，
		     * FileClient要上传文件至云端，首先发送文件信息，
		     * 文件名称，文件大小等信息，然后服务器将信息封装成记录，放
		     * 到Map集合或者List集合中。管理这些文件，然
		     * 后回传主备份服务器信息，UUID等信息给客户端，
		     * 由客户端完成操作
		     */
             case 2://文件下载返回Node所在ip和备份所在ip，port和下载位置
             {   Map<String,Object> map=new HashMap<>();
            	 map=(Map<String, Object>) dis.readObject();
            	 String uuid=(String) map.get("uuid");
            	 ObjectOutputStream objectOutputStream;
            	 MyFile myFile=searchFilelist(uuid);
            	 if(myFile!=null)
            	 {
            		
            		 map.clear();
            		 map.put("isuseful", true);
            		 map.put("filename", uuid+myFile.getFiletype());
            		 map.put("nodeip", myFile.getStorgeip());
            		 map.put("nodeport", myFile.getStorgeport());
            		 map.put("router", myFile.getFilepath());
            		 map.put("prename", myFile.getFilename());
            	     map.put("backupip", myFile.getBackupip());
            		 map.put("backupport",myFile.getBackupPort());
            		 map.put("backuprouter", myFile.getBackuppath());
            		objectOutputStream =new ObjectOutputStream(socket.getOutputStream());
            		 
            		 objectOutputStream.writeObject(map);
            		 objectOutputStream.flush();
            		// byte[] array=ConvertMapArray.convertMapToByteArray(map);
            		// System.out.println("length:"+array.length);
            		// objectOutputStream.writeInt(array.length);
            		 //objectOutputStream.write(array);
            		// objectOutputStream.w
            		
            		 
            	 }
            	 else
            	 {
            		 map.clear();
            		 map.put("isuseful", false);
            		 objectOutputStream =new ObjectOutputStream(socket.getOutputStream());
            		 objectOutputStream.writeObject(map);
            		 objectOutputStream.flush();
            	 }
            	 socket.close();
            	
             } break; 
            case 3:
            {
            	 Map<String,Object> map=new HashMap<>();
            	 map=(Map<String, Object>) dis.readObject();
            	 String uuid=(String) map.get("uuid");
            	 ObjectOutputStream objectOutputStream;
            	 MyFile myFile=searchFilelist(uuid);
            	 if(myFile!=null)
            	 {
            		
            		 map.clear();
            		 map.put("isuseful", true);
            		 map.put("filename", uuid+myFile.getFiletype());
            		 map.put("nodeip", myFile.getStorgeip());
            		 map.put("nodeport", myFile.getStorgeport());
            		 map.put("router", myFile.getFilepath());
            		 map.put("prename", myFile.getFilename());
            	     map.put("backupip", myFile.getBackupip());
            		 map.put("backupport",myFile.getBackupPort());
            		 map.put("backuprouter", myFile.getBackuppath());
            		objectOutputStream =new ObjectOutputStream(socket.getOutputStream());
            		 
            		 objectOutputStream.writeObject(map);
            		 objectOutputStream.flush();
            		// byte[] array=ConvertMapArray.convertMapToByteArray(map);
            		// System.out.println("length:"+array.length);
            		// objectOutputStream.writeInt(array.length);
            		 //objectOutputStream.write(array);
            		// objectOutputStream.w
            		
            		 fileslist.remove(searchFilelistindex(uuid));
            		 savefile("FileMsg.txt");
            	 }
            	 else
            	 {
            		 map.clear();
            		 map.put("isuseful", false);
            		 objectOutputStream =new ObjectOutputStream(socket.getOutputStream());
            		 objectOutputStream.writeObject(map);
            		 objectOutputStream.flush();
            	 }
            	 socket.close();
            }
            break;
			default:
				break;
			}}catch(Exception e)
		{
				
		e.printStackTrace();}
		
		 }	 
}
		


