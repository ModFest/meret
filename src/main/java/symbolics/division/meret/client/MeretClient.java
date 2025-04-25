package symbolics.division.meret.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.meret.Meret;

import java.util.Comparator;
import java.util.Optional;

public class MeretClient implements ClientModInitializer {
	public static final Music EMPTY = new Music(Holder.direct(SoundEvents.EMPTY), 10, 10, false);
	public static final Logger LOGGER = LoggerFactory.getLogger(Meret.ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("[Meret Client] Hisssss!!!!");
	}

	public static Optional<Music> getOverride(LocalPlayer player) {
		if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0) {
			return Optional.empty();
		}
		if (player == null) return Optional.empty();
		AreaSavedData areaSavedData = AreaClientData.getClientLevelData();
		if (areaSavedData == null) return Optional.empty();
		return areaSavedData.findTrackedAreasContaining(player).stream()
			.filter(area -> area.has(Meret.AREA_MUSIC_DATA_COMPONENT))
			.max(Comparator.comparingInt(Area::getPriority))
			.map(area -> area.get(Meret.AREA_MUSIC_DATA_COMPONENT).getMusic());
	}
}
