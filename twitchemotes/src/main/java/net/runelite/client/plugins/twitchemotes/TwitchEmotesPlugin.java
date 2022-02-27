/*
 * Copyright (c) 2019, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.twitchemotes;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.annotation.Nullable;
import javax.inject.Inject;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import java.util.regex.Pattern;

@Slf4j
@Extension
@PluginDescriptor(
		name = "Twitch Emotes",
		description = "Enables twitch emotes in chat.",
		tags = {"twitch","kappa","emote","pog","poggers","bttv","ffz"}
)
public class TwitchEmotesPlugin extends Plugin {

	private static final Pattern WHITESPACE_REGEXP = Pattern.compile("[\\s\\u00A0]");

	private int modIconsStart = -1;

	@Inject
	private Client client;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadTwitchEmotes();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		System.out.println("test twitch");

		switch (chatMessage.getType())
		{
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
			case CLAN_CHAT:
			case CLAN_GUEST_CHAT:
			case CLAN_GIM_CHAT:
			case PRIVATECHAT:
			case PRIVATECHATOUT:
			case MODPRIVATECHAT:
				break;
			default:
				return;
		}
		MessageNode messageNode = chatMessage.getMessageNode();
		String message = messageNode.getValue();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		messageNode.setValue(updatedMessage);
	}

	@Nullable
	String updateMessage(final String message)
	{
		final String[] messageWords = WHITESPACE_REGEXP.split(message);

		boolean editedMessage = false;
		for (int i = 0; i < messageWords.length; i++)
		{
			// Remove tags except for <lt> and <gt>
			final String trigger = Text.removeFormattingTags(messageWords[i]);
			final TwitchEmotes twitchEmotes = TwitchEmotes.getEmoji(trigger);

			if (twitchEmotes == null)
			{
				continue;
			}

			final int emojiId = modIconsStart + twitchEmotes.ordinal();

			messageWords[i] = messageWords[i].replace(trigger, "<img=" + emojiId + ">");
			editedMessage = true;
		}

		// If we haven't edited the message any, don't update it.
		if (!editedMessage)
		{
			return null;
		}

		return Strings.join(messageWords, " ");
	}

	//Loading in all the twitch emotes into java
	private void loadTwitchEmotes()
	{
		final IndexedSprite[] modIcons = client.getModIcons();
		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final TwitchEmotes[] twitchEmotes = TwitchEmotes.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + twitchEmotes.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < twitchEmotes.length; i++)
		{
			final TwitchEmotes twitchEmote = twitchEmotes[i];

			try
			{
				final BufferedImage image = twitchEmote.loadImage();
				final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
				newModIcons[modIconsStart + i] = sprite;
			}
			catch (Exception ex)
			{
				log.warn("Failed to load the sprite for emoji " + twitchEmote, ex);
			}
		}

		client.setModIcons(newModIcons);
	}
}