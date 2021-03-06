package com.gcdc.canopen;

import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


class Pdo extends Protocol
{
	private List<PdoSession> rpdos;
	private List<PdoSession> tpdos;
	private ObjectDictionary od1;
	
	Pdo( Driver driver, boolean DEBUG, ObjectDictionary od1 ) throws Exception
	{
		super(driver, DEBUG, "PDO", od1);
		rpdos = new ArrayList<PdoSession>();
		tpdos = new ArrayList<PdoSession>();
		this.od1 = od1;
		debugPrint("creating Rx Pdo's from Od");
		for(int i=0;i<4; i++)
		{
			// default rx pdo's (RPDO)
//			OdEntry odComm = od1.getEntry(0x1400+i);
//			OdEntry odMap = od1.getEntry(0x1600+i);
//			PdoSession pdoSession = new PdoSession(this, odComm, odMap);
//			appendRxPdo( pdoSession);
			appendRxPdoFromIndex(i);
		}


		debugPrint("creating Tx Pdo's from Od");
		for(int i=0;i<4; i++)
		{
			// default tx pdo's (TPDO)
			OdEntry odComm = od1.getEntry(0x1800+i);
			OdEntry odMap = od1.getEntry(0x1A00+i);
			PdoSession pdoSession = new PdoSession(this, odComm, odMap);
			appendTxPdo( pdoSession);
		}
		debugPrint("new Pdo");
	}


	void appendRxPdo(PdoSession ps)
	{
		rpdos.add(ps);
	}

	public void appendRxPdoFromIndex(int indexOffset) throws COException
	{
		OdEntry odComm = od1.getEntry(0x1400+indexOffset);
		OdEntry odMap = od1.getEntry(0x1600+indexOffset);
		PdoSession pdoSession = new PdoSession(this, odComm, odMap);
		appendRxPdo( pdoSession);
	}


	void appendTxPdo(PdoSession ps)
	{
		tpdos.add(ps);
	}


	boolean sendSyncEvents()
	{
		boolean retval = false;
		if(isEnabled == false)
			return(retval);

//		debugPrint("Pdo.sendSyncEvents()");

		Iterator<PdoSession> ps;
		ps = tpdos.iterator();

		while(ps.hasNext())
		{
			try
			{
				if(ps.next().syncEvent())
					retval = true;
			}
			catch(Exception e)
			{
				System.out.println("ERROR: PdoSession.syncEvent() threw "+e);
			}
		}
		return(retval);
	}


	boolean processMessage(CanMessage msg)
	{
//		debugPrint("Pdo.processMessage() cobId: 0x"+toIndexFmt(msg.id));
		if(super.processMessage(msg) == false)
			return(false);

//		debugPrint("Pdo.processMessage() matched cobId: 0x"+toIndexFmt(msg.id));
//		if(debug)
//			msg.dump();

		Iterator<PdoSession> ps;
		if(msg.rtr == 0)
			ps = rpdos.iterator();
		else
			ps = tpdos.iterator();

		while(ps.hasNext())
		{
			try
			{
				if(ps.next().processMessage(msg))
				{	
					notifyListeners(msg);
					return(true);
				}
			}
			catch(Exception e)
			{
				System.out.println("ERROR: Pdo.processMessage() threw "+e);
				msg.dump();
			}
		}

		System.out.print("Warning: pdo could not be processed: ");
		msg.dump();
		return(false);
	}


	public void run()
	{
		debugPrint("Pdo expired timer");
	}
}

