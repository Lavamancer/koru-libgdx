package io.anuke.koru.ucore.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.Array;

import io.anuke.koru.ucore.core.Core;
import io.anuke.koru.ucore.core.Inputs;
import io.anuke.koru.ucore.scene.Element;
import io.anuke.koru.ucore.scene.Scene;
import io.anuke.koru.ucore.scene.Skin;
import io.anuke.koru.ucore.scene.style.Drawable;
import io.anuke.koru.ucore.scene.ui.Dialog;
import io.anuke.koru.ucore.scene.ui.layout.Table;

public class SceneModule extends Module{
	private static String[] colorTypes = {"accent", "title"};
	
	public Scene scene;
	public Skin skin;
	
	public SceneModule(){
		scene = new Scene(Core.batch);
		Inputs.addProcessor(scene);
		
		loadSkin();
		
		loadContext();
	}
	
	protected void loadSkin(){
		if(Gdx.files.internal("ui/uiskin.json").exists()){
			FileHandle fileHandle = Gdx.files.internal("ui/uiskin.json");
			skin = new Skin(fileHandle);
			skin.font().setUseIntegerPositions(false);
		}else{
			Gdx.app.error("UI", "ERROR: No skin file found in ui/uiskin.json. UI features are disabled.");
		}
	}
	
	protected void loadContext(){
		Core.setScene(scene, skin);
		
		if(Core.font == null && skin != null)
			Core.font = skin.font();
		
		for(String s : colorTypes)
			if(Colors.get(s) == null)
				Colors.put(s, Color.WHITE);
		
	}
	
	public boolean hasMouse(){
		return scene.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true) != null;
	}
	
	public boolean hasDialog(){
		return scene.getKeyboardFocus() instanceof Dialog || scene.getScrollFocus() instanceof Dialog;
	}
	
	/**Updates and draws the stage.*/
	public void act(){
		scene.act();
		scene.draw();
	}
	
	/**Gets a drawable by name*/
	public Drawable tex(String name){
		return Core.skin.getDrawable(name);
	}
	
	/**Find an element by name, or by class if prefixed by #.*/
	public <N> N find(String name){
		if(name.startsWith("#")){
			for(Element a : scene.getElements()){
				if(a.getClass().getSimpleName().toLowerCase().equals(name.substring(0, 1))){
					return (N)a;
				}
			}
			return null;
		}
		return (N)scene.find(name);
	}
	
	public <N> Array<N> findList(Class<N> type){
		Array<N> arr = new Array<N>();
		for(Element actor : scene.getElements()){
			if(actor.getClass() == type){
				arr.add((N)actor);
			}
		}
		
		return arr;
	}
	
	/**Creates and adds a new layout to fill the stage.*/
	public Table fill(){
		return scene.table();
	}
	
	@Override
	public void update(){
		act();
	}
	
	@Override
	public void resize(int width, int height){
		scene.resize(width, height);
	}
}
