package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiString;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiRequest;
import org.anarres.ipmi.protocol.packet.ipmi.payload.RequestedMaximumPrivilegeLevel;

public class ActivateSessionRequest extends AbstractIpmiRequest {

	private IpmiSessionAuthenticationType authenticationType;
    public RequestedMaximumPrivilegeLevel requestedMaximumPrivilegeLevel;
    private IpmiString challengeString = new IpmiString(16);
    private int initialOutboundSequence = 0;
    
    public ActivateSessionRequest withAuthenticationType(IpmiSessionAuthenticationType authenticationType) {
    	this.authenticationType = authenticationType;
    	return this;
    }
    
    public IpmiSessionAuthenticationType getAuthenticationType() {
    	return authenticationType;
    }
    
    public ActivateSessionRequest withUserName(byte[] userName) {
    	this.challengeString.setContent(userName);
    	return this;
    }
   
    public byte[] getUserName() {
    	return this.challengeString.getContent();
    }
    
    public ActivateSessionRequest withInitialOutboundSequence(int sequence) {
    	this.initialOutboundSequence = sequence;
    	return this;
    }
    
    public int getInitialOutboundSequence() {
    	return this.initialOutboundSequence;
    }
    
    @Override
    public IpmiCommandName getCommandName() {
        return IpmiCommandName.ActivateSession;
    }

    @Override
    public void apply(IpmiClientIpmiCommandHandler handler, IpmiHandlerContext context) {
        handler.handleActivateSessionRequest(context, this);
    }

    @Override
    protected int getDataWireLength() {
        return 1 // Authentication Type
        		+ 1 // requested maximum privilege level
        		+ this.challengeString.length()
        		+ 4 // initial outbound sequence
        		;
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        buffer.put(authenticationType.getCode());
        buffer.put(requestedMaximumPrivilegeLevel.getCode());
        buffer.put(challengeString.getContent());
        toWireIntLE(buffer, initialOutboundSequence);
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
    	authenticationType = Code.fromBuffer(IpmiSessionAuthenticationType.class, buffer);
    	requestedMaximumPrivilegeLevel = Code.fromBuffer(RequestedMaximumPrivilegeLevel.class, buffer);
    	buffer.get(challengeString.getContent());
    	initialOutboundSequence = fromWireIntLE(buffer);
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "authenticationType", authenticationType);
        appendValue(buf, depth, "requestedMaximumPrivilegeLevel", requestedMaximumPrivilegeLevel);
        appendValue(buf, depth, "challengString", challengeString.getValue());
        appendValue(buf, depth, "initialOutboundSequence", initialOutboundSequence);
    }
}