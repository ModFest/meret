package symbolics.division.meret;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MeretCommands {
	private static int setMusic(MinecraftServer server, Area area, ResourceLocation sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
		SoundEvent soundEvent = SoundEvent.createVariableRangeEvent((sound));
		Music music = new Music(Holder.direct(soundEvent), minDelay, maxDelay, replaceCurrentMusic);
		area.put(server, Meret.AREA_MUSIC_DATA_COMPONENT, new AreaMusicComponent(music));
		return 1;
	}

	private static int clearMusic(MinecraftServer server, Area area) {
		area.remove(server, Meret.AREA_MUSIC_DATA_COMPONENT);
		return 1;
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
		dispatcher.register(
			literal("meret")
				.then(literal("set")
					.then(argument("area", AreaArgument.area())
						.then(argument("sound", ResourceLocationArgument.id())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.then(argument("min_delay", IntegerArgumentType.integer())
								.then(argument("max_delay", IntegerArgumentType.integer())
									.then(argument("replace_current_music", BoolArgumentType.bool())
										.executes(c -> setMusic(
											c.getSource().getServer(),
											AreaArgument.getArea(c, "area"),
											ResourceLocationArgument.getId(c, "sound"),
											IntegerArgumentType.getInteger(c, "min_delay"),
											IntegerArgumentType.getInteger(c, "max_delay"),
											BoolArgumentType.getBool(c, "replace_current_music")
										))
									)
								)
							)
						)
					)
				)
				.then(literal("clear")
					.then(argument("area", AreaArgument.area())
						.executes(c -> clearMusic(
							c.getSource().getServer(),
							AreaArgument.getArea(c, "area")
						))
					)
				)
		);
	}
}
