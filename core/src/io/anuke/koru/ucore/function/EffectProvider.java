package io.anuke.koru.ucore.function;

import com.badlogic.gdx.graphics.Color;

import io.anuke.koru.ucore.core.Effects.Effect;

public interface EffectProvider{
	public void createEffect(Effect effect, Color color, float x, float y, float rotation);
}
