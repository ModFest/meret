package symbolics.division.meret;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.Music;

public class AreaMusicComponent implements AreaDataComponent {
	private Music music;

	public AreaMusicComponent(Music music) {
		this.music = music;
	}

	@Override
	public void load(AreaSavedData areaSavedData, CompoundTag compoundTag) {
		this.music = Music.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("music")).getOrThrow().getFirst();
	}

	@Override
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("music", Music.CODEC.encode(this.music, NbtOps.INSTANCE, null).getOrThrow());
		return tag;
	}

	public Music getMusic() {
		return music;
	}
}
