package symbolics.division.meret;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AreaMusicComponent implements AreaDataComponent, AreaMusic {
	private static final Music EMPTY = new Music(Holder.direct(SoundEvents.EMPTY),0, 0, false);
	public record MusicCategory(TagKey<JukeboxSong> tag, int minDelay, int maxDelay, boolean replace) {
		private static final Codec<TagKey<JukeboxSong>> TAG_CODEC = TagKey.codec(Registries.JUKEBOX_SONG);
		public static Codec<MusicCategory> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
				TAG_CODEC.fieldOf("category").forGetter(MusicCategory::tag),
				Codec.INT.fieldOf("minDelay").forGetter(MusicCategory::minDelay),
				Codec.INT.fieldOf("maxDelay").forGetter(MusicCategory::maxDelay),
				Codec.BOOL.fieldOf("replace").forGetter(MusicCategory::replace)
			).apply(instance, MusicCategory::new)
		);
		public static MusicCategory of(ResourceLocation tag, int minDelay, int maxDelay, boolean replace) {
			return new MusicCategory(TagKey.create(Registries.JUKEBOX_SONG, tag), minDelay, maxDelay, replace);
		}
	}

	private final Codec<Either<Music, MusicCategory>> CODEC = Codec.either(Music.CODEC, MusicCategory.CODEC);

	@Nullable private Either<Music, MusicCategory> music;

	public AreaMusicComponent() {}

	public AreaMusicComponent(Music music) {
		this.music = Either.left(music);
	}

	public AreaMusicComponent(ResourceLocation category, int minDelay, int maxDelay, boolean replace) {
		this.music = Either.right(MusicCategory.of(category, minDelay, maxDelay, replace));
	}

	@Override
	public void load(AreaSavedData areaSavedData, CompoundTag compoundTag) {
		this.music = CODEC.decode(NbtOps.INSTANCE, compoundTag.get("music")).mapOrElse(
			Pair::getFirst,
			error -> {
				Meret.LOGGER.error("Failed to parse area music component: {}", error);
				return null;
			}
		);
	}

	@Override
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("music", CODEC.encode(this.music, NbtOps.INSTANCE, null).getOrThrow());
		return tag;
	}

	@Override
	public Music getMusic(RegistryAccess registryAccess, RandomSource randomSource) {
		if (this.music == null) return EMPTY;
		return this.music.map(
			song -> song,
			category -> {
				Optional<HolderSet.Named<JukeboxSong>> taggedOptional = registryAccess.registry(Registries.JUKEBOX_SONG).orElseThrow().getTag(category.tag);
				if (taggedOptional.isEmpty()) {
					Meret.LOGGER.warn("Failed to load music for tag: {}", category.tag);
					return EMPTY;
				}
				return taggedOptional.flatMap(
					set -> set.getRandomElement(randomSource)
						.map(Holder::value)
						.map(song -> new Music(song.soundEvent(), category.minDelay, category.maxDelay, category.replace))
				).orElse(EMPTY);
			}
		);
	}
}
