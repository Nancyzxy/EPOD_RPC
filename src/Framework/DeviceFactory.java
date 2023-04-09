package Framework;

public class DeviceFactory {
    public Device createEdgeDevice(int deviceId) {
        return new Device(deviceId);
    }
}
