package com.ihanuat.mod.modules;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

/**
 * Parses farming fortune values from the Hypixel SkyBlock tab list.
 *
 * Expected tab-list entries (after stripping colour codes):
 *   Farming Fortune: ☘1,234
 *   Wheat Fortune: ☘567        (or any crop name)
 */
public class FarmingFortuneParser {

    private static final Pattern FARMING_FORTUNE_PATTERN =
            Pattern.compile("Farming Fortune:\\s*☘?\\s*([\\d,]+)");

    private static final Pattern CROP_FORTUNE_PATTERN =
            Pattern.compile("(\\w+)\\s+Fortune:\\s*☘?\\s*([\\d,]+)");

    private static String lastFarmingFortune = "";
    private static String lastCropFortune = "";
    private static long lastParseTimeMs = 0;
    private static final long PARSE_INTERVAL_MS = 1000; // re-parse at most once per second

    /**
     * Scans the current tab list and caches the farming / crop fortune values.
     * Results are cached for one second to avoid redundant parsing every frame.
     */
    public static void parse(Minecraft client) {
        long now = System.currentTimeMillis();
        if (now - lastParseTimeMs < PARSE_INTERVAL_MS)
            return;
        lastParseTimeMs = now;

        if (client.getConnection() == null || client.player == null)
            return;

        String farmingFortune = "";
        String cropFortune = "";

        Collection<PlayerInfo> players = client.getConnection().getListedOnlinePlayers();

        for (PlayerInfo info : players) {
            String name = "";
            if (info.getTabListDisplayName() != null) {
                name = info.getTabListDisplayName().getString();
            } else if (info.getProfile() != null) {
                name = String.valueOf(info.getProfile());
            }

            String clean = name.replaceAll("(?i)\u00A7[0-9a-fk-or]", "").trim();
            String normalized = clean.replace('\u00A0', ' ');

            Matcher farmingMatcher = FARMING_FORTUNE_PATTERN.matcher(normalized);
            if (farmingMatcher.find()) {
                farmingFortune = farmingMatcher.group(1);
                continue;
            }

            Matcher cropMatcher = CROP_FORTUNE_PATTERN.matcher(normalized);
            if (cropMatcher.find()) {
                String fortuneType = cropMatcher.group(1);
                if (!fortuneType.equalsIgnoreCase("Farming")) {
                    cropFortune = cropMatcher.group(2);
                }
            }
        }

        lastFarmingFortune = farmingFortune;
        lastCropFortune = cropFortune;
    }

    /** Returns a display string such as "☘1,234 + ☘567" or "N/A". */
    public static String getFortuneDisplay() {
        if (lastFarmingFortune.isEmpty() && lastCropFortune.isEmpty()) {
            return "N/A";
        }
        if (!lastFarmingFortune.isEmpty() && !lastCropFortune.isEmpty()) {
            return "\u2618" + lastFarmingFortune + " + \u2618" + lastCropFortune;
        }
        if (!lastFarmingFortune.isEmpty()) {
            return "\u2618" + lastFarmingFortune;
        }
        return "\u2618" + lastCropFortune;
    }
}
