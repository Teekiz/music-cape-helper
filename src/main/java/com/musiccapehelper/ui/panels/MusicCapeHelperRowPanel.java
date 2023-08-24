package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.ui.rows.MusicCapeHelperRow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperRowPanel extends JPanel
{
	final MusicCapeHelperPlugin plugin;
	final MusicCapeHelperConfig config;
	boolean isOnMapPanel;
	private final JScrollPane musicScrollPane;
	private final JPanel musicScrollPaneContentPanel;
	private JLabel songNameLabelHeader;

	public MusicCapeHelperRowPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, boolean isOnMapPanel)
	{
		this.plugin = plugin;
		this.config = config;
		this.isOnMapPanel = isOnMapPanel;

		//name region required
		setLayout(new BorderLayout(0, 10));
		setBorder(new EmptyBorder(10, 0, 10, 0));

		songNameLabelHeader = new JLabel();
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		songNameLabelHeader.setHorizontalAlignment(JLabel.CENTER);
		add(songNameLabelHeader, BorderLayout.PAGE_START);

		musicScrollPane = new JScrollPane();
		musicScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		musicScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		musicScrollPaneContentPanel = new JPanel();
		musicScrollPaneContentPanel.setLayout(new GridBagLayout());
		musicScrollPaneContentPanel.setBackground(new Color(85,85,85));
		musicScrollPane.setViewportView(musicScrollPaneContentPanel);
		musicScrollPane.setPreferredSize(new Dimension(0, 700));
		add(musicScrollPane, BorderLayout.CENTER);

		tabSwitched(isOnMapPanel);
	}

	public void addMusicRows(List<MusicCapeHelperRow> rows)
	{
		musicScrollPaneContentPanel.removeAll();

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

		rows.forEach(r ->
		{
			musicScrollPaneContentPanel.add(r, gridBagConstraints);
			gridBagConstraints.gridy++;
		});
		/*
			creates a new jpanel to take up space for the rest of the page, this prevents the remaining space being distributed between other rows.
		 */
		JPanel emptySpacePanel = new JPanel();
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		musicScrollPaneContentPanel.add(emptySpacePanel, gridBagConstraints);

		tabSwitched(isOnMapPanel);
	}


	public void tabSwitched(boolean setToMapPanel)
	{
		isOnMapPanel = setToMapPanel;
		if (isOnMapPanel)
		{
			songNameLabelHeader.setText("Map List");
			songNameLabelHeader.setToolTipText("A list of all markers currently pinned to the map.");

			Arrays.stream(musicScrollPaneContentPanel.getComponents())
				.filter(r -> !r.isEnabled())
				.forEach(r -> r.setVisible(false));
		}
		else
		{
			songNameLabelHeader.setText("Music Track List");
			songNameLabelHeader.setToolTipText("A list of Old School Runescapes' music tracks.");

			Arrays.stream(musicScrollPaneContentPanel.getComponents())
				.filter(r -> !r.isVisible())
				.forEach(r -> r.setVisible(true));
		}

		refreshList();
	}

	//todo - fix
	public void refreshList()
	{
		musicScrollPaneContentPanel.revalidate();
		musicScrollPaneContentPanel.repaint();
		musicScrollPane.revalidate();
		musicScrollPane.repaint();
	}
}
