package at.stefl.irclient.java;

import java.net.InetAddress;

public class Station {

	private final String name;
	private final InetAddress address;
	private final int port;

	public Station(String name, InetAddress address, int port) {
		if (name == null)
			throw new IllegalArgumentException();
		if (address == null)
			throw new IllegalArgumentException();

		this.name = name;
		this.address = address;
		this.port = port;
	}

	@Override
	public String toString() {
		return name + "@" + address.getHostAddress() + ":" + port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

}