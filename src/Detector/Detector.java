package Detector;

import DataStructure.Vector;
import Framework.Device;
import java.util.*;

public abstract class Detector {
    public Set<? extends Vector> outlierVector; // This field is only used to return to the global network
    public Map<Integer, Map<ArrayList<?>, List<Vector>>> externalData;
    public HashMap<ArrayList<?>, Integer> status;
    Device device;
    public Detector(Device device){
        this.device = device;
        this.externalData = Collections.synchronizedMap(new HashMap<>());
    }
    public abstract void detectOutlier(List<Vector> data);

    //pruning + 后续处理
    public abstract void processOutliers();

    public abstract Map<ArrayList<?>,List<Vector>> sendData(HashSet<ArrayList<?>> bucketIds, int lastSent);
}
