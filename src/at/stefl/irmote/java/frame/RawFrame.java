package at.stefl.irmote.java.frame;

import java.util.Arrays;

import at.stefl.irmote.java.protocol.RawProtocol;

public class RawFrame extends IrFrame {

	public double frequency;
	public double[] times;

	public RawFrame() {
		super(RawProtocol.INSTANCE);
	}

	@Override
	public String toString() {
		return super.toString() + " " + frequency + " Hz";
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