package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiString;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiRequest;

public class GetSessionChallengeRequest extends AbstractIpmiRequest {

	private IpmiSessionAuthenticationType authenticationType;
	// 17 bytes user name, 22.16 Get Session Challenge Command page 293
    private IpmiString userName = new IpmiString(16);

    public GetSessionChallengeRequest withAuthenticationType(IpmiSessionAuthenticationType authenticationType) {
    	this.authenticationType = authenticationType;
    	return this;
    }
    
    public IpmiSessionAuthenticationType getAuthenticationType() {
    	return authenticationType;
    }
    
    public GetSessionChallengeRequest withUserName(byte[] userName) {
    	this.userName.setContent(userName);
    	return this;
    }
   
    public byte[] getUserName() {
    	return this.userName.getContent();
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
        		+ this.userName.length();
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        buffer.put(authenticationType.getCode());
        buffer.put(this.userName.getContent());
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
    	authenticationType = Code.fromBuffer(IpmiSessionAuthenticationType.class, buffer);
    	buffer.get(this.userName.getContent());
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "authenticationType", authenticationType);
        appendValue(buf, depth, "userName", this.userName.getValue());
    }
}