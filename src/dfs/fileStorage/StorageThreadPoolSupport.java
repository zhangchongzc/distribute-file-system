package dfs.fileStorage;

import java.net.Socket;
import java.util.ArrayList;

import dfs.fileServer.FileopThread;
import dfs.interfac.FileStrategy;

public class StorageThreadPoolSupport implements FileStrategy {

	private ArrayList<StorageThread> threads=new ArrayList<>();
	private final int INIT_THREAD=50;
	private final int MAX_THREAD=100;
	private FileStrategy fileStrategy=null;
	public StorageThreadPoolSupport (FileStrategy fileStrategy)
    {
    	this.fileStrategy=fileStrategy;
    	for(int i=0;i<INIT_THREAD;i++)
    	{
    		StorageThread storageThread=new StorageThread(fileStrategy);
    		storageThread.start();
    		threads.add(storageThread);
    	}
    	try {
			Thread.sleep(300);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
    }
  

	@Override
	public void service(Socket socket) {
		// TODO Auto-generated method stub
		StorageThread t=null;
		boolean found=false;
		for(int i=0;i<threads.size();i++)
		{
			t=threads.get(i);
			if(t.isidle())
			{
				found=true;
				break;
			}
		}
		if(!found)
		{
			t =new StorageThread(fileStrategy);
			t.start();
			try {
				Thread.sleep(300);
			} catch (Exception e) {
				// TODO: handle exception
			}
			threads.add(t);
		}
		t.setSocket(socket);
		
	}

}
