package DataFormatting;

import DataRecording.AccelerometerReading;
import DataRecording.SubIndexValue;
import DataRecording.NodeTracker;
import GlobalVars.GlobalVars;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * Created by gcdc on 6/7/17.
 */
public class DataFormatter {
//(elapsedTime),node1X,node1Y,node1Z,node2X,node2Y,node2Z...
    public DataFormatter(){}

    /**
     *
     * @param readings A list of readings from the accelerometers
     * @return Example output:  Elapsed Time: 208441739
                                X: 2731, Y: 3343, Z: 2192
                                Index: 0x6210
                                Elapsed Time: 208477156
                                X: 2502, Y: 1504, Z: 2305
                                Index: 0x6211
                                Elapsed Time: 208501947
                                X: 2787, Y: 1825, Z: 1820
                                Index: 0x6212
                                Elapsed Time: 208527208
                                X: 2455, Y: 3095, Z: 3201
                                Index: 0x6213
     */
    public String producePrettyOutputString(AccelerometerReading... readings){
        String retVal = new String();
        for(AccelerometerReading reading : readings){
            retVal = retVal.concat(reading.toString());
        }
        return retVal;

    }

    /**
     *
     * @param indexes A list of indexes that we recieve readings from
     * @return A header comment
     */
    public String produceHeader(int[] indexes){
        String header = new String(";").concat(new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss:SS z").format(new Date()));
        for(int index : indexes){
            header = header.concat(","+Integer.toHexString(index)+"(X)");
            header = header.concat(","+Integer.toHexString(index)+"(Y)");
            header = header.concat(","+Integer.toHexString(index)+"(Z)");
        }
        return header;
    }

    public String produceOutputLine(AccelerometerReading[] readings){
        String output = new String("");
        //NOTE:
        //  The elapsed time value will be different for each reading
        //  even though they were all gathered at the same Sync message.
        //  The timestamp of the first reading will be used on the line
        //  because that is assumed to be the most accurate. Luckily
        //  the margin of error is only a few ten thousandths of a second
        //  so it shouldn't cause any problems.
        output = output.concat(((Long)readings[0].getElapsedTime()).toString());
        for(AccelerometerReading reading : readings){
            output = output.concat(","+Integer.toString(reading.getX()));
            output = output.concat(","+Integer.toString(reading.getY()));
            output = output.concat(","+Integer.toString(reading.getZ()));
        }
        return output;
    }
}
