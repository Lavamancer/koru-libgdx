package io.anuke.koru.modules;

import static io.anuke.koru.ucore.util.Mathf.inBounds;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.anuke.koru.Koru;
import io.anuke.koru.network.Net;
import io.anuke.koru.network.Net.Mode;
import io.anuke.koru.network.packets.ChunkPacket;
import io.anuke.koru.network.packets.ChunkRequestPacket;
import io.anuke.koru.network.packets.TileUpdatePacket;
import io.anuke.koru.traits.ChunkLoadTrait;
import io.anuke.koru.world.Chunk;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.WorldLoader;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.world.materials.MaterialLayer;
import io.anuke.koru.world.materials.MaterialTypes.Wall;
import io.anuke.koru.ucore.core.Core;
import io.anuke.koru.ucore.core.Timers;
import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.graphics.Hue;
import io.anuke.koru.ucore.modules.Module;

public class World extends Module{
	public static final int chunksize = 16;
	public static final int loadrange = 3;
	public static final int tilesize = 12;
	
	private static Color ambientColor = new Color();
	private static final float[] colors = new float[]{1, 1, 0.9f, 0.5f, 0.2f, 0, 0, 0.5f, 0.9f, 1};
	public final static float timescale = 40000f*0; //temporarily disabled for testing - remove 0 to enable
	
	private boolean updated;
	private GridPoint2 point = new GridPoint2();
	private WorldLoader file;
	private boolean[][] chunkloaded;
	
	public int lastchunkx, lastchunky;
	public Chunk[][] chunks; //client-side tiles
	public Chunk[][] tempchunks; //temporary operation chunks
	public float time = 0f; //world time
	
	public World(WorldLoader loader){
		this();
		file = loader;
	}

	public World(){
		
		if( !Net.server()){
			chunkloaded = new boolean[loadrange * 2][loadrange * 2];
			chunks = new Chunk[loadrange * 2][loadrange * 2];
			tempchunks = new Chunk[loadrange * 2][loadrange * 2];
		}
	}
	
	public void init(){
		
	}
	
	//TODO move time and color related function to a different class?
	public Color getAmbientColor(){
		int index = (int)(time*colors.length);
		float mod = time*colors.length-index;
		float current = colors[index];
		float next = colors[(index == colors.length-1 ?  0 : index+1)];
		
		return Hue.mix(Color.WHITE, Color.BLACK, 1f-(current*(1f-mod) + next*mod), ambientColor);
	}

	@Override
	public void update(){
		if(Net.server() && Timers.get("checkunload", 80)) checkUnloadChunks();
	
		time += Timers.delta()/timescale;
		if(time >= 1f) time = 0f;
		
		updated = false;
		
		if(Net.server()) return;

		int newx = toChunkCoords(Core.camera.position.x);
		int newy = toChunkCoords(Core.camera.position.y);

		//camera moved, update chunks
		if(newx != lastchunkx || newy != lastchunky){
			
			int sx = newx - lastchunkx, sy = newy - lastchunky;
			
			for(int x = 0;x < loadrange * 2;x ++){
				for(int y = 0;y < loadrange * 2;y ++){
					tempchunks[x][y] = chunks[x][y];
				//	if(chunks[x][y] == null) continue;
					//Pools.free(chunks[x][y]);
					//chunks[x][y] = null;
				}
			}
			
			for(int x = 0;x < loadrange * 2;x ++){
				for(int y = 0;y < loadrange * 2;y ++){
					if(!inBounds(x + sx, y + sy, chunks)){
						chunks[x][y] = null;
						continue;
					}
					chunks[x][y] = tempchunks[x + sx][y + sy];
				}
			}
		}

		lastchunkx = newx;
		lastchunky = newy;

		sendChunkRequest();
	}
	
	void checkUnloadChunks(){
		Collection<Chunk> chunks = file.getLoadedChunks();
		Array<Spark> players = Koru.basis.getSparks();
		
		for(Chunk chunk : chunks){
			boolean passed = false;
			
			for(Spark s : players){
				if(!s.has(ChunkLoadTrait.class)) continue;
				
				int ecx = toChunkCoords(s.pos().x);
				int ecy = toChunkCoords(s.pos().y);
				
				if(Math.abs(chunk.x - ecx) <= loadrange && Math.abs(chunk.y - ecy) <= loadrange){
					passed = true;
					break;
				}
			}
			if(passed) continue;
			file.unloadChunk(chunk);
		}
	}

	public void loadChunks(ChunkPacket packet){

		//the relative position of the packet's chunk, to be put in the client's chunk array
		int relativex = packet.chunk.x - lastchunkx + loadrange;
		int relativey = packet.chunk.y - lastchunky + loadrange;
		
		//if the chunk coords are out of range, stop
		if(relativex < 0 || relativey < 0 || relativex >= loadrange * 2 || relativey >= loadrange * 2){
			return;
		}
		
		for(int x = 0; x < chunksize; x ++){
			for(int y = 0; y < chunksize; y ++){
				packet.chunk.tiles[x][y].x = x + packet.chunk.worldX();
				packet.chunk.tiles[x][y].y = y + packet.chunk.worldY();
			}
		}
		
		chunks[relativex][relativey] = packet.chunk;
	}

	public void sendChunkRequest(){
		for(int x = 0;x < loadrange * 2;x ++){
			for(int y = 0;y < loadrange * 2;y ++){
				if(chunks[x][y] == null){
					ChunkRequestPacket packet = new ChunkRequestPacket();
					packet.x = lastchunkx + x - loadrange;
					packet.y = lastchunky + y - loadrange;
					Net.send(packet);
				}
			}
		}
	}

	public synchronized ChunkPacket createChunkPacket(ChunkRequestPacket request){
		ChunkPacket packet = new ChunkPacket();
		packet.chunk = file.getChunk(request.x, request.y);
		return packet;
	}

	public boolean positionSolid(float x, float y){
		Tile tile = getWorldTile(x, y);
		Material block = tile.wall();
		Material tilem = tile.topFloor();
		return (block.solid()) && block.getHitbox(tile(x), tile(y), Rectangle.tmp).contains(x, y) 
			|| (tilem.solid() && tilem.getHitbox(tile(x), tile(y), Rectangle.tmp).contains(x, y));
	}

	public boolean blockSolid(int x, int y){
		Tile tile = getTile(x, y);
		return tile == null || (tile.solid() && tile.wall() instanceof Wall);
	}

	public boolean isAccesible(int x, int y){
		return !blockSolid(x - 1, y) || !blockSolid(x + 1, y) || !blockSolid(x, y - 1) || !blockSolid(x, y + 1);
	}

	public boolean blends(int x, int y, Material material){
		return !isType(x, y + 1, material) || !isType(x, y - 1, material) || !isType(x + 1, y, material) || !isType(x - 1, y, material);
	}

	public boolean isType(int x, int y, Material material){
		if( !inClientBounds(x, y)){
			return true;
		}
		return getTile(x, y).wall() == material || getTile(x, y).topFloor() == material;
	}

	public GridPoint2 search(Material material, int x, int y, int range){
		float nearest = Float.MAX_VALUE;
		for(int cx = -range;cx <= range;cx ++){
			for(int cy = -range;cy <= range;cy ++){
				int worldx = x + cx;
				int worldy = y + cy;
				if(getTile(worldx, worldy).wall() == material || getTile(worldx, worldy).topFloor() == material){
					float dist = Vector2.dst(x, y, worldx, worldy);
					if(dist < nearest){
						point.set(worldx, worldy);
						nearest = dist;
						return point;
					}
				}
			}
		}
		if(nearest > 0) return point;

		return null;
	}

	public boolean inClientBounds(int x, int y){
		if(Net.server()) return true;
		
		int tx = tile(Core.camera.position.x);
		int ty = tile(Core.camera.position.y);
		if(Math.abs(tx - x) >= loadrange * chunksize - 1 || Math.abs(ty - y) >= loadrange * chunksize - 1) return false;
		int ax = x / chunksize - tx / chunksize + loadrange;
		int ay = y / chunksize - ty / chunksize + loadrange;
		if( !inBounds(ax, ay, chunks)){ 
			return false;
		}
		if(getRelativeChunk(x, y) == null) return false;
		return true;
	}
	
	/**Clientside only.*/
	public Chunk getRelativeChunk(int x, int y){
		if(x < -1) x ++;
		if(y < -1) y ++;
		int ax = nint((float)x / chunksize) - lastchunkx + loadrange;
		int ay = nint((float)y / chunksize) - lastchunky + loadrange;
		if(!inBounds(ax, ay, chunks)) return null;
		return chunks[ax][ay];
	}

	public void updateTile(Tile tile){
		updated = true;
		tile.changeEvent();
		Net.sendRange(new TileUpdatePacket(tile.x, tile.y, tile), world(tile.x), world(tile.y),
				tilesize*chunksize*(1+loadrange), Mode.TCP);
	}

	public void updateLater(int x, int y){
		updated = true;
		getTile(x, y).changeEvent();
		Timers.run(2f, ()->{
			Net.send(new TileUpdatePacket(x, y, getTile(x, y)));
		});
	}

	public Tile getTile(int x, int y){
		if( !Net.server()){
			Chunk chunk = getRelativeChunk(x, y);
			return chunk == null ? null : chunk.getWorldTile(x, y);
		}
		int cx = (x < -1 ? x + 1 : x) / chunksize, cy = (y < -1 ? y + 1 : y) / chunksize;
		if(x < 0) cx --;
		if(y < 0) cy --;
		return file.getChunk(cx, cy).getWorldTile(x, y);
	}
	
	public Tile getTile(GridPoint2 point){
		return getTile(point.x, point.y);
	}
	
	public Tile getTile(long c){
		int x = (int)(c >> 32);
		int y = (int)c;
		return getTile(x,y);
	}

	public Tile getWorldTile(float fx, float fy){
		int x = tile(fx);
		int y = tile(fy);
		return getTile(x, y);
	}
	
	public void setTile(int x, int y, Tile tile){
		if( !Net.server()){
			getRelativeChunk(x, y).setWorldTile(x, y, tile);
			return;
		}
		int cx = x / chunksize, cy = y / chunksize;
		file.getChunk(cx, cy).setWorldTile(x, y, tile);
	}
	
	/**Returns a chunk based on tile coords.*/
	public Chunk getChunkFor(int x, int y){
		int cx = x / chunksize, cy = y / chunksize;
		return file.getChunk(cx, cy);
	}

	public int toChunkCoords(int a){
		return (a / chunksize);
	}

	public int toChunkCoords(float worldpos){
		int i = tile(worldpos) / chunksize;
		return i;
	}

	public boolean updated(){
		return updated;
	}

	public static int tile(float i){
		return nint(i/tilesize + 0.5f);
	}
	
	public static float world(int i){
		return tilesize * i;
	}
	
	static int nint(float b){
		return b < 0 ? (int)(b-1) : (int)b;
	}
	
	static long getLong(int x, int y){
		return (long)x << 32 | y & 0xFFFFFFFFL;
	}
	
	static int getX(long l){
		return(int)(l >> 32);
	}
	
	static int getY(long l){
		return (int)l;
	}
	
	//TODO make this work with transparency instead of specific types
	public static boolean isPlaceable(Material material, Tile tile){
		
		if(!material.isLayer(MaterialLayer.floor)){
			return tile.isWallEmpty();
		}else{
			return tile.topFloor() != material && !tile.wall().solid() && tile.canAddTile();
		}		
	}
}
