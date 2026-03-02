package com.ihanuat.mod.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tracks Rose Dragon / Rose Dragon Egg pet XP from the tab list.
 *
 * <p>
 * Uses two independent scans across all tab-list entries so the
 * pet-name line and the XP-bar line do not need to be adjacent (they
 * can be separated by several entries due to Hypixel's column layout).
 *
 * <p>
 * absoluteXp = CUMULATIVE_XP[level] + currentXpWithinLevel
 * The per-tick delta is forwarded to ProfitManager as "Rose Dragon XP".
 * Tracking stops the moment MAX LEVEL is detected.
 */
public class RoseDragonXpTracker {

    // ── Leveling table ────────────────────────────────────────────────────────
    // Index = level (1‒200). Value = cumulative XP required to REACH that level.
    // Source: rose_dragon.txt (cumulativeXp column).
    private static final long[] CUMULATIVE_XP = {
            /* 0 – unused */ 0L,
            // 1-10
            0L, 660L, 1_390L, 2_190L, 3_070L, 4_030L, 5_080L, 6_230L, 7_490L, 8_870L,
            // 11-20
            10_380L, 12_030L, 13_830L, 15_790L, 17_920L, 20_230L, 22_730L, 25_430L, 28_350L, 31_510L,
            // 21-30
            34_930L, 38_630L, 42_630L, 46_980L, 51_730L, 56_930L, 62_630L, 68_930L, 75_930L, 83_730L,
            // 31-40
            92_430L, 102_130L, 112_930L, 124_930L, 138_230L, 152_930L, 169_130L, 186_930L, 206_430L, 227_730L,
            // 41-50
            250_930L, 276_130L, 303_530L, 333_330L, 365_730L, 400_930L, 439_130L, 480_530L, 525_330L, 573_730L,
            // 51-60
            625_930L, 682_130L, 742_530L, 807_330L, 876_730L, 950_930L, 1_030_130L, 1_114_830L, 1_205_530L, 1_302_730L,
            // 61-70
            1_406_930L, 1_518_630L, 1_638_330L, 1_766_530L, 1_903_730L, 2_050_430L, 2_207_130L, 2_374_830L, 2_554_530L,
            2_747_230L,
            // 71-80
            2_953_930L, 3_175_630L, 3_413_330L, 3_668_030L, 3_940_730L, 4_232_430L, 4_544_130L, 4_877_830L, 5_235_530L,
            5_619_230L,
            // 81-90
            6_030_930L, 6_472_630L, 6_949_330L, 7_466_030L, 8_027_730L, 8_639_430L, 9_306_130L, 10_032_830L,
            10_824_530L, 11_686_230L,
            // 91-100
            12_622_930L, 13_639_630L, 14_741_330L, 15_933_030L, 17_219_730L, 18_606_430L, 20_103_130L, 21_719_830L,
            23_466_530L, 25_353_230L,
            // 101-110 (note: lvl 101 has 0 xpForLevel in source data, cumulative same as
            // 100)
            25_353_230L, 25_358_785L, 27_245_485L, 29_132_185L, 31_018_885L, 32_905_585L, 34_792_285L, 36_678_985L,
            38_565_685L, 40_452_385L,
            // 111-120
            42_339_085L, 44_225_785L, 46_112_485L, 47_999_185L, 49_885_885L, 51_772_585L, 53_659_285L, 55_545_985L,
            57_432_685L, 59_319_385L,
            // 121-130
            61_206_085L, 63_092_785L, 64_979_485L, 66_866_185L, 68_752_885L, 70_639_585L, 72_526_285L, 74_412_985L,
            76_299_685L, 78_186_385L,
            // 131-140
            80_073_085L, 81_959_785L, 83_846_485L, 85_733_185L, 87_619_885L, 89_506_585L, 91_393_285L, 93_279_985L,
            95_166_685L, 97_053_385L,
            // 141-150
            98_940_085L, 100_826_785L, 102_713_485L, 104_600_185L, 106_486_885L, 108_373_585L, 110_260_285L,
            112_146_985L, 114_033_685L, 115_920_385L,
            // 151-160
            117_807_085L, 119_693_785L, 121_580_485L, 123_467_185L, 125_353_885L, 127_240_585L, 129_127_285L,
            131_013_985L, 132_900_685L, 134_787_385L,
            // 161-170
            136_674_085L, 138_560_785L, 140_447_485L, 142_334_185L, 144_220_885L, 146_107_585L, 147_994_285L,
            149_880_985L, 151_767_685L, 153_654_385L,
            // 171-180
            155_541_085L, 157_427_785L, 159_314_485L, 161_201_185L, 163_087_885L, 164_974_585L, 166_861_285L,
            168_747_985L, 170_634_685L, 172_521_385L,
            // 181-190
            174_408_085L, 176_294_785L, 178_181_485L, 180_068_185L, 181_954_885L, 183_841_585L, 185_728_285L,
            187_614_985L, 189_501_685L, 191_388_385L,
            // 191-200
            193_275_085L, 195_161_785L, 197_048_485L, 198_935_185L, 200_821_885L, 202_708_585L, 204_595_285L,
            206_481_985L, 208_368_685L, 210_255_385L
    };

    private static final int MAX_LEVEL = 200;

    // Matches "[Lvl 94] Rose Dragon Egg" or "[Lvl 150] Rose Dragon" (lenient – no $
    // anchor)
    private static final Pattern PET_NAME_PATTERN = Pattern.compile(
            "\\[Lvl\\s*(\\d+)]\\s*Rose Dragon(?:\\s+Egg)?",
            Pattern.CASE_INSENSITIVE);

    // Matches "MAX LEVEL"
    private static final Pattern MAX_LEVEL_PATTERN = Pattern.compile(
            "MAX\\s*LEVEL", Pattern.CASE_INSENSITIVE);

    // Matches the current-XP part of "310,168/1.4M XP (22.4%)" or "590,533.4/1.3M
    // XP (45.9%)"
    // Requires "XP" somewhere on the line for specificity.
    private static final Pattern XP_VALUE_PATTERN = Pattern.compile(
            "([\\d,]+(?:\\.\\d+)?)\\s*/[\\d,.]+[KkMmBb]?\\s*XP",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern STRIP_COLOR = Pattern.compile("(?i)\u00A7[0-9A-FK-OR]");

    // Last observed absolute XP across the full 1-200 range. -1 = not calibrated.
    private static long lastAbsoluteXp = -1;

    // ── Public API ────────────────────────────────────────────────────────────

    public static void reset() {
        lastAbsoluteXp = -1;
    }

    /**
     * Called every client tick from {@link ProfitManager#update}.
     * Uses two independent scans so the pet-name line and XP-bar line
     * do not need to be adjacent in the PlayerInfo list.
     */
    public static void update(Minecraft client) {
        if (client.getConnection() == null)
            return;

        // Collect all tab-list lines as stripped plain text
        Collection<PlayerInfo> players = client.getConnection().getListedOnlinePlayers();
        List<String> tabLines = new ArrayList<>(players.size());
        for (PlayerInfo info : players) {
            String raw = (info.getTabListDisplayName() != null)
                    ? info.getTabListDisplayName().getString()
                    : "";
            String clean = STRIP_COLOR.matcher(raw).replaceAll("").replace('\u00A0', ' ').trim();
            tabLines.add(clean);
        }

        // ── Pass 1: find the pet level ────────────────────────────────────────
        int detectedLevel = -1;
        int petLineIndex = -1;
        for (int i = 0; i < tabLines.size(); i++) {
            Matcher m = PET_NAME_PATTERN.matcher(tabLines.get(i));
            if (m.find()) {
                detectedLevel = Integer.parseInt(m.group(1));
                petLineIndex = i;
                break;
            }
        }

        if (detectedLevel < 1 || detectedLevel > MAX_LEVEL) {
            // Pet not found in tab list – hold last baseline
            return;
        }

        // ── Check for MAX LEVEL (search within a window around the pet line) ─
        // Search up to 8 entries after the pet name in case columns shift it
        int windowEnd = Math.min(petLineIndex + 9, tabLines.size());
        for (int i = petLineIndex + 1; i < windowEnd; i++) {
            if (MAX_LEVEL_PATTERN.matcher(tabLines.get(i)).find()) {
                lastAbsoluteXp = -1; // Pet maxed – stop tracking
                return;
            }
        }

        // ── Pass 2: find the XP bar value anywhere in tab list ────────────────
        double currentXpInLevel = -1;
        for (String line : tabLines) {
            Matcher m = XP_VALUE_PATTERN.matcher(line);
            if (m.find()) {
                try {
                    currentXpInLevel = Double.parseDouble(m.group(1).replace(",", ""));
                    break;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (currentXpInLevel < 0) {
            // XP bar not visible this tick – hold baseline
            return;
        }

        // ── Compute absolute XP and record delta ──────────────────────────────
        long absoluteXp = CUMULATIVE_XP[detectedLevel] + (long) currentXpInLevel;

        if (lastAbsoluteXp >= 0 && absoluteXp > lastAbsoluteXp) {
            long delta = absoluteXp - lastAbsoluteXp;
            if (delta < 10_000_000L) { // sanity cap against data glitches
                ProfitManager.addRoseDragonXp((int) delta);
            }
        }

        lastAbsoluteXp = absoluteXp;
    }
}
