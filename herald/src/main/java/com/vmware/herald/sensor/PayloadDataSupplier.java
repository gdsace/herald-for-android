//  Copyright 2020 VMware, Inc.
//  SPDX-License-Identifier: Apache-2.0
//

package com.vmware.herald.sensor;

import com.vmware.herald.sensor.datatype.Data;
import com.vmware.herald.sensor.datatype.LegacyPayloadData;
import com.vmware.herald.sensor.datatype.PayloadData;
import com.vmware.herald.sensor.datatype.PayloadTimestamp;

import java.util.List;

/// Payload data supplier, e.g. BeaconCodes in C19X and BroadcastPayloadSupplier in Sonar.
public interface PayloadDataSupplier {
    /// Legacy payload supplier callback - for those transitioning their apps to Herald. Note: Device may be null if Payload in use is same for all receivers
    LegacyPayloadData legacyPayload(PayloadTimestamp timestamp, Device device);

    /// Get payload for given timestamp. Use this for integration with any payload generator, e.g. BeaconCodes or SonarBroadcastPayloadService
    PayloadData payload(PayloadTimestamp timestamp, Device device);

    /// Parse raw data into payloads
    List<PayloadData> payload(Data data);
}
