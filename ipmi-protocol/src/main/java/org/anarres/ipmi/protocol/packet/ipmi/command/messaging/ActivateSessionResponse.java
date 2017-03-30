package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiSessionAuthenticationType;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiResponse;
import org.anarres.ipmi.protocol.packet.ipmi.payload.RequestedMaximumPrivilegeLevel;

public class ActivateSessionResponse extends AbstractIpmiResponse {

	private IpmiSessionAuthenticationType authenticationType;
	private int sessionId;
	private int initialInboundSequence;
	private RequestedMaximumPrivilegeLevel maximumPrivilegeLevelAllowed;
	
    public IpmiSessionAuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public ActivateSessionResponse withAuthenticationType(IpmiSessionAuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
		return this;
	}

	public int getSessionId() {
		return sessionId;
	}

	public ActivateSessionResponse withSessionId(int sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public int getInitialInboundSequence() {
		return initialInboundSequence;
	}

	public ActivateSessionResponse withInitialInboundSequence(int initialInboundSequence) {
		this.initialInboundSequence = initialInboundSequence;
		return this;
	}

	public RequestedMaximumPrivilegeLevel getMaximumPrivilegeLevelAllowed() {
		return maximumPrivilegeLevelAllowed;
	}

	public ActivateSessionResponse withMaximumPrivilegeLevelAllowed(RequestedMaximumPrivilegeLevel maximumPrivilegeLevelAllowed) {
		this.maximumPrivilegeLevelAllowed = maximumPrivilegeLevelAllowed;
		return this;
	}

	@Override
    public IpmiCommandName getCommandName() {
        return IpmiCommandName.ActivateSession;
    }

    @Override
    public void apply(IpmiClientIpmiCommandHandler handler, IpmiHandlerContext context) {
        handler.handleActivateSessionResponse(context, this);
    }

    @Override
    protected int getResponseDataWireLength() {
        return 1 // completion code
        		+ 1 // authentication type
        		+ 4 // session id
        		+ 4 // initial inbound sequence
        		+ 1 // maximum privilege level allowed
        		;
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        if (toWireCompletionCode(buffer))
            return;
        buffer.put(authenticationType.getCode());
        toWireIntLE(buffer, sessionId);
        toWireIntLE(buffer, initialInboundSequence);
        buffer.put(maximumPrivilegeLevelAllowed.getCode());
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
        if (fromWireCompletionCode(buffer))
            return;
        authenticationType = Code.fromBuffer(IpmiSessionAuthenticationType.class, buffer);
        sessionId = fromWireIntLE(buffer);
        initialInboundSequence = fromWireIntLE(buffer);
        maximumPrivilegeLevelAllowed = Code.fromBuffer(RequestedMaximumPrivilegeLevel.class, buffer);

    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "authenticationType", authenticationType);
        appendValue(buf, depth, "sessionId", sessionId);
        appendValue(buf, depth, "initialInboundSequence", initialInboundSequence);
        appendValue(buf, depth, "maximumPrivilegeLevelAllowed", maximumPrivilegeLevelAllowed);
    }
}
