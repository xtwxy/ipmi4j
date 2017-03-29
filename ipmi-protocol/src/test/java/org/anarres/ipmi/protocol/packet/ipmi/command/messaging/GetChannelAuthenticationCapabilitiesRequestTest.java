package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSession;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi20SessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiChannelPrivilegeLevel;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayloadType;
import org.anarres.ipmi.protocol.packet.ipmi.security.IpmiAuthenticationAlgorithm;
import org.anarres.ipmi.protocol.packet.ipmi.security.IpmiConfidentialityAlgorithm;
import org.anarres.ipmi.protocol.packet.ipmi.security.IpmiIntegrityAlgorithm;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageRole;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetChannelAuthenticationCapabilitiesRequestTest {

	private static final String host = "192.168.0.69";
	private static final int port = 8007;

	private static final byte[] WIRE = {
				0x06, 0x00, 0x00, 0x07, 0x06, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x09, 0x00, 
				0x20, 0x18, (byte) 0xc8, (byte) 0x81, 0x00, 0x38, (byte) 0x8e, 0x04, 
				(byte) 0xb5, 
			};

	private IpmiPacketContext context;
	
	@Before
	public void setUp() throws Exception {
		context = new IpmiSessionManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToWireData() throws Exception {
		RmcpPacket packet = new RmcpPacket();
		
		packet.withSequenceNumber((byte)0);
		packet.withRemoteAddress(new InetSocketAddress(host, port));
		
		Ipmi20SessionWrapper data = new Ipmi20SessionWrapper();
		
		IpmiSession session = new IpmiSession(0);
		session.setAuthenticationAlgorithm(IpmiAuthenticationAlgorithm.RAKP_NONE);
		session.setConfidentialityAlgorithm(IpmiConfidentialityAlgorithm.NONE);
		session.setIntegrityAlgorithm(IpmiIntegrityAlgorithm.NONE);
	
		GetChannelAuthenticationCapabilitiesRequest request = new GetChannelAuthenticationCapabilitiesRequest();
		request.channelPrivilegeLevel = IpmiChannelPrivilegeLevel.Administrator;
		data.setIpmiPayload(request);
		data.setIpmiSessionId(0);
		data.setIpmiSessionSequenceNumber(0);
		data.setEncrypted(false);
		
		packet.withData(data);
	
		final int wireLength = packet.getWireLength(context);
		
        ByteBuffer buf = ByteBuffer.allocate(wireLength);
        packet.toWire(context, buf);
        buf.flip();
        
        assertEquals(WIRE.length, wireLength);
        assertArrayEquals(WIRE, buf.array());
	
	}

	@Test
	public void testFromWireData() {
		RmcpPacket packet = new RmcpPacket();
		
		
        ByteBuffer buf = ByteBuffer.allocate(WIRE.length);
        buf.put(WIRE);
        buf.flip();
        
        packet.fromWire(context, buf);
        
        assertEquals(0, packet.getSequenceNumber());
        assertEquals(RmcpMessageClass.IPMI, packet.getMessageClass());
        assertEquals(RmcpMessageRole.REQ, packet.getMessageRole());
        
        Ipmi20SessionWrapper data = (Ipmi20SessionWrapper)packet.getData();
        GetChannelAuthenticationCapabilitiesRequest request = (GetChannelAuthenticationCapabilitiesRequest) data.getIpmiPayload();
        		
        assertEquals(IpmiPayloadType.IPMI, 
        		((Ipmi20SessionWrapper)(packet.getData())).getIpmiPayload().getPayloadType());
		
        assertEquals(IpmiChannelPrivilegeLevel.Administrator, request.channelPrivilegeLevel);
        assertEquals(true, request.extendedCapabilities);
		
	}

}
