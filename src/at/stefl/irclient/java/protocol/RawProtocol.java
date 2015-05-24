package at.stefl.irclient.java.protocol;

import at.stefl.irclient.java.MarginOfError;
import at.stefl.irclient.java.frame.RawFrame;

public class RawProtocol extends Protocol<RawFrame> {

	public static final String NAME = "RAW";
	public static final RawProtocol INSTANCE = new RawProtocol();

	private RawProtocol() {
		super(NAME);
	}

	@Override
	public double getFrequency() {
		return Double.NaN;
	}

	@Override
	public Class<RawFrame> getFrameClass() {
		return RawFrame.class;
	}

	@Override
	public RawFrame encode(RawFrame frame) {
		return frame;
	}

	@Override
	public RawFrame decode(RawFrame raw, MarginOfError moe) {
		return raw;
	}

}