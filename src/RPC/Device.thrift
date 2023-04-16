namespace java RPC

/**
 * Structs can also be exceptions, if they are nasty.
 */
exception InvalidException {
  1: i32 what,
  2: string why
}

struct UnitInNode {
   1: list<double> unitID,
   2: i32 pointCnt,
   3: i32 isSafe,
   4: i32 deltaCnt,
   5: map<i32,i32> isUpdated,
   6: set<i32> belongedDevices
}

struct Vector {
   1: list<double> values,
   2: i32 arrivalTime,
   3: i32 slideID,
}

/**
 * Ahh, now onto the cool part, defining a service. Services just need a name
 * and can optionally inherit from another service using the extends keyword.
 */
service DeviceService {
  /**
   * A method definition looks like C code. It has a return type, arguments,
   * and optionally a list of exceptions that it may throw. Note that argument
   * lists and exception lists are specified using the exact same syntax as
   * field lists in struct or exception definitions.
   */
   map<list<double>, list<Vector>> sendData(1: set<list<double>> bucketIds, 2: i32 deviceHashCode),
   void getExternalData(1: map<list<double>, i32> status, 2: map<i32, set<list<double>>> result) throws (1: InvalidException invalid)

}
