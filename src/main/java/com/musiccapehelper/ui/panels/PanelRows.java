package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicExpandedRows;
import com.musiccapehelper.MusicHintArrow;
import com.musiccapehelper.MusicMapPoints;
import com.musiccapehelper.MusicPanelRows;
import com.musiccapehelper.enums.data.IconData;
import com.musiccapehelper.ui.rows.Row;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

public class PanelRows extends JPanel
{
	private final MusicMapPoints musicMapPoints;
	@Getter
	boolean isOnMapPanel;


	private final JScrollPane musicScrollPane;
	private final JPanel musicScrollPaneContentPanel;
	private JPanel emptySpacePanel;
	private final JLabel songNameLabelHeader;
	private final MusicPanelRows musicPanelRows;

	public PanelRows(boolean isOnMapPanel, MusicPanelRows musicPanelRows, MusicMapPoints musicMapPoints,
					 MusicExpandedRows musicExpandedRows, MusicHintArrow musicHintArrow)
	{
		this.isOnMapPanel = isOnMapPanel;
		this.musicPanelRows = musicPanelRows;
		this.musicMapPoints = musicMapPoints;

		//name region required
		setLayout(new BorderLayout(0, 10));
		setBorder(new EmptyBorder(10, 0, 10, 0));

		// header label and control panels
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		songNameLabelHeader = new JLabel();
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		songNameLabelHeader.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		headerPanel.add(songNameLabelHeader);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(0, 5, 0, 5));

		JButton pinAllControl = new JButton();
		pinAllControl.setIcon(IconData.ADD_ICON.getIcon());
		pinAllControl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				musicMapPoints.addOrRemoveAllMapPoints(true);
			}
		});
		controlPanel.add(pinAllControl);

		JButton unpinAllControl = new JButton();
		unpinAllControl.setIcon(IconData.REMOVE_ICON.getIcon());
		controlPanel.add(unpinAllControl);
		unpinAllControl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				musicMapPoints.addOrRemoveAllMapPoints(false);
			}
		});

		JButton removeHintArrowControl = new JButton();
		removeHintArrowControl.setIcon(IconData.HIDE_HINT_ARROW.getIcon());
		controlPanel.add(removeHintArrowControl);
		removeHintArrowControl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				musicHintArrow.clearHintArrow();
			}
		});

		JButton shrinkAllControl = new JButton();
		shrinkAllControl.setIcon(IconData.UP_ICON.getIcon());
		controlPanel.add(shrinkAllControl);
		shrinkAllControl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				musicExpandedRows.addOrRemoveAllExpandedRows(false, musicPanelRows.getRows());
			}
		});

		JButton expandAllControl = new JButton();
		expandAllControl.setIcon(IconData.DOWN_ICON.getIcon());
		controlPanel.add(expandAllControl);
		expandAllControl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				musicExpandedRows.addOrRemoveAllExpandedRows(true, musicPanelRows.getRows());
			}
		});

		headerPanel.add(controlPanel);
		add(headerPanel, BorderLayout.PAGE_START);

		//scroll pane
		musicScrollPane = new JScrollPane();
		musicScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		musicScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		musicScrollPaneContentPanel = new JPanel();
		musicScrollPaneContentPanel.setLayout(new GridBagLayout());
		musicScrollPane.setViewportView(musicScrollPaneContentPanel);
		musicScrollPane.setPreferredSize(new Dimension(0, 650));
		add(musicScrollPane, BorderLayout.CENTER);

		tabSwitched(isOnMapPanel);
	}

	public void addMusicRows()
	{
		musicScrollPaneContentPanel.removeAll();

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

		musicPanelRows.getRows().forEach(r ->
		{
			musicScrollPaneContentPanel.add(r, gridBagConstraints);
			gridBagConstraints.gridy++;
		});
		/*
			creates a new jpanel to take up space for the rest of the page, this prevents the remaining space being distributed between other rows.
		 */
		emptySpacePanel = new JPanel();
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		emptySpacePanel.setBackground(new Color(70, 70, 70));
		emptySpacePanel.setOpaque(true);
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

		addNoRowsReturnedTextLabel();
		refreshList();
	}

	public void addNoRowsReturnedTextLabel()
	{
		//checks to see if all rows are hidden, if they are add to the empty space panel a label
		if (Arrays.stream(musicScrollPaneContentPanel.getComponents())
			.filter(r -> r instanceof Row)
			.noneMatch(Component::isVisible) && emptySpacePanel != null)
		{
			emptySpacePanel.removeAll();
			JLabel noRowsLabel = new JLabel();
			noRowsLabel.setHorizontalTextPosition(JLabel.CENTER);
			noRowsLabel.setVerticalTextPosition(JLabel.BOTTOM);

			if (isOnMapPanel && musicMapPoints.getMapPoints().size() == 0)
			{
				noRowsLabel.setText("<html>You have no markers on" +
						"<br/> the map. Click the green" +
						"<br/>  arrow on the music list" +
						"<br/> page to add new rows to" +
						"<br/> the map.</html>");
				noRowsLabel.setIcon(IconData.ADD_ICON.getIcon());
			}
			else
			{
				noRowsLabel.setText("<html> " +
					"<br/>No music tracks available" +
					"<br/> to show. Please change" +
					"<br/> the filters to return" +
					"<br/> available relevant " +
					"<br/> music tracks.</html>");

			}
			emptySpacePanel.add(noRowsLabel);
		}
		else if (emptySpacePanel != null)
		{
			emptySpacePanel.removeAll();
		}
	}

	public void refreshList()
	{
		musicScrollPaneContentPanel.revalidate();
		musicScrollPaneContentPanel.repaint();
		musicScrollPane.revalidate();
		musicScrollPane.repaint();
	}
}
