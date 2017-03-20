/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.asf;

import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.anarres.ipmi.protocol.IanaEnterpriseNumber;
import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.visitor.IpmiClientRmcpMessageHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.common.AbstractWireable;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;

import com.google.common.primitives.UnsignedBytes;

/**
 * ASF RMCP Data (Enterprise number 0x4542).
 *
 * http://www.dmtf.org/sites/default/files/standards/documents/DSP0136.pdf
 * http://www.dmtf.org/standards/asf
 * [ASF2] Section 3.2.2.3 page 22.
 * [IPMI2] Section 13.2 pages 126-127.
 * 
 * This class manages both the RMCP payload and the ASF payload.
 * Implementors desiring to provide the ASF payload should override
 * {@link #getDataWireLength()} and {@link #toWireData(ByteBuffer)}
 * and ignore the RMCP wrapper.
 *
 * @author shevek
 */
public abstract class AbstractAsfData extends AbstractWireable implements AsfRmcpData {

    public static final IanaEnterpriseNumber IANA_ENTERPRISE_NUMBER = IanaEnterpriseNumber.Alerting_Specifications_Forum;
    // Page 33
    private byte messageTag;    // matches request/response

    @Override
    public RmcpMessageClass getMessageClass() {
        return RmcpMessageClass.ASF;
    }

    @Override
    public void apply(IpmiClientRmcpMessageHandler handler, IpmiHandlerContext context) {
        handler.handleAsfRmcpData(context, this);
    }

    public byte getMessageTag() {
        return messageTag;
    }

    public void setMessageTag(byte messageTag) {
        this.messageTag = messageTag;
    }

    @Override
    public int getWireLength(IpmiPacketContext context) {
        return 0
                + 4 // ianaEnterpriseNumber
                + 1 // message type
                + 1 // message tag
                + 1 // data length
                + 1 // reserved
                + getDataWireLength();
    }

    /** Returns the length of the ASF data part of this packet. */
    @Nonnegative
    protected abstract int getDataWireLength();

    @Override
    protected void toWireUnchecked(IpmiPacketContext context, ByteBuffer buffer) {
        buffer.putInt(IANA_ENTERPRISE_NUMBER.getNumber());
        buffer.put(getMessageType().getCode());
        buffer.put(getMessageTag());
        buffer.put((byte) 0);   // reserved
        buffer.put(UnsignedBytes.checkedCast(getDataWireLength()));
        toWireData(buffer);
    }

    /** Serializes the ASF data into this RMCP data. */
    protected abstract void toWireData(@Nonnull ByteBuffer buffer);

    @Override
    protected void fromWireUnchecked(IpmiPacketContext context, ByteBuffer buffer) {
        assertWireInt(buffer, IANA_ENTERPRISE_NUMBER.getNumber(), "IANA enterprise number");
        assertWireByte(buffer, getMessageType().getCode(), "message type code");
        setMessageTag(buffer.get());
        assertWireByte(buffer, (byte) 0, "reserved byte");
        int length = buffer.get() & 0xFF;   // Can't validate until we have the data. :-(
        fromWireData(buffer);
        // TODO: Validate data length on read.
    }

    protected abstract void fromWireData(@Nonnull ByteBuffer buffer);
}
