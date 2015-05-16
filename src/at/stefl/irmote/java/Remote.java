package at.stefl.irmote.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import at.stefl.irmote.java.frame.IrFrame;
import at.stefl.irmote.java.frame.RawFrame;
import at.stefl.irmote.java.protocol.IrProtocol;

public class Remote {

	public static final int DEFAULT_TIMEOUT = 1500;

	private static final MarginOfError MOE = new MarginOfError(
			Double.POSITIVE_INFINITY, 0.2);

	private String host;
	private int port;
	private int timeout = DEFAULT_TIMEOUT;

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	public Remote() {
	}

	public void setStation(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void setStation(Station station) {
		setStation(station.getAddress().getHostAddress(), station.getPort());
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	private void start() throws IOException {
		socket = new Socket(host, port);
		socket.setSoTimeout(timeout);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}

	private void stop() throws IOException {
		socket.close();
	}

	private void sendHead(int type, int length) throws IOException {
		out.writeByte(type);
		out.writeShort(length);
	}

	private void sendHead(PacketType type, int length) throws IOException {
		sendHead(type.getType(), length);
	}

	private int receiveHead(PacketType expectedType) throws IOException {
		byte type = in.readByte();
		short length = in.readShort();

		if (expectedType.getType() != type)
			throw new IllegalStateException();
		return length;
	}

	public void sendRaw(RawFrame raw) throws IOException {
		try {
			start();

			sendHead(PacketType.SEND_REQUEST, 4 + 2 + 2 * raw.times.length);
			out.writeInt((int) raw.frequency);
			out.writeShort(raw.times.length);

			for (int i = 0; i < raw.times.length; i++) {
				out.writeShort((int) (raw.times[i] * 1e6));
			}

			out.flush();

			receiveHead(PacketType.SEND_RESPONSE);
		} catch (Exception e) {
			throw e;
		} finally {
			stop();
		}
	}

	public void send(IrFrame frame) throws IOException {
		RawFrame raw = IrProtocol.encodeRaw(frame);
		sendRaw(raw);
	}

	public RawFrame receiveRaw() throws IOException {
		try {
			start();

			sendHead(PacketType.RECEIVE_REQUEST, 0);
			out.flush();

			RawFrame result = new RawFrame();
			receiveHead(PacketType.RECEIVE_RESPONSE);
			result.frequency = in.readInt();
			double[] times = new double[in.readShort()];
			result.times = times;

			for (int i = 0; i < times.length; i++) {
				times[i] = in.readShort() * 1e-6;
			}

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			stop();
		}
	}

	public IrFrame receive() throws IOException {
		RawFrame raw = receiveRaw();
		return IrProtocol.decodeRaw(raw, MOE);
	}

	public void configure(String name, String ssid, String password)
			throws IOException {
		start();

		byte[] nameBytes = name.getBytes("us-ascii");
		byte[] ssidBytes = ssid.getBytes("us-ascii");
		byte[] passwordBytes = password.getBytes("us-ascii");

		int length = 1 + nameBytes.length + 1 + ssidBytes.length + 1
				+ passwordBytes.length;
		sendHead(PacketType.NETWORK_REQUEST, length);
		out.writeByte(nameBytes.length);
		out.write(nameBytes);
		out.writeByte(ssidBytes.length);
		out.write(ssidBytes);
		out.writeByte(passwordBytes.length);
		out.write(passwordBytes);
		out.flush();

		receiveHead(PacketType.NETWORK_RESPONSE);

		stop();
	}

}