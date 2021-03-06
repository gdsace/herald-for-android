//  Copyright 2020 VMware, Inc.
//  SPDX-License-Identifier: Apache-2.0
//

package com.vmware.herald.sensor.datatype;

import com.vmware.herald.sensor.ble.BLESensorConfiguration;

import org.json.JSONObject;

import java.util.UUID;

/// Legacy payload data received from target
public class LegacyPayloadData extends PayloadData {
    public final UUID service;
    public enum ProtocolName {
        UNKNOWN, NOT_AVAILABLE, HERALD, OPENTRACE, ADVERT
    }

    public LegacyPayloadData(final UUID service, final byte[] value) {
        super(value);
        this.service = service;
    }

    public ProtocolName protocolName() {
        if (service == null) {
            return ProtocolName.NOT_AVAILABLE;
        } else if (service == BLESensorConfiguration.interopOpenTraceServiceUUID) {
            return ProtocolName.OPENTRACE;
        } else if (service == BLESensorConfiguration.interopAdvertBasedProtocolServiceUUID) {
            return ProtocolName.ADVERT;
        } else if (service == BLESensorConfiguration.serviceUUID) {
            return ProtocolName.HERALD;
        } else {
            return ProtocolName.UNKNOWN;
        }
    }

    @Override
    public String shortName() {
        // Decoder for test payload to assist debugging of OpenTrace interop
        if (service == BLESensorConfiguration.interopOpenTraceServiceUUID) {
            try {
                final JSONObject jsonObject = new JSONObject(new String(value));
                final String base64EncodedPayloadData = jsonObject.getString("id");
                final PayloadData payloadData = new PayloadData(base64EncodedPayloadData);
                return payloadData.shortName();
            } catch (Throwable e) {
                // Errors are expected for real payloads
            }
        }
        return super.shortName();
    }
}
