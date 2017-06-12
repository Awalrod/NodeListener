package Main;

import DataFormatting.DataFormatter;
import DataRecording.AccelerometerReading;
import DataRecording.NodeTracker;
import com.gcdc.can.CanMessage;
import com.gcdc.can.Driver;
import com.gcdc.can.DriverManager;
import com.gcdc.canopen.*;
import GlobalVars.GlobalVars;

/**
 * Created by gcdc on 6/7/17.
 */
public class DataLogger {
    private CanOpen co;
    private NodeTracker node1, node2, node3, node4;
    private DataFormatter dfmt;
    private class SyncListener implements CanOpenListener{
        @Override
        public void onMessage(CanMessage canMessage) {
            System.out.println("SYNC message received");
            if(GlobalVars.START_TIME == null){
                GlobalVars.START_TIME = System.nanoTime();
            }	
            AccelerometerReading readings[] = new AccelerometerReading[4];
            readings[0] = node1.getLatestReading();
            readings[1] = node2.getLatestReading();
            readings[2] = node3.getLatestReading();
            readings[3] = node4.getLatestReading();
            System.out.println(dfmt.produceOutputString(readings));
        }


        @Override
        public void onObjDictChange(SubEntry se){}
        @Override
        public void onEvent(CanOpen co, int state) {}
    }
    public DataLogger(String confFile){
        dfmt = new DataFormatter();
        Driver drvr;
        try{
            System.out.println("CANbus driver starting");

            DriverManager dm = new DriverManager("datagram", "192.168.1.54", 2000, false);
            drvr = dm.getDriver();
            System.out.println("CANbus driver configured");
            ObjectDictionary od = DefaultOD.create(0x23);
            co = new CanOpen(drvr, od, 0x23, false);

            node1 = new NodeTracker(co,0x281,0x10,0x6210,0x3,0x10, 0,1,2);
            node2 = new NodeTracker(co, 0x282, 0x11, 0x6211, 0x3, 0x10, 0,1,2);
            node3 = new NodeTracker(co, 0x283, 0x12, 0x6212, 0x3, 0x10, 0,1,2);
            node4 = new NodeTracker(co, 0x284, 0x13, 0x6213, 0x3, 0x10, 0,1,2);
            co.addSyncListener(new SyncListener());

            System.out.println("CanOpen configured");
            System.out.println("CanOpen Starting");

            
            co.start();


        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public static void main(String args[]){
        new DataLogger("test.conf");
    }
}
