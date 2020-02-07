package io.anuke.koru.ucore.scene.builders;

import com.badlogic.gdx.graphics.Color;

import io.anuke.koru.ucore.function.Listenable;
import io.anuke.koru.ucore.scene.ui.TextButton;

public class button extends builder<button, TextButton>{
	
	public button(String text, Listenable listener){
		this(text, "default", listener);
	}
	
	public button(String text, String style, Listenable listener){
		element = new TextButton(text, style);
		element.clicked(listener);
		cell = context().add(element);
	}
	
	public void textColor(Color color){
		element.getLabel().setColor(color);
	}
}
