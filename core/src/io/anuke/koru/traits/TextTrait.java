package io.anuke.koru.traits;

import com.badlogic.gdx.graphics.Color;

import io.anuke.koru.network.syncing.SyncData.Synced;
import io.anuke.koru.ucore.ecs.Trait;

@Synced
public class TextTrait extends Trait{
	public Color color = Color.WHITE;
	public String text;
}
