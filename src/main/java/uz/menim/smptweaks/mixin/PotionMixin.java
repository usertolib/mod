package uz.menim.smptweaks.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uz.menim.smptweaks.Config;
import uz.menim.smptweaks.SMPTweaksMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Speed / Strength / Fire Resistance potionlarining davomiyligini configdagi qiymatga o'rnatadi.
 * Amplifier (strong variantlar) o'zgarmaydi. Splash/lingering koeffitsiyentlarini vanilla o'zi qo'llaydi,
 * shuning uchun splash = davomiylik * 0.75 bo'lib qoladi.
 */
@Mixin(Potion.class)
public abstract class PotionMixin {
	@Unique private List<MobEffectInstance> smptweaks$source;
	@Unique private List<MobEffectInstance> smptweaks$result;

	@Inject(method = "getEffects", at = @At("RETURN"), cancellable = true)
	private void smptweaks$longerEffects(CallbackInfoReturnable<List<MobEffectInstance>> cir) {
		List<MobEffectInstance> original = cir.getReturnValue();
		if (original == null || original.isEmpty()) return;

		List<MobEffectInstance> cached = this.smptweaks$result;
		if (cached != null && original == this.smptweaks$source) {
			cir.setReturnValue(cached);
			return;
		}

		int ticks = Math.max(1, Config.potionDuration) * 20;
		boolean changed = false;
		List<MobEffectInstance> out = new ArrayList<>(original.size());
		for (MobEffectInstance e : original) {
			String id = e.getEffect().value().getDescriptionId();
			if (SMPTweaksMod.isTweakedEffect(id) && e.getDuration() != ticks) {
				out.add(new MobEffectInstance(e.getEffect(), ticks, e.getAmplifier()));
				changed = true;
			} else {
				out.add(e);
			}
		}

		List<MobEffectInstance> result = changed ? List.copyOf(out) : original;
		this.smptweaks$source = original;
		this.smptweaks$result = result;
		if (changed) cir.setReturnValue(result);
	}
}
