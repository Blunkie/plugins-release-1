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

import com.google.common.collect.ImmutableMap;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

enum TwitchEmotes
{
    BEDGE("Bedge"),
    CLUELESS("Clueless"),
    DEADGE("Deadge"),
    FEELSSTRONGMAN("Feelsstrongman"),
    KAPPA("Kappa"),
    KEKW("Kekw"),
    PAUSECHAMP("Pausechamp"),
    PEEPOHAPPY("Peepohappy"),
    PEEPOSAD("Peeposad"),
    PEPEGA("Pepega"),
    PEPELAUGH("Pepelaugh"),
    POG("Pog"),
    POGGERS("Poggers"),
    SADGE("Sadge"),
    TROLLDESPAIR("Trolldespair")

    ;

    private static final Map<String, TwitchEmotes> emoteMap;

    private final String trigger;

    static
    {
        ImmutableMap.Builder<String, TwitchEmotes> builder = new ImmutableMap.Builder<>();

        for (final TwitchEmotes emoji : values())
        {
            builder.put(emoji.trigger, emoji);
        }

        emoteMap = builder.build();
    }

    TwitchEmotes(String trigger)
    {
        this.trigger = trigger;
    }

    BufferedImage loadImage() throws IOException {

        return ImageUtil.loadImageResource(getClass(), this.name().toLowerCase() + ".png");
    }

    static TwitchEmotes getEmoji(String trigger)
    {
        return emoteMap.get(trigger);
    }
}
