package symbolics.division.meret.mixin.client;

import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @Shadow
    @Final
    private RandomSource random;
    @Shadow private int nextSongDelay;

    @Inject(method = "startPlaying", at = @At("HEAD"))
    void startPlaying(Music music, CallbackInfo ci) {
        nextSongDelay = Mth.nextInt(random, music.getMinDelay(), music.getMaxDelay());
    }
}
