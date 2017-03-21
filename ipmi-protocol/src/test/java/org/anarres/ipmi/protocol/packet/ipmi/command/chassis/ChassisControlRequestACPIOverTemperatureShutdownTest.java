package org.anarres.ipmi.protocol.packet.ipmi.command.chassis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSession;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi20SessionWrapper;
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

public class ChassisControlRequestACPIOverTemperatureShutdownTest {

	private static final String host = "192.168.0.69";
	private static final int port = 8007;

	private static final int WIRE_LENGTH = 24;
	private static final byte[] byteSequence = {
			0x06, 0x00, 0x00, 0x07, 0x06, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 
			0x20, 0x00, (byte) 0xe0, (byte) 0x81, 0x00, 0x02, 0x05, 0x78 
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
	public void testMarshal() throws Exception {
		RmcpPacket packet = new RmcpPacket();
		
		packet.withSequenceNumber((byte)0);
		packet.withRemoteAddress(new InetSocketAddress(host, port));
		
		Ipmi20SessionWrapper data = new Ipmi20SessionWrapper();
		
		IpmiSession session = new IpmiSession(0);
		session.setAuthenticationAlgorithm(IpmiAuthenticationAlgorithm.RAKP_NONE);
		session.setConfidentialityAlgorithm(IpmiConfidentialityAlgorithm.NONE);
		session.setIntegrityAlgorithm(IpmiIntegrityAlgorithm.NONE);
	
		ChassisControlRequest request = new ChassisControlRequest();
		request.chassisCommand = ChassisControlRequest.ChassisCommand.ACPIOverTemperatureShutdown;
		
		data.setIpmiPayload(request);
		data.setIpmiSessionId(0);
		data.setIpmiSessionSequenceNumber(0);
		data.setEncrypted(false);
		
		packet.withData(data);
	
		final int wireLength = packet.getWireLength(context);
        ByteBuffer buf = ByteBuffer.allocate(wireLength);
        packet.toWire(context, buf);
        buf.flip();
     
		assertEquals(WIRE_LENGTH, wireLength);
        assertArrayEquals(byteSequence, buf.array());
	
	}

	@Test
	public void testUnmarshal() {
		RmcpPacket packet = new RmcpPacket();
		
		
        ByteBuffer buf = ByteBuffer.allocate(WIRE_LENGTH);
        buf.put(byteSequence);
        buf.flip();
        
        packet.fromWire(context, buf);
        
        assertEquals(0, packet.getSequenceNumber());
        assertEquals(RmcpMessageClass.IPMI, packet.getMessageClass());
        assertEquals(RmcpMessageRole.REQ, packet.getMessageRole());
        
        Ipmi20SessionWrapper session = (Ipmi20SessionWrapper)(packet.getData());
        
        ChassisControlRequest request = (ChassisControlRequest) session.getIpmiPayload();

        assertEquals(IpmiPayloadType.IPMI, request.getPayloadType());
        
        assertEquals(ChassisControlRequest.ChassisCommand.ACPIOverTemperatureShutdown, request.chassisCommand);
	}
}
