package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi15SessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiLun;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayloadType;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageRole;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetSessionChallengeResponseTest {

	private static final byte RMCP_PACKET_SEQ_NUMBER = (byte) 0xFF;
	private static final byte IPMI_REQ_SEQ_NUMBER = (byte) 0x02;
	private static final int IPMI_SSN_SEQ_NUMBER = 0x0;
	private static final int IPMI_SESSION_ID = 0x0;
	private static final int TEMP_SESSION_ID = 0x0;
	private static final String host = "192.168.0.69";
	private static final int port = 8007;

	private static final byte[] WIRE = {
				0x06, 0x00, (byte) 0xff, 0x07, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x1c, 0x20, 0x1c, 
				(byte) 0xc4, (byte) 0x81, 0x08, 0x39, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x61, 0x64, 0x6d, 0x69, 0x6e, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x35
			};
	private static final byte SOURCE_ADDRESS = (byte) 0x81;
	private static final IpmiLun SOURCE_LUN = IpmiLun.L0;
	private static final byte TARGET_ADDRESS = 0x20;
	private static final IpmiLun TARGET_LUN = IpmiLun.L0;
	private static final byte[] USER_NAME = {'a', 'd', 'm', 'i', 'n', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

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
		
		packet.withSequenceNumber(RMCP_PACKET_SEQ_NUMBER);
		packet.withRemoteAddress(new InetSocketAddress(host, port));
		
		Ipmi15SessionWrapper data = new Ipmi15SessionWrapper();
		
		GetSessionChallengeResponse response = new GetSessionChallengeResponse();
		
		response.withSource(SOURCE_ADDRESS, SOURCE_LUN);
		response.withTarget(TARGET_ADDRESS, TARGET_LUN);
		response.withTemporarySessionId(TEMP_SESSION_ID);
		response.withChallengeStringData(USER_NAME);
		response.setSequenceNumber(IPMI_REQ_SEQ_NUMBER);
		
		data.setIpmiPayload(response);
		data.setIpmiSessionId(IPMI_SESSION_ID);
		data.setIpmiSessionSequenceNumber(IPMI_SSN_SEQ_NUMBER);
		
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
        
		assertEquals(RMCP_PACKET_SEQ_NUMBER, packet.getSequenceNumber());
        assertEquals(RmcpMessageClass.IPMI, packet.getMessageClass());
        assertEquals(RmcpMessageRole.REQ, packet.getMessageRole());
        
        Ipmi15SessionWrapper session = (Ipmi15SessionWrapper)packet.getData();
        assertEquals(IPMI_SESSION_ID, session.getIpmiSessionId());
        assertEquals(IPMI_SSN_SEQ_NUMBER, session.getIpmiSessionSequenceNumber());
        
        GetSessionChallengeResponse response = (GetSessionChallengeResponse) session.getIpmiPayload();
        		
        assertEquals(IpmiPayloadType.IPMI, 
        		session.getIpmiPayload().getPayloadType());
        
        assertEquals(SOURCE_ADDRESS, response.getSourceAddress());
        assertEquals(SOURCE_LUN, response.getSourceLun());
        
        assertEquals(TARGET_ADDRESS, response.getTargetAddress());
        assertEquals(SOURCE_LUN, response.getTargetLun());
		
		assertEquals(TEMP_SESSION_ID, response.getTemporarySessionId());
		assertEquals(USER_NAME, response.getChallengeStringData());
        assertEquals(IPMI_REQ_SEQ_NUMBER, response.getSequenceNumber());
	}


}
