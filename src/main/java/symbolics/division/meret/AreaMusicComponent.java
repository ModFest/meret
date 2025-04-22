package symbolics.division.meret;

import com.mojang.datafixers.util.Pair;
import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;

public class AreaMusicComponent implements AreaDataComponent {
	private static final Music EMPTY = new Music(Holder.direct(SoundEvents.EMPTY), 0, 0, false);
	private Music music;

	public AreaMusicComponent(Music music) {
		this.music = music;
	}

	@Override
	public void load(AreaSavedData areaSavedData, CompoundTag compoundTag) {
		this.music = Music.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("music")).mapOrElse(
			Pair::getFirst,
			error -> {
				Meret.LOGGER.error("failed to decode area music data: {}", error);
				return EMPTY;
			}
		);
	}

	@Override
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("music", Music.CODEC.encode(this.music, NbtOps.INSTANCE, null).getOrThrow());
		return tag;
	}

	public Music getMusic() {
		return music == null ? EMPTY : music;
	}
}
