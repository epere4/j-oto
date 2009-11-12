package com.google.code.joto.eventrecorder.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.Document;

/**
 *
 */
public class ScrolledTextPane {

	private JPanel mainPanel;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	
	private JToolBar southToolBar;
	
	//-------------------------------------------------------------------------

	public ScrolledTextPane() {
		mainPanel = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		scrollPane = new JScrollPane(textPane, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(BorderLayout.CENTER, scrollPane);
		
		southToolBar = new JToolBar();
		mainPanel.add(BorderLayout.SOUTH, southToolBar);
		
		JButton clearButton = new JButton("clear");
		JButton copyToClipboardButton = new JButton("copy");
		southToolBar.add(clearButton);
		southToolBar.add(copyToClipboardButton);
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearText();
			}
		});
		copyToClipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyToClipboard();
			}
		});
	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return mainPanel;
	}
	
	public void clearText() {
		try {
			Document doc = textPane.getDocument();
			doc.remove(0, doc.getLength());
		} catch(Exception ex) {
			System.err.println("failed to clear text");
		}
	}

	public String getText() {
		return textPane.getText();
	}

	public void setText(String p) {
		textPane.setText(p);
	}

	public void scrollToStart() {
		scrollPane.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
	}
	
	public void copyToClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection contents = new StringSelection(getText());
		clipboard.setContents(contents, contents);
	}
	
}
