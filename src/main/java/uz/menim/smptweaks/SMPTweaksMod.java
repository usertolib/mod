package uz.menim.smptweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseCooldown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SMPTweaksMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("SMPTweaks");

	/** O'yinchi UUID -> mace smash cooldown tugaydigan vaqt (ms). */
	private static final Map<UUID, Long> MACE_UNTIL = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		Config.load();

		// 1) Ender pearl cooldown: vanilla use_cooldown komponentini almashtiramiz.
		if (Config.enderPearlCooldown > 0) {
			DefaultItemComponentEvents.MODIFY.register(context ->
					context.modify(Items.ENDER_PEARL, builder ->
							builder.set(DataComponents.USE_COOLDOWN,
									new UseCooldown((float) Config.enderPearlCooldown, Optional.empty()))));
		}

		// 2) Mace'ga enchant qo'yishni taqiqlash (enchant stoli, anvil).
		EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
			if (Config.maceBlockEnchanting && target.is(Items.MACE)) {
				return TriState.FALSE;
			}
			return TriState.DEFAULT;
		});

		// 3) Mace smash cooldown.
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!source.typeHolder().is(DamageTypes.PLAYER_ATTACK)) return true;
			if (!(source.getDirectEntity() instanceof ServerPlayer attacker)) return true;
			if (!attacker.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).is(Items.MACE)) return true;
			// Faqat "smash" (yiqilayotganda urish) hisoblanadi; elytra bilan uchayotganda emas.
			if (!(attacker.fallDistance > Config.maceMinFallDistance) || attacker.isFallFlying()) return true;

			long now = System.currentTimeMillis();
			Long until = MACE_UNTIL.get(attacker.getUUID());
			if (until != null && now < until) {
				long left = (until - now + 999L) / 1000L;
				String text = Config.maceCooldownMessage.replace("%s", String.valueOf(left));
				attacker.displayClientMessage(Component.literal(text).withStyle(ChatFormatting.RED), true);
				return false; // zarba bekor qilinadi
			}

			MACE_UNTIL.values().removeIf(t -> t < now);
			MACE_UNTIL.put(attacker.getUUID(), now + Config.maceSmashCooldown * 1000L);
			return true;
		});

		LOGGER.info("SMPTweaks yoqildi.");
	}

	/** Potion davomiyligi o'zgartiriladigan effektlar (PotionMixin ishlatadi). */
	public static boolean isTweakedEffect(String descriptionId) {
		return descriptionId.equals("effect.minecraft.speed")
				|| descriptionId.equals("effect.minecraft.strength")
				|| descriptionId.equals("effect.minecraft.fire_resistance");
	}
}
