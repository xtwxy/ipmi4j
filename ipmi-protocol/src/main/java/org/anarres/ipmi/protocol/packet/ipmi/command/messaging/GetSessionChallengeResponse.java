/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi.command.messaging;

import java.nio.ByteBuffer;

import org.anarres.ipmi.protocol.client.visitor.IpmiClientIpmiCommandHandler;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.ipmi.IpmiCommandName;
import org.anarres.ipmi.protocol.packet.ipmi.command.AbstractIpmiResponse;

/**
 * [IPMI2] Section 22.18, table 22-23, page 297.
 *
 * @author shevek
 */
public class GetSessionChallengeResponse extends AbstractIpmiResponse {

	private static final int CHALLENGE_STRING_LEN_BYTES = 16;

	private int temporarySessionId = 0xcafe;
	private byte[] challengeStringData = new byte[CHALLENGE_STRING_LEN_BYTES];
	
	public GetSessionChallengeResponse withTemporarySessionId(int temporarySessionId) {
		this.temporarySessionId = temporarySessionId;
		return this;
	}
	
	public int getTemporarySessionId() {
		return this.temporarySessionId;
	}
	
	public GetSessionChallengeResponse withChallengeStringData(String challengeString) {
    	if(challengeString != null) {
    		if(challengeString.length() > CHALLENGE_STRING_LEN_BYTES) {
    			throw new IllegalArgumentException("The length of challengeString "
    					+ "exceeds 16 bytes limit, offending value is: '" + challengeString + "'.");
    		} else {

    			for(int i = 0; i < this.challengeStringData.length; ++i) {
    				this.challengeStringData[i] = 0x0;
    			}

    			byte[] bytes = challengeString.getBytes();
    			for(int i = 0; i < bytes.length; ++i) {
    				this.challengeStringData[i] = bytes[i];
    			}
    		}
    	}
    	return this;
	}
	
	public String getChallengeStringData() {
		return new String(this.challengeStringData).trim();
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
        		+ CHALLENGE_STRING_LEN_BYTES;
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        if (toWireCompletionCode(buffer))
            return;
        toWireIntLE(buffer, temporarySessionId);
        buffer.put(challengeStringData);
    }

    @Override
    protected void fromWireData(ByteBuffer buffer) {
        if (fromWireCompletionCode(buffer))
            return;
        temporarySessionId = fromWireIntLE(buffer);
        buffer.get(challengeStringData);

    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        appendValue(buf, depth, "temporarySessionId", temporarySessionId);
        appendValue(buf, depth, "challengStringData", new String(challengeStringData));
    }
}
