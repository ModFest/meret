package symbolics.division.meret.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.meret.Meret;
import symbolics.division.meret.client.MeretClient;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public LocalPlayer player;

	@Inject(
		method = "getSituationalMusic",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;level()Lnet/minecraft/world/level/Level;",
			shift = At.Shift.BEFORE),
		cancellable = true
	)
	private void meretSituationalMusic(CallbackInfoReturnable<Music> cir) {
		final Optional<Music> music = MeretClient.getOverride(this.player);

		// overrideAll prevents the regular level music from playing.
		if (music.isPresent() || Meret.CONFIG.overrideAll) {
			// A null music instance is safe.
			// The 1.21.6+ MusicManager shorts circuits null as a no-op, max time 100 frames,
			// and the same check has been mimicked in Music Moods.
			cir.setReturnValue(music.orElse(null));
		}
	}
}
