package Framework;

import utils.Constants;

import java.util.*;

public class UnitInNode {
    public ArrayList<?> unitID; //unit���ĵ������
    public int pointCnt; //unit�е������
    public int isSafe; //unit�İ�ȫ״̬ 0-outlier 1-undetermined 2-safe
    public int deltaCnt; //���ֵĵ������£����º���
    public HashMap<Integer, Integer> isUpdated; //unit�ĸ���״̬ nodeHashCode -> 1/0
    public Set<Integer> belongedDevices;

    public UnitInNode(ArrayList<?> unitID, int pointCnt) {
        this.unitID = unitID;
        this.pointCnt = pointCnt;
        this.deltaCnt = 0;
        this.isUpdated = new HashMap<>();
        for (Integer hashcode : EdgeNodeNetwork.nodeHashMap.keySet()) {
            // initially we put 1 into it, indicating the first time we need to get the data
            isUpdated.put(hashcode, 1);
        }
        this.belongedDevices = Collections.synchronizedSet(new HashSet<>());
    }

    public UnitInNode(UnitInNode x) {
        this.unitID = new ArrayList<>(x.unitID);
        this.pointCnt = x.pointCnt;
        this.isSafe = x.isSafe;
        this.deltaCnt = x.deltaCnt;
        this.isUpdated = new HashMap<>(x.isUpdated);
        this.belongedDevices = new HashSet<>(x.belongedDevices);
    }

    /* update safeness */
    public void updateSafeness() {
        if (pointCnt > Constants.K) {
            this.isSafe = 2;
        } else {
            this.isSafe = 1;
        }
    }

    public synchronized void update() {
        isUpdated.replaceAll((k, v) -> 1);
    }

    public synchronized void updateCount(int cnt) {
        this.pointCnt += cnt;
    }

    int n = 0;

    public synchronized void updateDeltaCount(int cnt) {
        if (this.n == Constants.dn) {
            this.n = 0;
            this.deltaCnt = 0;
        }
        this.deltaCnt += cnt;
        this.n++;
    }

    /**
     * @description if the two object has the same unitID, then we judge them as equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        UnitInNode unitInNode = (UnitInNode) obj;
        return this.unitID.equals(unitInNode.unitID);
    }
}
