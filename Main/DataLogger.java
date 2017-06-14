package Main;

import DataFormatting.DataFormatter;
import DataRecording.AccelerometerReading;
import DataRecording.NodeTracker;
import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import com.gcdc.can.DriverManager;
import com.gcdc.canopen.*;
import GlobalVars.GlobalVars;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.*;	// for Set 
/**
 * Created by gcdc on 6/7/17.
 */
public class DataLogger {
    private CanOpen co;
    private DriverManager dm;
    private Driver drvr;
    private ObjectDictionary od;
    private NodeTracker[] nodes;
    private int odIndexes[] = new int[]{0x6210, 0x6211, 0x6212, 0x6213};
    private DataFormatter dfmt;
    
    private class SyncListener implements CanOpenListener{
        @Override
        public void onMessage(CanMessage canMessage) {
//	        System.out.println("SYNC message received");
	        if (GlobalVars.START_TIME == null) {
		        GlobalVars.START_TIME = System.nanoTime();
		        System.out.println(dfmt.produceHeader(odIndexes));
	        } else {
		        //Skip the first sync message since object dictionary entries have not been set
		        AccelerometerReading readings[] = new AccelerometerReading[nodes.length];
		        for(int i = 0; i < nodes.length; i++ ){
			        readings[i] = nodes[i].getLatestReading();
		        }
//		        System.out.println(dfmt.producePrettyOutputString(readings));
		        System.out.println(dfmt.produceOutputLine(readings));
	        }
        }

        @Override
        public void onObjDictChange(SubEntry se){}
        @Override
        public void onEvent(CanOpen co, int state) {}
    }
    
    private class InputThread extends Thread	{
        @Override
        public void run(){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            System.out.println("Press 'r' to reset");
            while(true){
                try{
                    line = in.readLine();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(line.equals("r")){
                    synchronized(co){
                    co.notify();
                    }
                }
            }
        }
    }
    
    
    public void reset(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println("num Threads before shutdown " +threadSet.size());
        for(Thread thread: threadSet)
        {
            System.out.println("name: "+thread.getName());
        }
        dm.unloadDriver();
        drvr = null; 
        dm = null;
        System.gc();
        co.toRebootState();
System.out.println("DataLogger.reset() co about to be null");
        co = null;
        od = null;
        GlobalVars.START_TIME = null;
        for(NodeTracker node : nodes){
            node = null;
        }
        System.gc();
        Set<Thread> threadSet1 = Thread.getAllStackTraces().keySet();
        System.out.println("num Threads after shutdown " +threadSet1.size());
        for(Thread thread: threadSet1)
        {
            System.out.println("name: "+thread.getName());
        }
    }
    
    public boolean launch(String confFile){
        dfmt = new DataFormatter();
        Set<Thread> threadSet1 = Thread.getAllStackTraces().keySet();
        System.out.println("num Threads at start " +threadSet1.size());
        for(Thread thread: threadSet1)
        {
            System.out.println("name: "+thread.getName());
        }

        try{
            while(true){
            System.out.println("CANbus driver starting");
            dm = new DriverManager("datagram", "192.168.1.54", 2000, false);
            drvr = dm.getDriver();
            System.out.println("CANbus driver configured");
            od = DefaultOD.create(0x23);
            co = new CanOpen(drvr, od, 0x23, false);
            InputThread in = new InputThread();
            nodes = new NodeTracker[4];
            nodes[0] = new NodeTracker(co,0x281,0x10, odIndexes[0],0x3,0x10, 0,1,2);
            nodes[1] = new NodeTracker(co, 0x282, 0x11, odIndexes[1], 0x3, 0x10, 0,1,2);
            nodes[2] = new NodeTracker(co, 0x283, 0x12, odIndexes[2], 0x3, 0x10, 0,1,2);
            nodes[3] = new NodeTracker(co, 0x284, 0x13, odIndexes[3], 0x3, 0x10, 0,1,2);
            co.addSyncListener(new SyncListener());

            System.out.println("CanOpen configured");
            System.out.println("CanOpen Starting");
            in.start();
            co.start();
                
            co.join();               
            reset();
            System.out.println("co.start() is finished");
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
//        return(co.waitForExit());
        return(false);
    }
    
    
    public static void main(String args[]){
        boolean retval;
        do
        {
            retval = new DataLogger().launch("test.conf");
            System.out.println(retval);
        }
        while(retval);
        System.out.println("exit");
    }
}
