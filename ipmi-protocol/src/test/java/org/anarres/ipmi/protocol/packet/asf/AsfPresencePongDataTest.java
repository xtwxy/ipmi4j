package org.anarres.ipmi.protocol.packet.asf;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.session.IpmiPacketContext;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageRole;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AsfPresencePongDataTest {

	private static final String host = "192.168.0.69";
	private static final int port = 8007;

	private static final int WIRE_LENGTH = 28;
	private static final byte[] byteSequence = {
			0x06, 0x00, 0x00, 0x06, 0x00, 0x00, 0x11, (byte) 0xbe, 
			0x40, 0x00, 0x00, 0x10, 0x00, 0x00, 0x11, (byte) 0xbe, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00 
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
		
		AsfPresencePongData data = new AsfPresencePongData();
		
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
        assertEquals(RmcpMessageClass.ASF, packet.getMessageClass());
        assertEquals(RmcpMessageRole.REQ, packet.getMessageRole());
        
        assertTrue(packet.getData() instanceof AsfPresencePongData);
	}
}
