package edu.cornell.rocketry.gui;

public class Main {

	/**
	 * Launches the CURocketry Ground Station GUI
	 * @param args
	 */
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GSGui().setVisible(true);
			}
		});
	}
}
