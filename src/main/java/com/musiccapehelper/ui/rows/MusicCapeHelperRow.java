package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperRow extends JPanel
{
	@Getter
	protected Music music;
	@Getter
	protected boolean completed;
	@Getter
	protected boolean enabled;

	protected MusicCapeHelperPlugin plugin;
	protected MusicCapeHelperConfig config;

	//contents
	protected final Color labelColour;
	protected JLabel songNameLabel;
	protected JLabel enabledDisabled;
	protected GridBagConstraints gbc;


	public MusicCapeHelperRow(Music music, boolean completed, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.music = music;
		this.completed = completed;
		this.plugin = plugin;
		this.config = config;
		enabled = false;

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		if (completed && plugin.isPlayerLoggedIn()) {labelColour = config.panelCompleteTextColour();}
		else if (!completed && plugin.isPlayerLoggedIn()){labelColour = config.panelIncompleteTextColour();}
		else {labelColour = config.panelDefaultTextColour();}

		songNameLabel = new JLabel(music.getSongName(), JLabel.LEFT);
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		songNameLabel.setForeground(labelColour);
		songNameLabel.setHorizontalAlignment(JLabel.LEFT);

		//song name
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 2, 5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		add(songNameLabel, gbc);

		//song enabled disabled
		enabledDisabled = new JLabel();
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));
		updateRow();
		gbc.gridwidth = 1;
		gbc.gridx = 4;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		add(enabledDisabled, gbc);

		enabledDisabled.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(ColorScheme.DARK_GRAY_COLOR);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				//left click
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					setBackground(ColorScheme.DARK_GRAY_COLOR);
					plugin.rowClicked(MusicCapeHelperRow.this);
				}
				//right click
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					setBackground(ColorScheme.DARK_GRAY_COLOR);
					popUpMenuActions();
				}
			}
		});

		setToolTipText(music.getDescription());

		addRowContents();
	}

	//adds extra content
	public void addRowContents() {}

	public void setEnabledDisabled(Boolean isOnMap)
	{
		enabled = isOnMap;
		updateRow();
	}

	public void updateRow()
	{
		if (enabled)
		{
			enabledDisabled.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/removeicon.png")));
			enabledDisabled.setToolTipText("Click to unpin icon from map");
		}
		else
		{
			enabledDisabled.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/addicon.png")));
			enabledDisabled.setToolTipText("Click to pin icon to the map");
		}
	}

	public void popUpMenuActions()
	{
		//popup contents
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem popupMenuText = new JMenuItem();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

		if (enabled) {
			popupMenuText.setText("Unpin map icon");}
		else {
			popupMenuText.setText("Pin map icon");}
		popupMenuText.setFont(FontManager.getRunescapeSmallFont());
		popupMenu.add(popupMenuText);

		setComponentPopupMenu(popupMenu);

		popupMenuText.addActionListener(a ->
		{
			plugin.rowClicked(MusicCapeHelperRow.this);
		});
	}
}
