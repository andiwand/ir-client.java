package at.stefl.irmote.java;

import java.util.Set;

public class Test {

	public static void main(String[] args) throws Throwable {
		Set<Station> stations = Discovery.discover();
		System.out.println(stations);

		if (stations.isEmpty())
			return;
		Station station = stations.iterator().next();
		Remote remote = new Remote();
		remote.setStation(station);

		remote.configure("test-station", "asdf", "fdsa");

		// System.out.println("receive");
		// IrFrame frame = remote.receive();
		// System.out.println(frame);
		//
		// Thread.sleep(2000);
		//
		// for (int i = 0; i < 5; i++) {
		// System.out.println("send");
		// remote.send(frame);
		// Thread.sleep(2000);
		// }
	}

}