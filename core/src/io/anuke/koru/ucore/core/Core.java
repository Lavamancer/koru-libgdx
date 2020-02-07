package io.anuke.koru.ucore.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import io.anuke.koru.ucore.graphics.Atlas;
import io.anuke.koru.ucore.graphics.Caches;
import io.anuke.koru.ucore.scene.Scene;
import io.anuke.koru.ucore.scene.Skin;

public class Core{
	public static OrthographicCamera camera = new OrthographicCamera();
	public static Batch batch = new SpriteBatch();
	public static Atlas atlas;
	public static BitmapFont font;
	public static int cameraScale = 1;
	
	public static Scene scene;
	public static Skin skin;
	
	static{
		
		for(String s : new ObjectMap.Keys<String>(Colors.getColors())){
			if(s != null)
			Colors.put(s.toLowerCase().replace("_", ""), Colors.get(s));
		}
		
		Colors.put("crimson", Color.SCARLET);
		Colors.put("scarlet", Color.SCARLET);
	}
	
	public static void setScene(Scene ascene, Skin askin){
		if(ascene != null) scene = ascene;
		if(askin != null) skin = askin;
	}

	/* Disposes of all resources, as well as internal resources and skin.*/
	public static void dispose(){
		Draw.dispose();
		Graphics.dispose();
		Caches.dispose();
		Inputs.dispose();
		Sounds.dispose();
		Musics.dispose();
		Timers.dispose();
		
		batch.dispose();
		
		if(scene != null){
			scene.dispose();
		}
	
		if(atlas != null){
			atlas.dispose();
		}
	
		if(skin != null){
			skin.dispose();
		}
		
		if(font != null){
			font.dispose();
		}
	}
}
