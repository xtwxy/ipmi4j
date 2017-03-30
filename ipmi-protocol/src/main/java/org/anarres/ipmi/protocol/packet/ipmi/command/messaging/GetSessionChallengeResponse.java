/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiString;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiResponse;

/**
 * [IPMI2] Section 22.18, table 22-23, page 297.
 *
 * @author shevek
 */
public class GetSessionChallengeResponse extends AbstractIpmiResponse {

	private int temporarySessionId = 0xcafe;
	private IpmiString challengeStringData = new IpmiString(16);
	
	public GetSessionChallengeResponse withTemporarySessionId(int temporarySessionId) {
		this.temporarySessionId = temporarySessionId;
		return this;
	}
	
	public int getTemporarySessionId() {
		return this.temporarySessionId;
	}
	
	public GetSessionChallengeResponse withChallengeStringData(byte[] challengeString) {
		this.challengeStringData.setContent(challengeString);
    	return this;
	}
	
	public byte[] getChallengeStringData() {
		return this.challengeStringData.getContent();
	}
	
    @Override
    public IpmiCommandName getCommandName() {
        return IpmiCommandName.GetSessionChallenge;
    }

    @Override
    public void apply(IpmiClientIpmiCommandHandler handler, IpmiHandlerContext context) {
        handler.handleGetSessionChallengeResponse(context, this);
    }

    @Override
    protected int getResponseDataWireLength() {
        return 1 // completion code
        		+ 4 // temporary session id
        		+ this.challengeStringData.length();
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        if (toWireCompletionCode(buffer))
            return;
        toWireIntLE(buffer, temporarySessionId);
        buffer.put(challengeStringData.getContent());
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
        if (fromWireCompletionCode(buffer))
            return;
        temporarySessionId = fromWireIntLE(buffer);
        buffer.get(challengeStringData.getContent());

    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "temporarySessionId", temporarySessionId);
        appendValue(buf, depth, "challengStringData", challengeStringData.getValue());
    }
}
