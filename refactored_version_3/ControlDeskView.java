/* ControlDeskView.java
 *
 *  Version:
 *			$Id$
 * 
 *  Revisions:
 * 		$Log$
 * 
 */

/**
 * Class for representation of the control desk
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;

public class ControlDeskView implements ActionListener, Observer {

	private JButton addParty, finished;
	private JFrame win;
	private JList partyList;
	
	/** The maximum  number of members in a party */
	private int maxMembers;
	
	private ControlDesk controlDesk;

	/**
	 * Displays a GUI representation of the ControlDesk
	 *
	 **/

	 ControlDeskView(ControlDesk controlDesk, int maxMembers) {

		this.controlDesk = controlDesk;
		this.maxMembers = maxMembers;
		int numLanes = controlDesk.getNumLanes();

		win = new JFrame("Control Desk");
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new BorderLayout());

		// Controls Panel
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new GridLayout(2, 1));
		controlsPanel.setBorder(new TitledBorder("Controls"));
		 String s="Add Party";
		 addParty = HelperView.makNewTab(s,controlsPanel);
		 addParty.addActionListener(this);
		 s="Finished";
		 finished =  HelperView.makNewTab(s,controlsPanel);
		 finished.addActionListener(this);

		// Lane Status Panel
		JPanel laneStatusPanel = new JPanel();
		laneStatusPanel.setLayout(new GridLayout(numLanes, 1));
		laneStatusPanel.setBorder(new TitledBorder("Lane Status"));

		HashSet lanes=controlDesk.getLanes();
		Iterator it = lanes.iterator();
		int laneCount=0;
		while (it.hasNext()) {
			Lane curLane = (Lane) it.next();
			LaneStatusView laneStat = new LaneStatusView(curLane,(laneCount+1));
			curLane.subscribe(laneStat);
			((Pinsetter)curLane.getPinsetter()).subscribe(laneStat);
			JPanel lanePanel = laneStat.showLane();
			lanePanel.setBorder(new TitledBorder("Lane" + ++laneCount ));
			laneStatusPanel.add(lanePanel);
		}

		// Party Queue Panel
		JPanel partyPanel = new JPanel();
		partyPanel.setLayout(new FlowLayout());
		partyPanel.setBorder(new TitledBorder("Party Queue"));

		Vector empty = new Vector();
		empty.add("(Empty)");

		partyList = new JList(empty);
		partyList.setFixedCellWidth(120);
		partyList.setVisibleRowCount(10);
		JScrollPane partyPane = new JScrollPane(partyList);
		partyPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		partyPanel.add(partyPane);
		//		partyPanel.add(partyList);

		// Clean up main panel
		colPanel.add(controlsPanel, "East");
		colPanel.add(laneStatusPanel, "Center");
		colPanel.add(partyPanel, "West");

		win.getContentPane().add("Center", colPanel);

		win.pack();

		/* Close program when this window closes */
		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Center Window on Screen
		 HelperView.setWindowAlignment(win);

	}

	/**
	 * Handler for actionEvents
	 *
	 * @param e	the ActionEvent that triggered the handler
	 *
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addParty)) {
			AddPartyView addPartyWin = new AddPartyView(this, maxMembers);
		}
		if (e.getSource().equals(finished)) {
			win.hide();
			System.exit(0);
		}
	}

	/**
	 * Receive a new party from andPartyView.
	 *
	 * @param addPartyView	the AddPartyView that is providing a new party
	 *
	 */

	public void updateAddParty(AddPartyView addPartyView) {
		controlDesk.addPartyQueue(addPartyView.getParty());
	}

	/**
	 * Receive a broadcast from a ControlDesk
	 *
	 * @param ce	the ControlDeskEvent that triggered the handler
	 *
	 */

//	public void receiveControlDeskEvent(ControlDeskEvent ce) {
//		partyList.setListData(((Vector) ce.getPartyQueue()));
//	}
	
	@Override
	public void update(Observable o, Object arg) {
		try {
			ControlDesk cd = (ControlDesk)o;
			partyList.setListData(((Vector) cd.getPartyQueue()));
		}
		catch(Exception e){
			System.out.println(e);
			return;
		}
		
	}
}
