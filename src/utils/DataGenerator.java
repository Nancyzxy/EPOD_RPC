package utils;

import RPC.Vector;
import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataGenerator {

    public PriorityQueue<Vector> dataQueue;
    public Date firstTimeStamp;

    public DataGenerator(int deviceId) {
        this.getData(Constants.timePrefix + deviceId + ".txt");
        assert this.dataQueue.peek() != null;
        this.firstTimeStamp = this.dataQueue.peek().arrivalRealTime;
    }


    public ArrayList<Vector> getTimeBasedIncomingData(Date currentTime, int lengthInSecond) {
        ArrayList<Vector> results = new ArrayList<>();
        Date endTime = new Date();
        endTime.setTime(currentTime.getTime() + lengthInSecond * 1000L);
        Vector d = dataQueue.peek();
        while (d != null && d.arrivalRealTime.compareTo(currentTime) >= 0
                && d.arrivalRealTime.compareTo(endTime) < 0) {
            d.slideID = Constants.currentSlideID;
            results.add(d);
            dataQueue.poll();
            d = dataQueue.peek();
        }
        return results;
    }


    public void getData(String filename) {
        Comparator<Vector> comparator = new DataComparatorWithTimestamp();
        dataQueue = new PriorityQueue<>(comparator);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(filename));
            try {
                String line = bfr.readLine();
                int id = 0;
                while (line != null) {
                    String[] atts = line.split(",");
                    ArrayList<Double> d = new ArrayList<>();
                    for (int i = 1; i < atts.length; i++) {
                        d.set(i - 1, Double.parseDouble(atts[i]) + (new Random()).nextDouble() / 10000000);
                    }
                    Vector data = new Vector(d);
                    data.arrivalRealTime = formatter.parse(atts[0]);
                    data.arrivalTime = id;
                    id++;
                    dataQueue.add(data);
                    line = bfr.readLine();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

class DataComparatorWithTimestamp implements Comparator<Vector> {
    @Override
    public int compare(Vector x, Vector y) {
        return x.arrivalRealTime.compareTo(y.arrivalRealTime);
    }
}
