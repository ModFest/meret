package symbolics.division.meret;

import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;

public interface AreaMusic {
	Music getMusic(RegistryAccess registryAccess, RandomSource randomSource);
}
