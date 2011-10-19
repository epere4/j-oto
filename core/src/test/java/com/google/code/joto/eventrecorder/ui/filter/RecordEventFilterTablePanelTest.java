package com.google.code.joto.eventrecorder.ui.filter;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.junit.Test;

/**
 * JUnit test for RecordEventFilterTablePanel
 */
public class RecordEventFilterTablePanelTest {

	@Test
	public void testDoNothing() {
	}
	
//	@Test
	public void openCloseEditorPane() throws Exception {
		RecordEventFilterEditor editor = new RecordEventFilterEditor();
		showInFrame(editor.getJComponent());
	}
	
//	@Test
	public void openCloseTablePane() throws Exception {
		RecordEventFilterTableModel tm = new RecordEventFilterTableModel();
		RecordEventFilter f1 = new RecordEventFilter();
		tm.addRow(f1);
		RecordEventFilter f2 = new RecordEventFilter();
		tm.addRow(f2);
		
		RecordEventFilterTablePanel tableView = new RecordEventFilterTablePanel(tm);
		
		showInFrame(tableView.getJComponent());
	}

	private void showInFrame(JComponent comp) throws InterruptedException {
		JFrame frame = new JFrame();
		frame.getContentPane().add(comp);
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(10000);
		frame.setVisible(false);
		frame.dispose();
	}
}
