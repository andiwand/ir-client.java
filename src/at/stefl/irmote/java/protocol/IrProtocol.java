package at.stefl.irmote.java.protocol;

import at.stefl.irmote.java.MarginOfError;
import at.stefl.irmote.java.frame.IrFrame;
import at.stefl.irmote.java.frame.RawFrame;

public abstract class IrProtocol<T extends IrFrame> {

	private static final IrProtocol<?>[] PROTOCOLS = { NecProtocol.INSTANCE };

	public static IrFrame decodeRaw(RawFrame raw, MarginOfError moe) {
		for (IrProtocol<?> protocol : PROTOCOLS) {
			try {
				IrFrame frame = protocol.decode(raw, moe);
				if (frame != null)
					return frame;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return raw;
	}

	public static RawFrame encodeRaw(IrFrame frame) {
		for (IrProtocol<?> protocol : PROTOCOLS) {
			@SuppressWarnings("unchecked")
			IrProtocol<IrFrame> p = (IrProtocol<IrFrame>) protocol;
			if (protocol.getFrameClass().equals(frame.getClass()))
				return p.encode(frame);
		}

		throw new IllegalStateException();
	}

	private final String name;

	public IrProtocol(String name) {
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