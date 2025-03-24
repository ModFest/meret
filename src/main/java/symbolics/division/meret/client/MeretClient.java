package symbolics.division.meret.client;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;
import symbolics.division.meret.Meret;

import java.util.Comparator;

public class MeretClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }

    @Nullable
    public static Music getOverride(LocalPlayer player) {
        if (player == null) return null;

        AreaSavedData areaSavedData = AreaClientData.getClientLevelData();
        if (areaSavedData == null) return null;
        var optionalArea = areaSavedData.findTrackedAreasContaining(player).stream()
            .filter(area -> area.has(Meret.AREA_MUSIC_DATA_COMPONENT))
            .max(Comparator.comparingInt(Area::getPriority));

        return optionalArea.map(area -> area.get(Meret.AREA_MUSIC_DATA_COMPONENT).music).orElse(null);
    }
}
