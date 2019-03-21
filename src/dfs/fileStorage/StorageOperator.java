package dfs.fileStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import dfs.interfac.FileOperation;
import dfs.utils.ConvertMapArray;

public class StorageOperator  {

	/*实现向备份节点发送实现备份
	 * 
	 * @param ip 备份节点的ip
	 * 
	 * @param port 备份节点的端口
	 * 注意此处filename改为绝对路径，
	 * */

	public void fileupload(String filename,String sourcefilename,String ip, int port) {
		// TODO Auto-generated method stub
		try {
			Socket socket=new Socket(ip, port);
			FileInputStream fileInputStream=new FileInputStream(new File(sourcefilename));
			DataInputStream dis=new DataInputStream(fileInputStream);
			DataOutputStream doStream=new DataOutputStream(socket.getOutputStream());
			doStream.writeInt(4);//表示备份
			Map<String, Object> sMap=new HashMap<>();
			sMap.put("fileName", filename);
			byte[] mapbytearray=ConvertMapArray.convertMapToByteArray(sMap);
			System.out.println("Backup Map length"+mapbytearray.length);
			doStream.writeInt(mapbytearray.length);
			doStream.write(mapbytearray);
		    int i=0;
		    while((i=dis.read())!=-1)
		    {
		    	doStream.write(i);
		    }
		    fileInputStream.close();
			doStream.flush();
			doStream.close();
			fileInputStream.close();
			System.out.println("Backup send file sucessfuly");
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    /*实现文件传输到Client节点
     * 
     * @param ip Client的ip
	 * 
	 * @param port CLient的端口
     * */

	public void filedownload(String uuid, String ip, int port) {
		// TODO Auto-generated method stub

	}


	/*实现备份节点文件的删除*/
	/*实现备份节点文件的删除*/
	public void removefile(String filename,String backupip, int backupport) {
		// TODO Auto-generated method stub
		try {
			System.out.println("remove backupfile");
			Socket socket=new Socket(backupip, backupport);
			DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeInt(5);
			Map<String, Object> rMap=new HashMap<>();
			rMap.put("filename", filename);
			byte[] mapbytearray=ConvertMapArray.convertMapToByteArray(rMap);
			dataOutputStream.writeInt(mapbytearray.length);
			dataOutputStream.write(mapbytearray);
			dataOutputStream.flush();
			socket.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

}
