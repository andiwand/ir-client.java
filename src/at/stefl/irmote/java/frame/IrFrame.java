package at.stefl.irmote.java.frame;

import at.stefl.irmote.java.protocol.IrProtocol;

public abstract class IrFrame {

	private final IrProtocol<? extends IrFrame> protocol;

	public IrFrame(IrProtocol<? extends IrFrame> protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return getProtocol().getName() + " " + getFrequency() + " "
				+ getDataString();
	}

	public IrProtocol<? extends IrFrame> getProtocol() {
		return protocol;
	}

	public double getFrequency() {
		return getProtocol().getFrequency();
	}

	public abstract String getDataString();

}