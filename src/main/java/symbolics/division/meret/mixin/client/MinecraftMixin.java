package symbolics.division.meret.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.meret.Meret;
import symbolics.division.meret.client.MeretClient;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/Optionull;map(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;")
	)
	public Object meretSituationalMusic(Object original) {
		if (original != null) return original; // Preserve screen music.
		Minecraft self = (Minecraft) (Object) this;
		return MeretClient.getOverride(self.player).orElse(Meret.CONFIG.overrideAll ? MeretClient.EMPTY : null);
	}
}
