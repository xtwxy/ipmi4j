package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiRequest;

public class GetSessionChallengeRequest extends AbstractIpmiRequest {

	private static final int USER_NAME_LEN_BYTES = 16;

	private IpmiSessionAuthenticationType authenticationType;
	// 17 bytes user name, 22.16 Get Session Challenge Command page 293
    private byte[] userName = new byte[USER_NAME_LEN_BYTES];

    public GetSessionChallengeRequest withAuthenticationType(IpmiSessionAuthenticationType authenticationType) {
    	this.authenticationType = authenticationType;
    	return this;
    }
    
    public IpmiSessionAuthenticationType getAuthenticationType() {
    	return authenticationType;
    }
    
    public GetSessionChallengeRequest withUserName(String userName) {
    	if(userName != null) {
    		if(userName.length() > USER_NAME_LEN_BYTES) {
    			throw new IllegalArgumentException("The length of userName "
    					+ "exceeds 16 bytes limit, offending value is: '" + userName + "'.");
    		} else {

    			for(int i = 0; i < this.userName.length; ++i) {
    				this.userName[i] = 0x0;
    			}

    			byte[] bytes = userName.getBytes();
    			for(int i = 0; i < bytes.length; ++i) {
    				this.userName[i] = bytes[i];
    			}
    		}
    	}
    	return this;
    }
   
    public String getUserName() {
    	return new String(this.userName).trim();
    }
    @Override
    public IpmiCommandName getCommandName() {
        return IpmiCommandName.GetSessionChallenge;
    }

    @Override
    public void apply(IpmiClientIpmiCommandHandler handler, IpmiHandlerContext context) {
        handler.handleGetSessionChallengeRequest(context, this);
    }

    @Override
    protected int getDataWireLength() {
        return 1 // Authentication Type
        		+ USER_NAME_LEN_BYTES;
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        buffer.put(authenticationType.getCode());
        buffer.put(userName);
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
    	authenticationType = Code.fromBuffer(IpmiSessionAuthenticationType.class, buffer);
    	buffer.get(userName);
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "authenticationType", authenticationType);
        appendValue(buf, depth, "userName", new String(userName));
    }
}