//  Copyright 2020 VMware, Inc.
//  SPDX-License-Identifier: Apache-2.0
//

package com.vmware.herald.sensor.payload.test;

import com.vmware.herald.sensor.Device;
import com.vmware.herald.sensor.ble.BLEDevice;
import com.vmware.herald.sensor.ble.BLESensorConfiguration;
import com.vmware.herald.sensor.datatype.Data;
import com.vmware.herald.sensor.datatype.Int32;
import com.vmware.herald.sensor.datatype.LegacyPayloadData;
import com.vmware.herald.sensor.datatype.PayloadData;
import com.vmware.herald.sensor.datatype.PayloadTimestamp;
import com.vmware.herald.sensor.datatype.RSSI;
import com.vmware.herald.sensor.payload.DefaultPayloadDataSupplier;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/// Test payload data supplier for generating fixed payload to support evaluation
public class TestPayloadDataSupplier extends DefaultPayloadDataSupplier {
    private final int length;
    private final int identifier;

    public TestPayloadDataSupplier(int identifier) {
        this(identifier, 129);
    }

    public TestPayloadDataSupplier(int identifier, int length) {
        this.identifier = identifier;
        this.length = length;
    }

    @Override
    public PayloadData payload(PayloadTimestamp timestamp, Device device) {
        final PayloadData payloadData = new PayloadData();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        // First 3 bytes are reserved for protocolAndVersion (UInt8) + countryCode (UInt16)
        payloadData.append(new Data((byte) 0, 3));
        // Next 4 bytes are used for fixed cross-platform identifier (Int32)
        payloadData.append(new Int32(identifier));
        // Fill remaining payload with blank data to make payload the test length
        payloadData.append(new Data((byte) 0, length - payloadData.value.length));
        return payloadData;
    }

    @Override
    public LegacyPayloadData legacyPayload(PayloadTimestamp timestamp, Device device) {
        if (!(device instanceof BLEDevice)) {
            return null;
        }
        final BLEDevice bleDevice = (BLEDevice) device;
        final RSSI rssi = bleDevice.rssi();
        final PayloadData payloadData = payload(timestamp, device);
        if (rssi == null || payloadData == null) {
            return null;
        }
        try {
            final JSONObject centralWriteDataV2 = new JSONObject();
            centralWriteDataV2.put("mc", android.os.Build.MODEL); // phone model of central
            centralWriteDataV2.put("rs", (double) rssi.value); // rssi
            centralWriteDataV2.put("id", payloadData.base64EncodedString()); // tempID
            centralWriteDataV2.put("o", "OT_HA"); // organisation
            centralWriteDataV2.put("v", 2); // protocol version
            final byte[] encodedData = centralWriteDataV2.toString().getBytes("UTF8");
            final LegacyPayloadData legacyPayloadData = new LegacyPayloadData(BLESensorConfiguration.interopOpenTraceServiceUUID, encodedData);
            return legacyPayloadData;
        } catch (Throwable e) {
        }
        return null;
    }
}
