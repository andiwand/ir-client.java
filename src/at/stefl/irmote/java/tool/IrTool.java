package at.stefl.irmote.java.tool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import at.stefl.irmote.java.Discovery;
import at.stefl.irmote.java.Remote;
import at.stefl.irmote.java.Station;
import at.stefl.irmote.java.frame.IrFrame;

public class IrTool extends JFrame {

	private class DiscoveryService extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Set<Station> stations = Discovery.discover();
					discoveryModel.update(stations);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class DiscoveryModel extends AbstractTableModel {
		private static final long serialVersionUID = 8649654422316024669L;

		private final String[] NAMES = new String[] { "Name", "Address", "Port" };
		private final Class<?>[] CLASSES = { String.class, String.class,
				Integer.class };

		private List<Station> stations = new ArrayList<>();

		@Override
		public int getColumnCount() {
			return NAMES.length;
		}

		@Override
		public synchronized int getRowCount() {
			return stations.size();
		}

		@Override
		public String getColumnName(int column) {
			return NAMES[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return CLASSES[columnIndex];
		}

		public synchronized Station getStation(int rowIndex) {
			return stations.get(rowIndex);
		}

		@Override
		public synchronized Object getValueAt(int rowIndex, int columnIndex) {
			Station station = stations.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return station.getName();
			case 1:
				return station.getAddress().getHostAddress();
			case 2:
				return station.getPort();
			default:
				throw new IllegalStateException();
			}
		}

		public synchronized void update(Set<Station> stations) {
			List<Station> newStations = new ArrayList<>();

			for (int i = 0; i < this.stations.size(); i++) {
				Station station = this.stations.get(i);
				if (stations.contains(station)) {
					newStations.add(station);
				} else {
					fireTableRowsDeleted(newStations.size(), newStations.size());
				}
			}

			this.stations = newStations;

			for (Station station : stations) {
				if (newStations.contains(station))
					continue;
				newStations.add(station);
				fireTableRowsInserted(newStations.size() - 1,
						newStations.size() - 1);
			}
		}
	}

	private class FrameModel extends AbstractTableModel {
		private static final long serialVersionUID = -7817427624659087961L;

		private class Entry {
			String name;
			IrFrame frame;
		}

		private final String[] NAMES = new String[] { "Name", "Protocol",
				"Freqency", "Data" };
		private final Class<?>[] CLASSES = { String.class, String.class,
				Double.class, String.class };

		private List<Entry> entries = new ArrayList<>();

		@Override
		public String getColumnName(int column) {
			return NAMES[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return CLASSES[columnIndex];
		}

		@Override
		public int getColumnCount() {
			return NAMES.length;
		}

		@Override
		public synchronized int getRowCount() {
			return entries.size();
		}

		public synchronized IrFrame getFrame(int rowIndex) {
			return entries.get(rowIndex).frame;
		}

		@Override
		public synchronized Object getValueAt(int rowIndex, int columnIndex) {
			Entry entry = entries.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return entry.name;
			case 1:
				return entry.frame.getProtocol().getName();
			case 2:
				return entry.frame.getFrequency();
			case 3:
				return entry.frame.getDataString();
			default:
				throw new IllegalStateException();
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Entry entry = entries.get(rowIndex);
			entry.name = (String) aValue;
		}

		public synchronized void addEntry(String name, IrFrame frame) {
			Entry e = new Entry();
			e.name = name;
			e.frame = frame;
			entries.add(e);
			fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
		}
	}

	private class ReceiveAction implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent e) {
			setLock(true);
			executor.execute(this);
		}

		@Override
		public void run() {
			try {
				setStation();
				IrFrame frame = remote.receive();
				frameModel.addEntry("", frame);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				setLock(false);
			}
		}
	}

	private class SendAction implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent e) {
			setLock(true);
			executor.execute(this);
		}

		@Override
		public void run() {
			try {
				setStation();
				IrFrame frame = getFrame();
				if (frame == null)
					return;
				remote.send(frame);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				setLock(false);
			}
		}
	}

	private class SetAction implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent e) {
			setLock(true);
			executor.execute(this);
		}

		@Override
		public void run() {
			try {
				setStation();
				remote.configure(name.getText(), ssid.getText(),
						password.getText());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				setLock(false);
			}
		}
	}

	private static final long serialVersionUID = -3137023196421947713L;

	private static final int GAP = 5;

	private Remote remote;
	private Executor executor;

	private JTable discoveryTable;
	private DiscoveryModel discoveryModel;
	private DiscoveryService discoveryService;
	private JTextField address;
	private JTable frameTable;
	private FrameModel frameModel;
	private JButton receive;
	private JButton send;
	private JTextField name;
	private JTextField ssid;
	private JTextField password;
	private JButton set;

	public IrTool() {
		super("IR Tool");

		remote = new Remote();
		executor = Executors.newSingleThreadExecutor();

		createInterface();

		receive.addActionListener(new ReceiveAction());
		send.addActionListener(new SendAction());

		discoveryTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = discoveryTable.getSelectedRow();
						if (selectedRow < 0) {
							// address.setText("");
							return;
						}
						selectedRow = discoveryTable
								.convertRowIndexToModel(selectedRow);
						Station station = discoveryModel
								.getStation(selectedRow);
						address.setText(station.getAddress().getHostAddress()
								+ ":" + station.getPort());
					}
				});

		set.addActionListener(new SetAction());

		discoveryService = new DiscoveryService();
		discoveryService.start();
	}

	private void createInterface() {
		setLayout(new BorderLayout(GAP, GAP));
		getRootPane().setBorder(
				BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));

		JPanel top = new JPanel();
		JPanel topBottom = new JPanel();
		JPanel center = new JPanel();
		JPanel centerBottom = new JPanel();
		JPanel bottom = new JPanel();

		top.setLayout(new BorderLayout(GAP, GAP));
		topBottom.setLayout(new BorderLayout(GAP, GAP));
		center.setLayout(new BorderLayout(GAP, GAP));
		centerBottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
		GroupLayout bottomLayout = new GroupLayout(bottom);
		bottomLayout.setAutoCreateGaps(true);
		bottom.setLayout(bottomLayout);

		top.setBorder(new CompoundBorder(BorderFactory
				.createTitledBorder("Station"), BorderFactory
				.createEmptyBorder(GAP, GAP, GAP, GAP)));
		center.setBorder(new CompoundBorder(BorderFactory
				.createTitledBorder("Send / Receive"), BorderFactory
				.createEmptyBorder(GAP, GAP, GAP, GAP)));
		bottom.setBorder(new CompoundBorder(BorderFactory
				.createTitledBorder("Network"), BorderFactory
				.createEmptyBorder(GAP, GAP, GAP, GAP)));

		discoveryModel = new DiscoveryModel();
		discoveryTable = new JTable(discoveryModel);
		discoveryTable.setAutoCreateRowSorter(true);
		JScrollPane discoveryScrollPane = new JScrollPane(discoveryTable);
		discoveryScrollPane.setPreferredSize(new Dimension(200, 100));
		address = new JTextField();
		JLabel addressLabel = new JLabel("Address:");

		frameModel = new FrameModel();
		frameTable = new JTable(frameModel);
		frameTable.setAutoCreateRowSorter(true);
		JScrollPane frameScrollPane = new JScrollPane(frameTable);
		frameScrollPane.setPreferredSize(new Dimension(200, 100));
		receive = new JButton("Receive");
		send = new JButton("Send");

		name = new JTextField();
		ssid = new JTextField();
		password = new JTextField();
		set = new JButton("Set");
		JLabel nameLabel = new JLabel("Name:");
		JLabel ssidLabel = new JLabel("SSID:");
		JLabel passwordLabel = new JLabel("Password:");

		top.add(discoveryScrollPane, BorderLayout.CENTER);
		top.add(topBottom, BorderLayout.SOUTH);
		topBottom.add(addressLabel, BorderLayout.WEST);
		topBottom.add(address, BorderLayout.CENTER);

		center.add(frameScrollPane, BorderLayout.CENTER);
		center.add(centerBottom, BorderLayout.SOUTH);
		centerBottom.add(receive);
		centerBottom.add(send);

		bottomLayout.setHorizontalGroup(bottomLayout
				.createSequentialGroup()
				.addGroup(
						bottomLayout.createParallelGroup()
								.addComponent(nameLabel)
								.addComponent(ssidLabel)
								.addComponent(passwordLabel))
				.addGroup(
						bottomLayout.createParallelGroup().addComponent(name)
								.addComponent(ssid).addComponent(password)
								.addComponent(set, Alignment.TRAILING)));
		bottomLayout.setVerticalGroup(bottomLayout
				.createSequentialGroup()
				.addGroup(
						bottomLayout.createParallelGroup()
								.addComponent(nameLabel).addComponent(name))
				.addGroup(
						bottomLayout.createParallelGroup()
								.addComponent(ssidLabel).addComponent(ssid))
				.addGroup(
						bottomLayout.createParallelGroup()
								.addComponent(passwordLabel)
								.addComponent(password)).addComponent(set));

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		pack();
	}

	private void setLock(boolean lock) {
		receive.setEnabled(!lock);
		send.setEnabled(!lock);
	}

	private void setStation() {
		String address = this.address.getText();
		int index = address.lastIndexOf(':');
		String host = address.substring(0, index);
		int port = Integer.parseInt(address.substring(index + 1));
		remote.setStation(host, port);
	}

	private IrFrame getFrame() {
		int selectedRow = frameTable.getSelectedRow();
		if (selectedRow < 0)
			return null;
		selectedRow = frameTable.convertRowIndexToModel(selectedRow);
		return frameModel.getFrame(selectedRow);
	}

	public static void main(String[] args) {
		IrTool tool = new IrTool();
		tool.setSize(500, 600);
		tool.setLocationRelativeTo(null);
		tool.setDefaultCloseOperation(EXIT_ON_CLOSE);
		tool.setVisible(true);
	}

}
