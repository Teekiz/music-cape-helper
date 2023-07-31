package com.musiccapehelper;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Music Cape Helper"
)
public class MusicCapeHelperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MusicCapeHelperConfig config;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");


	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		clientThread.invokeAtTickEnd(() ->
		{
			Widget[] musicList = client.getWidget(239, 6).getChildren();

			List<Widget> filteredList = Arrays.stream(musicList)
				.filter(c -> Integer.toHexString(c.getTextColor()).equals("ff0000"))
				.collect(Collectors.toList());

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Widget loaded ABC: " + filteredList.size(), null);
		});
	}


	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
