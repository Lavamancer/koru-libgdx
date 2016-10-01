package io.anuke.koru.modules;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.koru.items.Item;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.network.packets.BlockInputPacket;
import io.anuke.koru.network.packets.InputPacket;
import io.anuke.koru.network.packets.StoreItemPacket;
import io.anuke.koru.systems.CollisionSystem;
import io.anuke.koru.utils.InputType;
import io.anuke.koru.world.InventoryTileData;
import io.anuke.koru.world.Materials;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.World;
import io.anuke.ucore.modules.Module;

public class Input extends Module<Koru> implements InputProcessor{
	private Vector2 vector = new Vector2();
	public CollisionSystem collisions;
	KoruEntity player;
	
	public void init(){
		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(getModule(UI.class).stage);
		plex.addProcessor(this);
		Gdx.input.setInputProcessor(plex);
		player = getModule(ClientData.class).player;
		collisions = new CollisionSystem();
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.R)) sendInput(InputType.r);
		
		float speed = 2f;
		
		if(Gdx.input.isKeyPressed(Keys.W)){
			vector.y += speed;
		}
		if(Gdx.input.isKeyPressed(Keys.A)){
			vector.x -= speed;
		}
		if(Gdx.input.isKeyPressed(Keys.S)){
			vector.y -= speed;
		}
		if(Gdx.input.isKeyPressed(Keys.D)){
			vector.x += speed;
		}
		
		vector.limit(speed);
		
		if(!collisions.checkTerrainCollisions(getModule(World.class), player, vector.x, 0)){
			player.position().add(vector.x, 0);
		}
		
		if(!collisions.checkTerrainCollisions(getModule(World.class), player, 0, vector.y)){
			player.position().add(0, vector.y);
		}
		
		vector.set(0,0);
		
		if(Gdx.input.isKeyJustPressed(Keys.T)){
			GridPoint2 point =cursorblock();
			Tile tile = getModule(World.class).getTile(point);
			if(tile.blockdata != null && tile.blockdata instanceof InventoryTileData){
				StoreItemPacket packet = new StoreItemPacket();
				packet.x = point.x;
				packet.y = point.y;
				packet.stack = new ItemStack(Item.wood, 20);
				getModule(Network.class).client.sendTCP(packet);
			}
		}
	}
	
	void sendInput(InputType type){
		InputPacket packet = new InputPacket();
		packet.type = type;
		getModule(Network.class).client.sendTCP(packet);
	}

	@Override
	public boolean keyDown(int keycode){
		return false;
	}

	@Override
	public boolean keyUp(int keycode){
		return false;
	}

	@Override
	public boolean keyTyped(char character){
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		BlockInputPacket packet = new BlockInputPacket();
		if(button == Buttons.LEFT){
			sendInput(InputType.leftclick_down);
			packet.material = Materials.pinetree2;
		}else if(button == Buttons.RIGHT){
			sendInput(InputType.rightclick_down);
			packet.material = Materials.air;
		}
		
		GridPoint2 mouse = cursorblock();
		packet.x = mouse.x;
		packet.y = mouse.y;
		getModule(Network.class).client.sendTCP(packet);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if(button == Buttons.LEFT){
			sendInput(InputType.leftclick_up);
		}else if(button == Buttons.RIGHT){
			sendInput(InputType.rightclick_up);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}

	@Override
	public boolean scrolled(int amount){
		getModule(Renderer.class).camera.zoom += amount/10f;
		return false;
	}
	
	public GridPoint2 cursorblock(){
		Vector3 v = getModule(Renderer.class).camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(), 1f));
		return new GridPoint2(World.tile(v.x), World.tile(v.y));
	}

}