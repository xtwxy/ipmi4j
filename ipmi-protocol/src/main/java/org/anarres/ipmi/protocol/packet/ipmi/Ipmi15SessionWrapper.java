/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayload;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayloadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.UnsignedBytes;

/**
 * [IPMI2] Section 13.6 pages 133-134, column 1.
 *
 * @author shevek
 */
public class Ipmi15SessionWrapper extends AbstractIpmiSessionWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(Ipmi15SessionWrapper.class);
    // private IpmiSessionAuthenticationType authenticationType = IpmiSessionAuthenticationType.NONE;
    // private int ipmiSessionSequenceNumber;
    // private int ipmiSessionId;
    private IpmiString ipmiMessageAuthenticationCode = new IpmiString(16);   // 16 bytes
    private IpmiSessionAuthenticationType authenticationType = IpmiSessionAuthenticationType.NONE;

    public Ipmi15SessionWrapper withMessageAuthenticationCode(byte[] code) {
    	this.ipmiMessageAuthenticationCode.setContent(code);
    	return this;
    }
    
    public IpmiSessionAuthenticationType getAuthenticationType() {
    	return this.authenticationType;
    }
    
    public Ipmi15SessionWrapper withAuthenticationType(IpmiSessionAuthenticationType type) {
    	this.authenticationType = type;
    	return this;
    }
    
    public byte[] getMessageAuthenticationCode() {
    	return this.ipmiMessageAuthenticationCode.getContent();
    }
    
    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return !IpmiSessionAuthenticationType.NONE.equals(authenticationType);
    }

    // @Override
    // public int getIpmiSessionId() { return ipmiSessionId; }
    // @Override
    // public int getIpmiSessionSequenceNumber() { return ipmiSessionSequenceNumber; }
    @Override
    public int getWireLength(IpmiPacketContext context) {
        return 1 // authenticationType
                + 4 // ipmiSessionSequenceNumber
                + 4 // ipmiSessionId
                + (IpmiSessionAuthenticationType.NONE.equals(authenticationType) ? 0 : 16)
                + 1 // payloadLength
                + getIpmiPayload().getWireLength(context);
    }

    /** Sequence number handling: [IPMI2] Section 6.12.8, page 58. */
    @Override
    public void toWireUnchecked(IpmiPacketContext context, ByteBuffer buffer) {
        // Page 133
        buffer.put(authenticationType.getCode());
        toWireIntLE(buffer, getIpmiSessionSequenceNumber());
        toWireIntLE(buffer, getIpmiSessionId());
        if (authenticationType != IpmiSessionAuthenticationType.NONE) {
            buffer.put(ipmiMessageAuthenticationCode.getContent());
        }
        // Page 134
        IpmiPayload payload = getIpmiPayload();
        int payloadLength = payload.getWireLength(context);
        // LOG.info("payload=" + payload);
        // LOG.info("payloadLength=" + payloadLength);
        buffer.put(UnsignedBytes.checkedCast(payloadLength));

        payload.toWire(context, buffer);
    }

    @Override
    public void fromWireUnchecked(IpmiPacketContext context, ByteBuffer buffer) {
        authenticationType = Code.fromBuffer(IpmiSessionAuthenticationType.class, buffer);
        setIpmiSessionSequenceNumber(fromWireIntLE(buffer));
        setIpmiSessionId(fromWireIntLE(buffer));

        if (authenticationType != IpmiSessionAuthenticationType.NONE) {
            buffer.get(ipmiMessageAuthenticationCode.getContent());
        }
        byte payloadLength = buffer.get();

        ByteBuffer payloadBuffer = buffer.duplicate();
        payloadBuffer.limit(payloadBuffer.position() + UnsignedBytes.toInt(payloadLength));
        buffer.position(payloadBuffer.limit());

        IpmiPayload payload = newPayload(payloadBuffer, IpmiPayloadType.IPMI);
        payload.fromWire(context, payloadBuffer);
        setIpmiPayload(payload);

        // assert payloadLength == header.getWireLength() + payload.getWireLength();
    }
}
