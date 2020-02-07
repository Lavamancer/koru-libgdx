package io.anuke.koru.ucore.scene.utils;

import io.anuke.koru.ucore.scene.Element;
import io.anuke.koru.ucore.scene.event.InputEvent;

public class IbeamCursorListener extends ClickListener{
	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Element fromActor) {
		super.enter(event, x, y, pointer, fromActor);
		if (pointer == -1 && event.getTarget().isVisible()) {
			Cursors.setIbeam();
		}
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Element toActor) {
		super.exit(event, x, y, pointer, toActor);
		if (pointer == -1) {
			Cursors.restoreCursor();
		}
	}
}
