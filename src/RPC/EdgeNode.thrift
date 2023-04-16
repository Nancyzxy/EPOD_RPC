
include "Device.thrift"
namespace java RPC

service EdgeNodeService {
  /**
   * A method definition looks like C code. It has a return type, arguments,
   * and optionally a list of exceptions that it may throw. Note that argument
   * lists and exception lists are specified using the exact same syntax as
   * field lists in struct or exception definitions.
   */
   void receiveAndProcessFP(1: map<list<double>, i32> fingerprints, 2: i32 edgeDeviceHashCode) throws (1: Device.InvalidException invalid),
   void processResult(1:list<double> unitID, 2:list<Device.UnitInNode> unitInNodeList),
   void provideNeighborsResult(1:list<list<double>> unSateUnits, 2: i32 edgeNodeHash)
}