package Framework;

import Detector.Detector;
import Detector.MCOD;
import Detector.NewNETS;
import RPC.RPCFrame;
import DataStructure.Vector;
import utils.Constants;
import utils.DataGenerator;

import java.util.*;

@SuppressWarnings("unchecked")
public class Device extends RPCFrame implements Runnable {
    public int deviceId;
    volatile public boolean ready = false;
    public List<Vector> rawData = new ArrayList<>();
    public HashMap<ArrayList<?>, Integer> fullCellDelta; //fingerprint
    public DataGenerator dataGenerator;
    public EdgeNode nearestNode;
    public Detector detector;
    public HashMap<Integer, Integer> historyRecord; //用来记录每个device的上次发送的历史记录，deviceID->slideID


    public Device(int deviceId) {
        this.port = new Random().nextInt(50000) + 10000;
        this.deviceId = deviceId;
        this.dataGenerator = new DataGenerator(deviceId);
        if (Objects.equals(Constants.methodToGenerateFingerprint, "NETS")) {
            this.detector = new NewNETS(0, this);
        } else if (Objects.equals(Constants.methodToGenerateFingerprint, "MCOD")) {
            this.detector = new MCOD(this);
        }
        this.fullCellDelta = new HashMap<>();
        this.historyRecord = new HashMap<>();
        for (int deviceHashCode : EdgeNodeNetwork.deviceHashMap.keySet()) {
            this.historyRecord.put(deviceHashCode, 0);
        }
    }

    public Set<? extends Vector> detectOutlier(int itr) throws Throwable {
        this.ready = false;
        //get initial data
        Constants.currentSlideID = itr;
        Date currentRealTime = new Date();
        currentRealTime.setTime(dataGenerator.firstTimeStamp.getTime() + (long) Constants.S * 10 * 1000 * itr);
        this.rawData = dataGenerator.getTimeBasedIncomingData(currentRealTime, Constants.S * 10);

        //step1: 产生指纹 + 本地先检测出outliers
        if (itr > Constants.nS - 1) {
            clearFingerprints();
        }
        this.detector.detectOutlier(this.rawData);

        //step2: 上传指纹
        if (itr >= Constants.nS - 1) {
            uploadFP(fullCellDelta);
        }

        //本地获取数据 + 处理outliers
        while (!this.ready) {
        }
        this.detector.processOutliers();
        return this.detector.outlierVector;
    }


    public void uploadFP(HashMap<ArrayList<?>, Integer> aggFingerprints) throws Throwable {
        Object[] parameters = new Object[]{aggFingerprints, this.hashCode()};
        invoke("localhost", this.nearestNode.port,
                EdgeNode.class.getMethod("receiveAndProcessFP", HashMap.class, Integer.class), parameters);
    }

    public void clearFingerprints() {
        this.fullCellDelta = new HashMap<>();
    }

    public Map<ArrayList<?>, List<Vector>> sendData(HashSet<ArrayList<?>> bucketIds, int deviceHashCode) {
        //根据历史记录来发送数据
        int lastSent = Math.max(this.historyRecord.get(deviceHashCode), Constants.currentSlideID - Constants.nS);
        this.historyRecord.put(deviceHashCode, Constants.currentSlideID);
        return this.detector.sendData(bucketIds, lastSent);
    }

    public void getExternalData(HashMap<ArrayList<?>, Integer> status, HashMap<Integer, HashSet<ArrayList<?>>> result) throws InterruptedException {
        this.detector.status = status; //用来判断outliers是否需要重新计算，用在processOutliers()中
        ArrayList<Thread> threads = new ArrayList<>();
        for (Integer deviceCode : result.keySet()) {
            //HashMap<Integer,HashSet<ArrayList<?>>>
            Thread t = new Thread(() -> {
                Object[] parameters = new Object[]{result.get(deviceCode)};
                try {
                    Map<ArrayList<?>, List<Vector>> data = (Map<ArrayList<?>, List<Vector>>)
                            invoke("localhost", EdgeNodeNetwork.deviceHashMap.get(deviceCode).port,
                                    Device.class.getMethod("sendData", HashSet.class, int.class), parameters);
                    if (!this.detector.externalData.containsKey(Constants.currentSlideID)) {
                        this.detector.externalData.put(Constants.currentSlideID, Collections.synchronizedMap(new HashMap<>()));
                    }
                    Map<ArrayList<?>, List<Vector>> map = this.detector.externalData.get(Constants.currentSlideID);//TODO: Check 并发的问题
                    data.keySet().forEach(
                            x -> {
                                if (!map.containsKey(x)) {
                                    map.put(x, Collections.synchronizedList(new ArrayList<>()));
                                }
                                map.get(x).addAll(data.get(x));
                            }
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        this.ready = true;
    }

    public void setNearestNode(EdgeNode nearestNode) {
        this.nearestNode = nearestNode;
    }
}
