package at.stefl.irclient.java.frame;

import at.stefl.irclient.java.protocol.Protocol;

public abstract class Frame {

	private final Protocol<? extends Frame> protocol;

	public Frame(Protocol<? extends Frame> protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return getProtocol().getName() + " " + getFrequency() + " Hz "
				+ getDataString();
	}

	public Protocol<? extends Frame> getProtocol() {
		return protocol;
	}

	public double getFrequency() {
		return getProtocol().getFrequency();
	}

	public abstract String getDataString();

}