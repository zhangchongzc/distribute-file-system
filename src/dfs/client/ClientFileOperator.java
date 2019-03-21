package dfs.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import dfs.interfac.FileOperation;
import dfs.utils.ConvertMapArray;

import dfs.utils.MD5Utils;
import dfs.utils.Tool;

public class ClientFileOperator implements FileOperation {
    //这里是客户端应填的代码。实现完整的逻辑，要求是实现与server交互，与Node节点交互。
	@Override
	public void fileupload(String filename, String ip, int port)  {//此处的filename为将要上传的本地文件名
		// TODO Auto-generated method stub  ip port 为fileServer的ip port
		System.out.println("Start upload------");
		Map<String, Object> fileinformap=new HashMap<>();//用map来存储文件名称和文件名称
		File file=new File(filename);
		fileinformap.put("filename", filename);
		fileinformap.put("filesize",file.length());
		Socket socket = null;
		InputStream inputStream = null;
		OutputStream outputStream=null;
		try {
		  try {
			  socket = new Socket(ip, port);//与fileServer建立连接
			  ObjectOutputStream ooStream=new ObjectOutputStream(socket.getOutputStream());//向server发送信息
			  System.out.println("--------Get Stream-------");
				ooStream.writeInt(1);;//表示上传
				ooStream.writeObject(fileinformap);//将先前map发送
				ooStream.writeChar('e');;//表示结束
				ooStream.flush();
	            inputStream=socket.getInputStream();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("中心服务器节点崩溃");
		}
		  	ObjectInputStream ois=new ObjectInputStream(inputStream);
		  	 Map<String,Object> map=(Map<String, Object>)ois.readObject();//接收server发送的数据主存节点备份节点等数据
				System.out.println("Connect to Server sucessfully");
				if(map.get("uuid")!=null)
				{   System.out.println("File information");
					System.out.println("Fileid "+map.get("uuid"));//文件的唯一标识
					System.out.println("Main node ip,port"+(String)map.get("nodeip")+" ,"+(int)map.get("nodeport"));//主节点的ip port
					System.out.println("Folderroot:"+(String)map.get("router"));//主存节点的下上传到的路径
					System.out.println("__NEW_FILE_NAME"+(String)map.get("uuid")+filename.substring(filename.lastIndexOf(".")));//上传后的文件名
					System.out.println("______________BACKUP_NODE__________");
					System.out.println("Main node ip,port"+(String)map.get("ip")+" ,"+(int)map.get("port"));//备份节点ip port
					System.out.println("Folderroot:"+(String)map.get("brouter"));//备份节点路径
					
				}
				socket.close();
				try {
					socket=new Socket((String)map.get("nodeip"),(int)map.get("nodeport"));//与主存节点建立联系
					String router=(String)map.get("router");
					String uuid=(String)map.get("uuid");
					String fileName=uuid+filename.substring(filename.lastIndexOf("."));
					String Ip=(String)map.get("ip");
					int Port=(int)map.get("port");
					Map<String, Object> storeInformap=new HashMap<>();//将文件要上传到的备份节点路径等信息发送到主存节点
					storeInformap.put("router", router);
					storeInformap.put("fileName", fileName);
					storeInformap.put("Ip", Ip);
					storeInformap.put("Port",Port);
					storeInformap.put("backuprouter", (String)map.get("brouter"));
					byte[] byteArray=ConvertMapArray.convertMapToByteArray(storeInformap);//将storeInformap转换为byte数组发送到nodeserver
					int length=byteArray.length;
					InputStream fis=new FileInputStream(file); 
					BufferedInputStream bis=new BufferedInputStream(fis);
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());//
					dos.write(1);
					( dos).writeInt(byteArray.length);
					System.out.println("length: "+length);
					dos.write(byteArray);
					byte []array=new byte[1024*100];
					int i=0;
					while(bis.read(array)!=-1)
					{
						array=Tool.compress(Tool.quickEncrypt(array));//加密//压缩
						dos.write(array);
					}
					System.out.println("文件读入成功");
					dos.flush();
				    socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
					System.err.println("主存储节点存储失败");
					System.out.println("向备份节点发送数据");//如果上传主存节点失败则与备份节点建立联系
					String Ip=(String)map.get("ip");//直接上传到备份节点
					int Port=(int)map.get("port");
					System.out.println(Ip+","+Port);
					socket=new Socket(Ip,Port);
					
					String router=(String)map.get("brouter");
					String uuid=(String)map.get("uuid");
					String fileName=uuid+filename.substring(filename.lastIndexOf("."));
					
					Map<String, Object> storeInformap=new HashMap<>();
					storeInformap.put("router", router);
					storeInformap.put("fileName", fileName);
					storeInformap.put("Ip", (String)map.get("nodeip"));
					storeInformap.put("Port",(int)map.get("nodeport"));
					storeInformap.put("backuprouter",router );
					byte[] byteArray=ConvertMapArray.convertMapToByteArray(storeInformap);
					int length=byteArray.length;
					InputStream fis=new FileInputStream(file); 
					BufferedInputStream bis=new BufferedInputStream(fis);
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					dos.write(1);
					( dos).writeInt(byteArray.length);
					System.out.println("length: "+length);
					dos.write(byteArray);
					byte []array=new byte[1024*100];
					int i=0;
					while(bis.read(array)!=-1)
					{
						array=Tool.compress(Tool.quickEncrypt(array));//加密//压缩
						dos.write(array);
					}
					System.out.println("文件读入成功");
					dos.flush();
				    socket.close();
				}
			
				} catch ( Exception e) {
				// TODO Auto-generated catch block
				System.out.println("服务器节点崩溃");//如果主节点备份节点都无法上传则说明节点服务器崩溃
			}
		} 
	 
	@Override
	public void filedownload(String uuid, String ip, int port) {
		// TODO Auto-generated method stub
		System.out.println("Start download--------");
		Map<String, Object>downlaodMap=new HashMap<>();//下载时要提供文件的唯一标识
		downlaodMap.put("uuid", uuid);
		Socket socket=null;
		try {
			socket = new Socket(ip, port);//与FileServer建立联系获取到文件存储在哪个节点
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.writeInt(2);//表示下载
			objectOutputStream.writeObject(downlaodMap);
			objectOutputStream.flush();
			ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());//接收由FileServer提供的节点信息
				Map<String, Object> map=(Map<String, Object>) ois.readObject();
				if(map!=null)//如果接收到的map集合不为空说明找到了所要下载的文件所在节点
				{
					if((boolean)map.get("isuseful")!=true)
					{
						System.err.println("Wrong uuid or the file doesn't exist");
					}
					else
					{//2.向存储节点来要
						//接受信息，向主存储节点下载文件
						DataInputStream oins=null;
						String nodeip=(String) map.get("nodeip");
						int nodeport=(int)map.get("nodeport");
						String filename=(String)map.get("filename");
						String router=(String)map.get("router");
						String backupip=(String)map.get("backupip");
						int backupport=(int)map.get("backupport");
						String backuprputer=(String)map.get("backuprouter");
						String prename=(String)map.get("prename");
						socket.close();
						DataOutputStream obj;
						try {
							socket=new Socket(nodeip, nodeport);//与主存节点建立联系
							System.out.println(nodeip+","+nodeport);
							obj=new DataOutputStream(socket.getOutputStream());//用来向主存节点写存储路径以便下载
						} catch (Exception e) {
							// TODO: handle exception
							try {
								socket=new Socket(backupip, backupport);//与备份节点建立联系
							} catch (Exception e2) {
								// TODO: handle exception
								System.out.println("服务节点已崩溃");//如果备份节点出错则主存备份都崩溃不能在下载
								throw new Exception();
							}
							
							//与备份节点进行通信
							obj=new DataOutputStream(socket.getOutputStream());//用来向备份节点传送所要下载的文件的路径
							obj.writeInt(2);
							System.out.println("Client try to attach"+backuprputer+"/"+filename);
							String filepath1=backuprputer+"/"+filename;
							Map<String, Object> mapp1=new HashMap<>();
							mapp1.put("filepath", filepath1);
							byte[]bytearray1=ConvertMapArray.convertMapToByteArray(mapp1);
							obj.writeInt(bytearray1.length);
							obj.write(bytearray1);
							obj.flush();//将下载信息发送到node
							try {
								oins=new DataInputStream(socket.getInputStream());//用来从备份节点读取文件流
							} catch (Exception e2) {
								// TODO: handle exception
								System.out.println("服务节点已崩溃");
								throw new Exception();
							}
							
							FileOutputStream foStream=new FileOutputStream(new File(prename));
							DataOutputStream doStream=new DataOutputStream(foStream);
							System.out.println("Create "+prename);
							int length=oins.readInt();
							System.out.println("lenth:"+length);
							byte []array=new byte[length];
							
							/*byte key=MD5Utils.getKey();
							while((readbyte=bis.read())!=-1){
								//array=EDS.Encrytor(array);//加密
								dos.write((readbyte^key));
							}*/
						   oins.read(array);
						   array=Tool.quickDecrypt(Tool.decompress(array));
						   doStream.write(array);//将文件流写入本地文件
							socket.close();
							System.out.println("从备份节点获取到了文件");
							throw new Exception();//结束
						}
						obj.writeInt(2);
						System.out.println("Client try to attach"+router+"/"+filename);
						String filepath=router+"/"+filename;
						Map<String, Object> mapp=new HashMap<>();
						mapp.put("filepath", filepath);
						byte[]bytearray=ConvertMapArray.convertMapToByteArray(mapp);
						obj.writeInt(bytearray.length);
						obj.write(bytearray);
						obj.flush();
						System.out.println("_________SEND__REQUEST");
						
						try {
							oins=new DataInputStream(socket.getInputStream());
						} catch (Exception e) {
							//若中断链接
							try {
								socket=new Socket(backupip, backupport);
							} catch (Exception e2) {
								// TODO: handle exception
								System.out.println("服务节点已崩溃");
								throw new Exception();
							}
							
							//与备份节点进行通信
							obj=new DataOutputStream(socket.getOutputStream());
							obj.writeInt(2);
							System.out.println("Client try to attach"+backuprputer+"/"+filename);
							String filepath1=backuprputer+"/"+filename;
							Map<String, Object> mapp1=new HashMap<>();
							mapp.put("filepath", filepath1);
							byte[]bytearray1=ConvertMapArray.convertMapToByteArray(mapp1);
							obj.writeInt(bytearray.length);
							obj.write(bytearray1);
							obj.flush();
							try {
								oins=new DataInputStream(socket.getInputStream());
							} catch (Exception e2) {
								// TODO: handle exception
								System.out.println("服务节点已崩溃");
							}
							
							FileOutputStream foStream=new FileOutputStream(new File(prename));
							DataOutputStream doStream=new DataOutputStream(foStream);
							System.out.println("Create "+prename);
							int length=oins.readInt();
							System.out.println("lenth:"+length);
							byte []array=new byte[length];
							
							/*byte key=MD5Utils.getKey();
							while((readbyte=bis.read())!=-1){
								//array=EDS.Encrytor(array);//加密
								dos.write((readbyte^key));
							}*/
						   oins.read(array);
						   array=Tool.quickDecrypt(Tool.decompress(array));
						   doStream.write(array);
							
							socket.close();
							System.out.println("从备份节点获取到了文件");
							throw new Exception();
						}
						
						FileOutputStream foStream=new FileOutputStream(new File("a/"+prename));
						DataOutputStream doStream=new DataOutputStream(foStream);
						System.out.println("Create "+prename);
						int length=oins.readInt();
						System.out.println("lenth:"+length);
						byte []array=new byte[length];
						
						/*byte key=MD5Utils.getKey();
						while((readbyte=bis.read())!=-1){
							//array=EDS.Encrytor(array);//加密
							dos.write((readbyte^key));
						}*/
					   oins.read(array);
					   array=Tool.quickDecrypt(Tool.decompress(array));
					   doStream.write(array);
					
						socket.close();
						System.out.println("从主存储节点获取到了文件");
					}
				}
			
			 
		     }  catch (Exception e) {
			// TODO Auto-generated catch block
			
		
		}
		
		
		
		
	}

	@Override
	public void removefile(String uuid, String ip, int port) {
		// TODO Auto-generated method stub
		System.out.println("Start download--------");
		Map<String, Object>removeMap=new HashMap<>();
		removeMap.put("uuid", uuid);
		//1.向服务器询问在哪
		Socket socket=null;
		try {
		socket = new Socket(ip, port);
		ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeInt(3);//表示删除
		objectOutputStream.writeObject(removeMap);
		objectOutputStream.flush();
//			objectOutputStream.close();
		//接受消息
		ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());


		Map<String, Object> map=(Map<String, Object>) ois.readObject();


		if(map!=null)
		{
		if((boolean)map.get("isuseful")!=true)
		{
		System.err.println("Wrong uuid or the file doesn't exist");
		}
		else
		{//2.在删除主存节点的同时删除备份节点所存文件
		DataInputStream oins=null;
		String nodeip=(String) map.get("nodeip");
		int nodeport=(int)map.get("nodeport");
		String filename=(String)map.get("filename");
		String router=(String)map.get("router");

		String backupip=(String)map.get("backupip");
		int backupport=(int)map.get("backupport");
		String backuprputer=(String)map.get("backuprouter");
		String prename=(String)map.get("prename");
		socket.close();
		DataOutputStream obj;
		try {
		socket=new Socket(nodeip, nodeport);
		System.out.println(nodeip+","+nodeport);
		//与主节点进行通信
		 obj=new DataOutputStream(socket.getOutputStream());
		 obj.writeInt(3);
		System.out.println("Client try to attach"+router+"/"+filename);
		String filepath1=router+"/"+filename;
		Map<String, Object> mapp1=new HashMap<>();
		mapp1.put("filepath", filepath1);
		mapp1.put("backupip", backupip);
		mapp1.put("backupport", backupport);
		mapp1.put("backfilename",filename);
		mapp1.put("backuprouter", backuprputer);
		byte[]bytearray1=ConvertMapArray.convertMapToByteArray(mapp1);;
		obj.writeInt(bytearray1.length);
		obj.write(bytearray1);
		obj.flush();

		} catch (Exception e) {
		// TODO: handle exception
		System.out.println("服务节点已崩溃");
		}
		}
		}
		}
		catch(Exception e){e.printStackTrace();}
		}
		 /*--------Get uuid  Finished-----------*/

}
