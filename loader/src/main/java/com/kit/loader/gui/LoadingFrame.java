package com.kit.loader.gui;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.painter.BusyPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

public class LoadingFrame extends JFrame {

	private final Color DARK = new Color(35, 43, 51);
	private final Color LIGHT = new Color(15, 118, 94);
	private JLabel loadingLabel;

	public LoadingFrame() {
		setTitle("07Kit");
		setSize(320, 130);
		setResizable(false);
		getContentPane().setBackground(DARK);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		JXBusyLabel loadingSpinner = new JXBusyLabel(new Dimension(38, 38));
		BusyPainter painter = new BusyPainter(
				new Rectangle2D.Float(0, 0, 8.0f, 8.0f),
				new Rectangle2D.Float(5.5f, 5.5f, 27.0f, 27.0f));
		painter.setTrailLength(4);
		painter.setPoints(8);
		painter.setFrame(-1);
		painter.setBaseColor(LIGHT);
		painter.setHighlightColor(Color.WHITE);
		loadingSpinner.setBusyPainter(painter);
		loadingSpinner.setVisible(true);
		loadingSpinner.setBusy(true);

		add(loadingSpinner);
		GridBagConstraints c = new GridBagConstraints();

		loadingLabel = new JLabel("Loading..");
		loadingLabel.setForeground(Color.WHITE);
		loadingLabel.setFont(loadingLabel.getFont().deriveFont(16f).deriveFont(Font.BOLD));
		add(loadingLabel);

		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(6, 6, 6, 6);
		add(loadingLabel, c);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int centerX = (toolkit.getScreenSize().width / 2) - (getWidth() / 2);
		int centerY = (toolkit.getScreenSize().height / 2) - (getHeight() / 2);
		setLocation(centerX, centerY);
		setVisible(true);
	}

	public void setLoadingText(String text) {
		loadingLabel.setText(text);
	}
}
