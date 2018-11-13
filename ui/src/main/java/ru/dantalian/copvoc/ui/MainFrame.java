package ru.dantalian.copvoc.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {

	public MainFrame() {
		super();
		setTitle("Copious Vocabulary");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JPanel mainPanel = new JPanel();
		final MigLayout mainLayout = new MigLayout("fill", // Layout Constraints
				"[fill]", // Column constraints
				"[]8[]"); // Row constraints
		mainPanel.setLayout(mainLayout);

		mainPanel.add(new JButton("test1"));
		mainPanel.add(new JButton("test2"));
		mainPanel.add(new JButton("test3"));

		getContentPane().add(mainPanel);
		pack();
	}

}
