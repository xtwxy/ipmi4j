package org.anarres.ipmi.protocol.packet.ipmi;

public class IpmiString {
	private final byte[] content;

	public IpmiString(final int length) {
		this.content = new byte[length];
	}
	
	public void setContent(byte[] c) {
		if(c != null && c.length <= this.content.length) {
			clear();
			for(int i = 0; (i < c.length) && (i < this.content.length); ++i) {
				this.content[i] = c[i];
			}
		}
	}
	
	public byte[] getContent() {
		return this.content;
	}
	
	public void setValue(String str) {
		if (str != null) {
			if (str.length() > content.length) {
				throw new IllegalArgumentException(
						String.format("The length of String " + "exceeds %d bytes limit, offending value is: '%s'.",
								content.length, str));
			} else {

				clear();

				byte[] bytes = str.getBytes();
				for (int i = 0; (i < bytes.length) && (i < this.content.length); ++i) {
					this.content[i] = bytes[i];
				}
			}
		}
	}

	private void clear() {
		for (int i = 0; i < this.content.length; ++i) {
			this.content[i] = 0x0;
		}
	}

	public String getValue() {
		return new String(this.content).trim();
	}
	
	public int length() {
		return this.content.length;
	}
}
