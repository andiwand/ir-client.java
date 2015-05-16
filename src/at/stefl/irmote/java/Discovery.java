package at.stefl.irmote.java;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class Discovery extends Thread {

	public static Set<Station> discover() throws IOException {
		return discover(Constant.BEACON_PORT, Constant.BEACON_SIZE_MAX,
				Constant.DEFAULT_DISCOVERY_TIMEOUT);
	}

	public static Set<Station> discover(int port, int bufferSize, int timeout)
			throws IOException {
		Set<Station> stations = new HashSet<Station>();
		Discovery discovery = new Discovery(port, bufferSize, stations);
		discovery.start();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		discovery.interrupt();
		try {
			discovery.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return stations;
	}

	private final int port;
	private final int bufferSize;
	private final Set<Station> stations;
	DatagramSocket socket;

	public Discovery(int port, int bufferSize, Set<Station> stations) {
		super("discovery");

		this.port = port;
		this.bufferSize = bufferSize;
		this.stations = stations;
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[bufferSize];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while (true) {
				socket.receive(packet);
				DataInputStream in = new DataInputStream(
						new ByteArrayInputStream(buffer));
				int remotePort = in.readShort();
				int nameLength = in.readByte();
				String name = new String(buffer, 3, nameLength, "us-ascii");
				in.close();
				Station station = new Station(name, packet.getAddress(),
						remotePort);
				stations.add(station);
			}
		} catch (SocketException e) {
			if (isInterrupted())
				return;
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		socket.close();
	}

	public void close() {
		if (socket != null)
			socket.close();
	}
}
