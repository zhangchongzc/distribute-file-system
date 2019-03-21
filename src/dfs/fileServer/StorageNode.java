package dfs.fileServer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

 

public class StorageNode implements Serializable {

	private String Nodename;
	private String NodeIP;
	private int NodePort;
	boolean isconnected;
	private Date connectdate;
	private String RootFolder;
	private double size;
	private double usesize;
	public StorageNode(String nodename, String nodeIP, int nodePort, boolean isconnected, Date connectdate,
			String rootFolder ,double size, double usesize
			) {
		super();
		Nodename = nodename;
		NodeIP = nodeIP;
		NodePort = nodePort;
		this.isconnected = isconnected;
		this.connectdate = connectdate;
		RootFolder = rootFolder;
		this.size = size;
		this.usesize = usesize;
	}
	
	public String getNodename() {
		return Nodename;
	}
	public void setNodename(String nodename) {
		Nodename = nodename;
	}
	public String getNodeIP() {
		return NodeIP;
	}
	public void setNodeIP(String nodeIP) {
		NodeIP = nodeIP;
	}
	public int getNodePort() {
		return NodePort;
	}
	public void setNodePort(int nodePort) {
		NodePort = nodePort;
	}
	public boolean isIsconnected() {
		return isconnected;
	}
	public void setIsconnected(boolean isconnected) {
		this.isconnected = isconnected;
	}
	public Date getConnectdate() {
		return connectdate;
	}
	public void setConnectdate(Date connectdate) {
		this.connectdate = connectdate;
	}
	public String getRootFolder() {
		return RootFolder;
	}
	public void setRootFolder(String rootFolder) {
		RootFolder = rootFolder;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	public double getUsesize() {
		return usesize;
	}
	public void setUsesize(double usesize) {
		this.usesize = usesize;
	}
	public static StorageNode getMinUsingNode(List<StorageNode>list)
	{
		int j=0;
		int min=0;
		for(int i=0;i<list.size();i++)
		{
			if((list.get(i).getSize()-list.get(i).getUsesize())>min)
			{
				j=i;
				min=(int) (list.get(i).getSize()-list.get(i).getUsesize());
			}
		}
		StorageNode node=list.get(j);
		list.remove(j);
		return node;
	}
	
}
