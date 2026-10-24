package uz.menim.smptweaks;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** config/smptweaks.properties faylini o'qiydi (yo'q bo'lsa default bilan yaratadi). */
public final class Config {
	public static int enderPearlCooldown = 60;          // soniya
	public static boolean maceBlockEnchanting = true;
	public static int maceSmashCooldown = 15;           // soniya
	public static double maceMinFallDistance = 1.5;     // blok
	public static int potionDuration = 480;             // soniya (Speed, Strength, Fire Resistance)
	public static String maceCooldownMessage = "Mace zarbasi %s soniyadan keyin tayyor bo'ladi";

	private static final String DEFAULT_FILE = """
			# SMPTweaks sozlamalari. O'zgartirgandan keyin serverni qayta ishga tushiring.

			# Ender pearl cooldown (soniya)
			ender_pearl_cooldown=60

			# Mace'ga enchant qo'yishni taqiqlash (enchant stoli va anvil)
			mace_block_enchanting=true
			# Tepadan tushib urishdan keyingi cooldown (soniya)
			mace_smash_cooldown=15
			# Shu masofadan ko'p yiqilayotganda urish "smash" hisoblanadi (vanilla: 1.5)
			mace_min_fall_distance=1.5
			# Cooldown paytida action bar'da chiqadigan xabar (%s = qolgan soniya)
			mace_cooldown_message=Mace zarbasi %s soniyadan keyin tayyor bo'ladi

			# Speed, Strength, Fire Resistance davomiyligi (soniya). 480 = 8:00
			potion_duration=480
			""";

	private Config() {}

	public static void load() {
		Path file = FabricLoader.getInstance().getConfigDir().resolve("smptweaks.properties");
		try {
			if (!Files.exists(file)) {
				try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
					w.write(DEFAULT_FILE);
				}
				return;
			}
			Properties p = new Properties();
			try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				p.load(r);
			}
			enderPearlCooldown = intOf(p, "ender_pearl_cooldown", enderPearlCooldown);
			maceBlockEnchanting = Boolean.parseBoolean(p.getProperty("mace_block_enchanting", String.valueOf(maceBlockEnchanting)).trim());
			maceSmashCooldown = intOf(p, "mace_smash_cooldown", maceSmashCooldown);
			maceMinFallDistance = doubleOf(p, "mace_min_fall_distance", maceMinFallDistance);
			potionDuration = intOf(p, "potion_duration", potionDuration);
			maceCooldownMessage = p.getProperty("mace_cooldown_message", maceCooldownMessage);
		} catch (IOException | RuntimeException e) {
			SMPTweaksMod.LOGGER.warn("SMPTweaks config o'qilmadi, default qiymatlar ishlatiladi", e);
		}
	}

	private static int intOf(Properties p, String key, int def) {
		try {
			return Integer.parseInt(p.getProperty(key, String.valueOf(def)).trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private static double doubleOf(Properties p, String key, double def) {
		try {
			return Double.parseDouble(p.getProperty(key, String.valueOf(def)).trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
