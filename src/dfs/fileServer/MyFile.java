package dfs.fileServer;

import java.io.Serializable;

public class MyFile implements Serializable{

	String filename;
	String uuid;
	String filepath;
	String StrogeNodeid;
	String Storgeip;
	int    Storgeport;
	String backupip;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getStrogeNodeid() {
		return StrogeNodeid;
	}
	public void setStrogeNodeid(String strogeNodeid) {
		StrogeNodeid = strogeNodeid;
	}
	public String getStorgeip() {
		return Storgeip;
	}
	public void setStorgeip(String storgeip) {
		Storgeip = storgeip;
	}
	public int getStorgeport() {
		return Storgeport;
	}
	public void setStorgeport(int storgeport) {
		Storgeport = storgeport;
	}
	public String getBackupip() {
		return backupip;
	}
	public void setBackupip(String backupip) {
		this.backupip = backupip;
	}
	public int getBackupPort() {
		return backupPort;
	}
	public void setBackupPort(int backupPort) {
		this.backupPort = backupPort;
	}
	public String getBackuppath() {
		return backuppath;
	}
	public void setBackuppath(String backuppath) {
		this.backuppath = backuppath;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	int backupPort;
	String backuppath;
	String filetype;
	public MyFile(String filename, String uuid, String filepath, String strogeNodeid, String storgeip,
			int storgeport, String backupip, int backupPort, String backuppath, String filetype) {
		super();
		this.filename = filename;
		this.uuid = uuid;
		
		this.filepath = filepath;
		StrogeNodeid = strogeNodeid;
		Storgeip = storgeip;
		Storgeport = storgeport;
		this.backupip = backupip;
		this.backupPort = backupPort;
		this.backuppath = backuppath;
		this.filetype = filetype;
	}
	
}
