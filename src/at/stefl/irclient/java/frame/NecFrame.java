package at.stefl.irclient.java.frame;

import at.stefl.irclient.java.protocol.NecProtocol;

public class NecFrame extends Frame {

	private int data;

	public NecFrame(int data) {
		super(NecProtocol.INSTANCE);

		this.data = data;
	}

	@Override
	public String getDataString() {
		return String.format("%08X", data);
	}

	public int getData() {
		return data;
	}

}