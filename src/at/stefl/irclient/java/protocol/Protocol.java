package at.stefl.irclient.java.protocol;

import at.stefl.irclient.java.MarginOfError;
import at.stefl.irclient.java.frame.Frame;
import at.stefl.irclient.java.frame.RawFrame;

public abstract class Protocol<T extends Frame> {

	private static final Protocol<?>[] PROTOCOLS = { NecProtocol.INSTANCE };

	public static Frame decodeRaw(RawFrame raw, MarginOfError moe) {
		for (Protocol<?> protocol : PROTOCOLS) {
			try {
				Frame frame = protocol.decode(raw, moe);
				if (frame != null)
					return frame;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return raw;
	}

	public static RawFrame encodeRaw(Frame frame) {
		for (Protocol<?> protocol : PROTOCOLS) {
			@SuppressWarnings("unchecked")
			Protocol<Frame> p = (Protocol<Frame>) protocol;
			if (protocol.getFrameClass().equals(frame.getClass()))
				return p.encode(frame);
		}

		throw new IllegalStateException();
	}

	private final String name;

	public Protocol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract double getFrequency();

	public abstract Class<T> getFrameClass();

	public abstract RawFrame encode(T frame);

	public abstract T decode(RawFrame raw, MarginOfError moe);

}