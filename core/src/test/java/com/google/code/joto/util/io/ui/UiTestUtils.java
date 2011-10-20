package com.google.code.joto.util.io.ui;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class UiTestUtils {

	public static void showInFrame(JComponent comp) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(comp);
		frame.pack();
		frame.setVisible(true);
		try {
			Thread.sleep(10000);
		} catch(InterruptedException ex) {
			// ignore, do nothing!
		}
		frame.setVisible(false);
		frame.dispose();
	}

}
