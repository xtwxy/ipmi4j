package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi15SessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiLun;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayloadType;
import org.anarres.ipmi.protocol.packet.ipmi.payload.RequestedMaximumPrivilegeLevel;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageRole;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.anarres.ipmi.protocol.util.ByteArrayPrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivateSessionResponseTest {

	private static final byte RMCP_PACKET_SEQ_NUMBER = (byte) 0xFF;
	private static final byte IPMI_REQ_SEQ_NUMBER = (byte) 0x03;
	private static final int IPMI_SSN_SEQ_NUMBER = (byte) 0x0;
	private static final int IPMI_SESSION_ID = 0xcafe;
	private static final String host = "192.168.0.69";
	private static final int port = 8007;

	private static final byte[] WIRE = {
			0x06, 0x00, (byte)0xff, 0x07, 0x02, 0x00, 0x00, 0x00, 
			0x00, (byte)0xfe, (byte)0xca, 0x00, 0x00, (byte)0xcc, (byte)0x80, 0x48, 
			(byte)0x95, 0x00, 0x2a, (byte)0xd6, (byte)0xe6, (byte)0xb3, 0x7c, 0x2a, 
			0x6d, 0x17, 0x2a, 0x40, 0x4b, 0x12, 0x20, 0x1c, 
			(byte)0xc4, (byte)0x81, 0x0c, 0x3a, 0x00, 0x02, (byte)0xfe, (byte)0xca, 
			0x00, 0x00, 0x6b, (byte)0xee, (byte)0xe5, 0x1c, 0x04, 0x11
			};
	private static final byte SOURCE_ADDRESS = (byte) 0x81;
	private static final IpmiLun SOURCE_LUN = IpmiLun.L0;
	private static final byte TARGET_ADDRESS = 0x20;
	private static final IpmiLun TARGET_LUN = IpmiLun.L0;
	private static final IpmiSessionAuthenticationType AUTHENTICATION_TYPE = IpmiSessionAuthenticationType.MD5;
	private static final int INITIAL_INBOUND_SEQ = 484830827;

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
		
		Ipmi15SessionWrapper sessionWrapper = new Ipmi15SessionWrapper();
		
		ActivateSessionResponse response = new ActivateSessionResponse();
		
		response.withSource(SOURCE_ADDRESS, SOURCE_LUN);
		response.withTarget(TARGET_ADDRESS, TARGET_LUN);
		response.withAuthenticationType(AUTHENTICATION_TYPE);
		response.setSequenceNumber(IPMI_REQ_SEQ_NUMBER);
	
		response.withAuthenticationType(IpmiSessionAuthenticationType.MD5);
		response.withSessionId(IPMI_SESSION_ID);
		response.withInitialInboundSequence(INITIAL_INBOUND_SEQ);
		response.withMaximumPrivilegeLevelAllowed(RequestedMaximumPrivilegeLevel.ADMINISTRATOR);
	
		sessionWrapper.setIpmiPayload(response);
		sessionWrapper.setIpmiSessionId(IPMI_SESSION_ID);
		sessionWrapper.setIpmiSessionSequenceNumber(IPMI_SSN_SEQ_NUMBER);
		sessionWrapper.withAuthenticationType(IpmiSessionAuthenticationType.MD5);
		byte[] code = Arrays.copyOfRange(WIRE, 13, 29);
		sessionWrapper.withMessageAuthenticationCode(code);
		
		packet.withData(sessionWrapper);
		
		final int wireLength = packet.getWireLength(context);
		
        ByteBuffer buf = ByteBuffer.allocate(wireLength);
        packet.toWire(context, buf);
        buf.flip();
        
        ByteArrayPrinter.print(buf.array(), System.out);
        
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
		byte[] code = Arrays.copyOfRange(WIRE, 13, 29);
        assertArrayEquals(code, session.getMessageAuthenticationCode());
        assertEquals(IpmiSessionAuthenticationType.MD5, session.getAuthenticationType());
        ActivateSessionResponse response = (ActivateSessionResponse) session.getIpmiPayload();
        
        assertEquals(INITIAL_INBOUND_SEQ, response.getInitialInboundSequence());
        assertEquals(IpmiPayloadType.IPMI, 
        		session.getIpmiPayload().getPayloadType());
        
        assertEquals(SOURCE_ADDRESS, response.getSourceAddress());
        assertEquals(SOURCE_LUN, response.getSourceLun());
        
        assertEquals(TARGET_ADDRESS, response.getTargetAddress());
        assertEquals(SOURCE_LUN, response.getTargetLun());
		
		assertEquals(AUTHENTICATION_TYPE, response.getAuthenticationType());
        assertEquals(IPMI_REQ_SEQ_NUMBER, response.getSequenceNumber());
	}


}
