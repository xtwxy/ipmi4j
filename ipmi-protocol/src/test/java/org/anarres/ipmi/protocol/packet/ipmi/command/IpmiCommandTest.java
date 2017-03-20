/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi.command;

import static org.junit.Assert.assertArrayEquals;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi15SessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiChannelPrivilegeLevel;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiLun;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.command.messaging.GetChannelAuthenticationCapabilitiesRequest;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.UnsignedBytes;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * @author shevek
 */
public class IpmiCommandTest {

    private static final Logger LOG = LoggerFactory.getLogger(IpmiCommandTest.class);

    private static class Formatter extends LoggingHandler {

        @Nonnull
        public String format(@Nonnull String name, @Nonnull ByteBuf buf) {
            return super.formatByteBuf(name, buf);
        }
    }
    private final Formatter formatter = new Formatter();
    private final IpmiPacketContext context = new IpmiSessionManager();

    @Nonnull
    private static byte[] toByteArray(@Nonnull int... ints) {
        byte[] out = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            out[i] = UnsignedBytes.checkedCast(ints[i]);
        return out;
    }

    @Test
    public void testMessages() {
        GetChannelAuthenticationCapabilitiesRequest request = new GetChannelAuthenticationCapabilitiesRequest();
        request.withSource(0x81, IpmiLun.L0);
        request.withTarget(0x20, IpmiLun.L0);
        request.extendedCapabilities = true;
        request.channelPrivilegeLevel = IpmiChannelPrivilegeLevel.Administrator;

        IpmiSessionWrapper data = new Ipmi15SessionWrapper();
        data.setIpmiPayload(request);

        RmcpPacket packet = new RmcpPacket();
        packet.withData(data);


        ByteBuffer buf = ByteBuffer.allocate(packet.getWireLength(context));
        packet.toWire(context, buf);
        buf.flip();
        LOG.info(formatter.format("Request", Unpooled.wrappedBuffer(buf)));

        byte[] expect = toByteArray(0x06, 0x00, 0xff, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x09, 0x20, 0x18, 0xc8, 0x81, 0x00, 0x38, 0x8e, 0x04, 0xb5);
        assertArrayEquals(expect, buf.array());

        buf = ByteBuffer.wrap(expect);
        packet = new RmcpPacket();
        packet.fromWire(context, buf);
        LOG.info("Packet is\n" + packet);
    }
}