package io.anuke.koru.ucore.scene.actions;

import com.badlogic.gdx.utils.Align;

import io.anuke.koru.ucore.scene.Action;

public class OriginAction extends Action{

	@Override
	public boolean act(float delta){
		actor.setOrigin(Align.center);
		return true;
	}

}
