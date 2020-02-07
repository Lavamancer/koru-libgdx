package io.anuke.koru.ucore.scene.builders;

import io.anuke.koru.ucore.function.FieldListenable;
import io.anuke.koru.ucore.scene.ui.TextField;

public class field extends builder<field, TextField>{
	
	public field(String text, FieldListenable listener){
		element = new TextField(text);
		element.changed(()->{
			listener.listen(element.getText());
		});
		cell = context().add(element);
	}
	
	public field(FieldListenable listener){
		this("", listener);
	}
}
