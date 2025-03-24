package symbolics.division.meret;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class MeretConfig extends WrappedConfig {
	@Comment("Whether to ignore all situational music when no meret override is present.")
	@Comment("Menu music will still play.")
	public boolean overrideAll = false;
}
