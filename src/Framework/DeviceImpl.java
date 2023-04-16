package Framework;

import Detector.Detector;
import Detector.MCOD;
import Detector.NewNETS;
import RPC.DeviceService;
import RPC.EdgeNodeService;
import RPC.Vector;
import utils.Constants;
import utils.DataGenerator;

import java.util.*;

@SuppressWarnings("unchecked")
public class DeviceImpl implements DeviceService.Iface {
    public int deviceId;
    volatile public boolean ready = false;
    public List<Vector> rawData = new ArrayList<>();
    public HashMap<List<Double>, Integer> fullCellDelta; //fingerprint
    public DataGenerator dataGenerator;
    public Detector detector;
    public HashMap<Integer, Integer> historyRecord; //用来记录每个device的上次发送的历史记录，deviceID->slideID
    public Map<Integer, DeviceService.Client> clientsForDevices;
    public EdgeNodeService.Client clientsForNearestNode;

    public DeviceImpl(int deviceId) {
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

    public void setClients(EdgeNodeService.Client clientsForNearestNode, Map<Integer,DeviceService.Client> clientsForDevices){
        this.clientsForDevices = clientsForDevices;
        this.clientsForNearestNode = clientsForNearestNode;
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
            this.clientsForNearestNode.receiveAndProcessFP(fullCellDelta, this.hashCode());
        }

        //本地获取数据 + 处理outliers
        while (!this.ready) {
        }
        this.detector.processOutliers();
        return this.detector.outlierVector;
    }


    public void clearFingerprints() {
        this.fullCellDelta = new HashMap<>();
    }

    public Map<List<Double>, List<Vector>> sendData(Set<List<Double>> bucketIds, int deviceHashCode) {
        //根据历史记录来发送数据
        int lastSent = Math.max(this.historyRecord.get(deviceHashCode), Constants.currentSlideID - Constants.nS);
        this.historyRecord.put(deviceHashCode, Constants.currentSlideID);
        return this.detector.sendData(bucketIds, lastSent);
    }

    public void getExternalData(Map<List<Double>, Integer> status, Map<Integer, Set<List<Double>>> result) {
        this.detector.status = status; //用来判断outliers是否需要重新计算，用在processOutliers()中
        ArrayList<Thread> threads = new ArrayList<>();
        for (Integer deviceCode : result.keySet()) {
            //HashMap<Integer,HashSet<ArrayList<?>>>
            Thread t = new Thread(() -> {
                try {
                    Map<List<Double>, List<Vector>> data = this.clientsForDevices.get(deviceCode).sendData(result.get(deviceCode), this.hashCode());
                    if (!this.detector.externalData.containsKey(Constants.currentSlideID)) {
                        this.detector.externalData.put(Constants.currentSlideID, Collections.synchronizedMap(new HashMap<>()));
                    }
                    Map<List<Double>, List<Vector>> map = this.detector.externalData.get(Constants.currentSlideID);//TODO: Check 并发的问题
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
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.ready = true;
    }

}
