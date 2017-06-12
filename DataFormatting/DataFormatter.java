package DataFormatting;

import DataRecording.AccelerometerReading;
import DataRecording.SubIndexValue;
import DataRecording.NodeTracker;

import java.util.ArrayList;

/**
 * Created by gcdc on 6/7/17.
 */
public class DataFormatter {
//(elapsedTime),node1X,node1Y,node1Z,node2X,node2Y,node2Z...
    public DataFormatter(){}
    public String produceOutputString(AccelerometerReading... readings){
        String retVal = new String();
        for(AccelerometerReading reading : readings){
            retVal = retVal.concat(reading.toString());
        }
        return retVal;
    }
}
