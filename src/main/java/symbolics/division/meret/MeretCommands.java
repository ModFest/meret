package symbolics.division.meret;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MeretCommands {
	private static MutableComponent feedback(ChatFormatting bodyFormatting, Component... siblings) {
		MutableComponent prefix = Component.literal("").append(Component.literal("[Meret] ").withStyle(ChatFormatting.DARK_RED));
		for (Component sibling : siblings) {
			prefix = prefix.append(sibling.getStyle().getColor() == null ? sibling.copy().withStyle(bodyFormatting) : sibling);
		}
		return prefix;
	}

	private static int setMusic(Consumer<Component> feedback, MinecraftServer server, Area area, ResourceLocation sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
		SoundEvent soundEvent = SoundEvent.createVariableRangeEvent((sound));
		Music music = new Music(Holder.direct(soundEvent), minDelay, maxDelay, replaceCurrentMusic);
		area.put(server, Meret.AREA_MUSIC_DATA_COMPONENT, new AreaMusicComponent(music));
		feedback.accept(feedback(ChatFormatting.GREEN,
			Component.literal("music override for "),
			Component.literal(area.getId().toString()).withStyle(ChatFormatting.WHITE),
			Component.literal(" set to "),
			Component.literal(sound.toString()).withStyle(ChatFormatting.WHITE)
		));
		return 1;
	}

	private static int clearMusic(Consumer<Component> feedback, MinecraftServer server, Area area) {
		if (!area.has(Meret.AREA_MUSIC_DATA_COMPONENT)) {
			feedback.accept(feedback(ChatFormatting.YELLOW,
				Component.literal("no music was set in "),
				Component.literal(area.getId().toString()).withStyle(ChatFormatting.WHITE)
			));
		}
		area.remove(server, Meret.AREA_MUSIC_DATA_COMPONENT);
		feedback.accept(feedback(ChatFormatting.GREEN,
			Component.literal("music override cleared for "),
			Component.literal(area.getId().toString()).withStyle(ChatFormatting.WHITE)
		));
		return 1;
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
		dispatcher.register(
			literal("meret")
				.then(literal("set")
					.then(argument("area", ResourceLocationArgument.id()).suggests(AreaArgument::listSuggestions)
						.then(argument("sound", ResourceLocationArgument.id())
							.suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS))
							.then(argument("minDelay", IntegerArgumentType.integer(0))
								.then(argument("maxDelay", IntegerArgumentType.integer(0))
									.then(argument("replaceCurrent", BoolArgumentType.bool())
										.executes(c -> setMusic(
											c.getSource()::sendSystemMessage,
											c.getSource().getServer(),
											AreaArgument.getArea(c, "area"),
											ResourceLocationArgument.getId(c, "sound"),
											IntegerArgumentType.getInteger(c, "minDelay"),
											IntegerArgumentType.getInteger(c, "maxDelay"),
											BoolArgumentType.getBool(c, "replaceCurrent")
										))
									)
								)
							)
						)
					)
				)
				.then(literal("clear")
					.then(argument("area", ResourceLocationArgument.id()).suggests(AreaArgument::listSuggestions)
						.executes(c -> clearMusic(
							c.getSource()::sendSystemMessage,
							c.getSource().getServer(),
							AreaArgument.getArea(c, "area")
						))
					)
				)
			.requires(source -> source.hasPermission(2))
		);
	}
}
