package com.gcdc.can.datagram;
import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import com.gcdc.can.CanMessageConsumer;

public class DatagramDriver implements Driver
{
	private String ipAddr;
	private int port;
	Task backgroundTask = null;
	CanMessageConsumer cmc;
	boolean debug;

	public DatagramDriver( boolean DEBUG )
	{
		debug = DEBUG;
		if( debug )
			System.out.println( "Datagram Driver Loaded" );
	}

	public int startTransfer() throws Exception
	{
		if( debug)
			System.out.println("opening thread with \""+ipAddr+":"+port+"\"");

		backgroundTask = new Task(ipAddr, port);
		backgroundTask.setCmc(cmc);
		backgroundTask.start();
		return(0);
	}

	public void stopTransfer()
	{
		if(backgroundTask != null)
		{
			backgroundTask.close();
			backgroundTask = null;
			System.gc();
		}	
	}

	public void sendMessage( CanMessage telegramm ) throws java.io.IOException
	{
		if(backgroundTask!=null)
		{
			backgroundTask.sendMsg(telegramm);
		}
		else
		{	
//			try
//			{
//				startTransfer();
//			}
//			catch(Exception e)
//			{
//				System.out.println("DatagranDriver.sendMessage fatal "+e);
//				System.exit(-1);
//			}
//			backgroundTask.sendMsg(telegramm);
			System.out.println("DatagramDriver.sendMessage background task not ready");
		}
		
	}

	public CanMessage getMessage()
	{
		return(null);
	}

	public int setCanSpeed( int speed ){ return 0; }
	public int getCanSpeed(){ return 0; }
	public int setFilter( int id_filter ){ return 0; }
	public int[] getFilter(){ return null; }
	public int deleteFilter( int id_filterd ){ return 0; }

	public int setSocketIP( String ip )
	{
		ipAddr = new String(ip);
		return(0);
	}

	public String getSocketIP()
	{
		return(ipAddr);
	}

	public int setSocketPort( int port )
	{
		this.port = port;
		return(port);
	}

	public int getSocketPort()
	{
		return(port);
	}


	public void setMessageConsumer(CanMessageConsumer cmc)
	{
		this.cmc = cmc;
		if(backgroundTask != null)
		{
			backgroundTask.setCmc(this.cmc);
		}
	}
}
