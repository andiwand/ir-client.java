package at.stefl.irclient.java.frame;

import java.util.Arrays;

import at.stefl.irclient.java.protocol.RawProtocol;

public class RawFrame extends Frame {

	public double frequency;
	public double[] times;

	public RawFrame() {
		super(RawProtocol.INSTANCE);
	}

	@Override
	public double getFrequency() {
		return frequency;
	}

	@Override
	public String getDataString() {
		return Arrays.toString(times);
	}

	@Override
	public RawProtocol getProtocol() {
		return RawProtocol.INSTANCE;
	}

}