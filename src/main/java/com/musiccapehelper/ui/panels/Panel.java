package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.MusicExpandedRows;
import com.musiccapehelper.MusicHintArrow;
import com.musiccapehelper.MusicList;
import com.musiccapehelper.MusicMapPoints;
import com.musiccapehelper.MusicPanelRows;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

public class Panel extends PluginPanel
{
	private final PanelRows rowPanel;
	private final MusicPanelRows musicPanelRows;

	public Panel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, MusicList musicList,
				 MusicPanelRows musicPanelRows, MusicMapPoints musicMapPoints, MusicExpandedRows musicExpandedRows, MusicHintArrow musicHintArrow)
	{
		this.musicPanelRows = musicPanelRows;

		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		//title panel
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("Music Cape Helper");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titlePanel.add(titleLabel);
		add(titlePanel);

		add(new PanelSettings(plugin, config, this, musicList));

		rowPanel = new PanelRows(false, musicPanelRows, musicMapPoints, musicExpandedRows, musicHintArrow );
		JPanel displayPanel = new JPanel();
		MaterialTabGroup musicMapTabGroup = new MaterialTabGroup(displayPanel);
		MaterialTab musicTab = new MaterialTab("Music", musicMapTabGroup, rowPanel);
		MaterialTab mapTab = new MaterialTab("Map", musicMapTabGroup, rowPanel);

		musicMapTabGroup.addTab(musicTab);
		musicMapTabGroup.addTab(mapTab);
		musicMapTabGroup.select(musicTab);
		add(musicMapTabGroup, BorderLayout.NORTH);
		add(displayPanel, BorderLayout.CENTER);

		musicTab.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				rowPanel.tabSwitched(false);
			}
		});

		mapTab.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				rowPanel.tabSwitched(true);
			}
		});
	}

	public void updateRow(Row row)
	{
		row.updateRowValues();
		rowPanel.refreshList();
	}

	public void updateAllRows()
	{
		musicPanelRows.getRows().stream().filter(r -> r instanceof MusicRow).forEach(Row::updateRowValues);
		musicPanelRows.getRows().stream().filter(r -> r instanceof HeaderRow).forEach(Row::updateRowValues);
		rowPanel.refreshList();
	}

	//this verifies that only the correct rows are showing.
	public void checkMapRowPanels()
	{
		rowPanel.tabSwitched(rowPanel.isOnMapPanel());
	}

	//this method is used to completely update the panel, with new rows being added where required.
	public void addRowsToPanel(String searchText)
	{
		SwingUtilities.invokeLater(() ->
		{
			musicPanelRows.createMusicRows(searchText);
			rowPanel.addMusicRows();
		});
	}
}
