package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.ipmi.Ipmi15SessionWrapper;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiLun;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.command.UnknownIpmiRequest;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayloadType;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageRole;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnknownRequestTest {

	private static final byte RMCP_PACKET_SEQ_NUMBER = (byte) 0xFF;
	private static final byte IPMI_REQ_SEQ_NUMBER = (byte) 0x05;
	private static final int IPMI_SSN_SEQ_NUMBER = 15413991;
	private static final int IPMI_SESSION_ID = 0xcafe;

	private static final byte[] WIRE = {
			0x06, 0x00, (byte)0xff, 0x07, 0x02, (byte)0xe7, 0x32, (byte)0xeb, 
			0x00, (byte)0xfe, (byte)0xca, 0x00, 0x00, 0x49, 0x79, 0x0f,
			(byte)0x8d, (byte)0xe7, (byte)0x9b, (byte)0xbe, 0x1e, 0x43, 0x26, 0x24, 
			(byte)0xfe, (byte)0x97, 0x66, (byte)0x91, (byte)0xe8, 0x09, 0x20, (byte)0xb0,
			0x30, (byte)0x81, 0x14, 0x3e, 0x00, 0x02, 0x2b
		};
	private static final byte SOURCE_ADDRESS = (byte) 0x81;
	private static final IpmiLun SOURCE_LUN = IpmiLun.L0;
	private static final byte TARGET_ADDRESS = 0x20;

	private IpmiPacketContext context;
	
	@Before
	public void setUp() throws Exception {
		context = new IpmiSessionManager();
	}

	@After
	public void tearDown() throws Exception {
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
        UnknownIpmiRequest request = (UnknownIpmiRequest) session.getIpmiPayload();
        
        assertEquals(IpmiPayloadType.IPMI, 
        		session.getIpmiPayload().getPayloadType());
        
        assertEquals(SOURCE_ADDRESS, request.getSourceAddress());
        assertEquals(SOURCE_LUN, request.getSourceLun());
        
        assertEquals(TARGET_ADDRESS, request.getTargetAddress());
        assertEquals(SOURCE_LUN, request.getTargetLun());
        assertEquals(IPMI_REQ_SEQ_NUMBER, request.getSequenceNumber());
	}

}
