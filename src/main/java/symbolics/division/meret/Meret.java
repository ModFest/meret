package symbolics.division.meret;

import dev.doublekekse.area_lib.component.AreaDataComponentType;
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Meret implements ModInitializer {
	public static final String ID = "meret";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final AreaDataComponentType<AreaMusicComponent> AREA_MUSIC_DATA_COMPONENT = AreaDataComponentTypeRegistry.registerTracking(id("area_music"), () -> new AreaMusicComponent(null));

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(ID, id);
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(MeretCommands::register);
		LOGGER.info("[Meret] Rattle on, snakes!");
	}
}
