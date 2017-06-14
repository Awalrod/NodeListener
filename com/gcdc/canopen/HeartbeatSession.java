package com.gcdc.canopen;

import com.gcdc.can.CanMessage;

import java.util.concurrent.atomic.*;


class HeartbeatSession extends Thread
{
	Heartbeat heartbeat;
	OdEntry params;


	HeartbeatSession( Heartbeat heartbeat, OdEntry params )
	{
		this.heartbeat = heartbeat;
		this.params = params;
		setName("HeartbeatSession");
	}


	int getInterval() throws Exception
	{
		if(params ==null)
		{
			System.out.println("Error HeartbeatSession.getInterval params is null");
			return(5000);
		}

		SubEntry se = params.getSub(0);
		if(se == null)
		{
			System.out.println("Error HeartbeatSession.getInterval returned null");
			return(5000);
		}

		return(se.getInt());
	}


	boolean processMessage(CanMessage msg) throws Exception
	{
		return(true);
	}


	public void run()
	{
		int interval = 5000;
		try
		{
			while(true)
			{
				int state = heartbeat.getState();
				byte[] bdata = new byte[1];
				bdata[0] = (byte)state;
				try
				{
					interval = getInterval();
					heartbeat.send((Protocol.NODE_GUARD <<7), bdata);
				}
				catch( java.io.IOException e)
				{
					System.out.println("IO ERRORs ending Heartbeat signal: "+e);
					System.exit(-4);
				}
				catch( Exception e)
				{
					interval = 5000;
					System.out.println("ERROR sending Heartbeat signal: "+e);
				}
				sleep(interval);
			}
		}
		catch(InterruptedException e)
		{
		System.out.println("HeartbeatSession interrupted "+e);
//			e.printStackTrace();
		}
	}
}

