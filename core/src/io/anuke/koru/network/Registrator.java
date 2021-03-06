package io.anuke.koru.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.anuke.koru.entities.types.*;
import io.anuke.koru.input.InputType;
import io.anuke.koru.items.Item;
import io.anuke.koru.items.ItemData;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.items.impl.Items;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.network.syncing.SyncData;
import io.anuke.koru.network.syncing.SyncData.Synced;
import io.anuke.koru.traits.*;
import io.anuke.koru.traits.DirectionTrait.Direction;
import io.anuke.koru.ui.Menu;
import io.anuke.koru.world.Chunk;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.ecs.Trait;
import io.anuke.koru.ucore.ecs.extend.traits.LifetimeTrait;
import io.anuke.koru.ucore.ecs.extend.traits.PosTrait;

public class Registrator{
	private static ObjectSet<Class<? extends Trait>> synced = ObjectSet.with(
		PosTrait.class,
		ChildTrait.class
	);
	
	private static ObjectSet<Class<? extends Trait>> notSynced = ObjectSet.with();
	
	public static void register(Kryo k){
		register(k, 
			ConnectPacket.class,
			PositionPacket.class,
			DataPacket.class,
			WorldUpdatePacket.class,
			SparkRemovePacket.class,
			ChunkRequestPacket.class,
			ChunkPacket.class,
			InputPacket.class,
			TileUpdatePacket.class,
			BlockInputPacket.class,
			StoreItemPacket.class,
			ChatPacket.class,
			BitmapDataPacket.class,
			BitmapDataPacket.Header.class,
			MaterialRequestPacket.class,
			InventoryUpdatePacket.class,
			InventoryClickPacket.class,
			SlotChangePacket.class,
			RecipeSelectPacket.class,
			SparkRequestPacket.class,
			BlockQueryPacket.class,
			BatchTileUpdatePacket.class,
			
			TextTrait.class,
			InventoryTrait.class,
			ParticleTrait.class,
			MaterialTrait.class,
			ItemTrait.class,
			DirectionTrait.class,
			PosTrait.class,
			LifetimeTrait.class,
			ConnectionTrait.class,
			EffectTrait.class,
	
			SyncData.class,
			
			Direction.class,
			
			ItemDrop.class,
			Particle.class,
			Projectile.class,
			TestEntity.class,
			DamageIndicator.class,
			BlockAnimation.class,
			
			Chunk.class,
			Tile.class,
			Tile[].class,
			Tile[][].class,
			Tile[][][].class,
			ItemStack[].class,
			ItemStack[][].class,
			long[].class,
			Items.class,
			ItemData.class,
			
			InputType.class,
			SyncType.class,
			Object[].class,
			Bits.class,
			Color.class,
			Vector2.class,
			ArrayList.class,
			ObjectMap.class,
			ConcurrentHashMap.class,
			ObjectMap.Keys.class,
			HashMap.class,
			byte[].class,
			int[].class,
			Class.class
		);
		
		k.register(Spark.class, new SparkSerializer());
		k.register(MenuOpenPacket.class, new MenuPacketSerializer());
		
		k.register(Material.class, new MaterialsSerializer());
		k.register(ItemStack.class, new ItemStackSerializer());
		k.register(Tile.class, new TileSerializer());
	}
	
	private static void register(Kryo k, Class<?>...classes){
		for(Class<?> c : classes){
			k.register(c);
		}
	}
	
	public static class ItemStackSerializer extends Serializer<ItemStack>{
		@Override
		public ItemStack read(Kryo k, Input i, Class<ItemStack> c){
			ItemStack stack = new ItemStack();
			stack.amount = k.readObject(i, Integer.class);
			stack.item = Item.getItem(k.readObject(i, Integer.class));
			return stack;
		}

		@Override
		public void write(Kryo k, Output o, ItemStack t){
			k.writeObject(o, t.amount);
			k.writeObject(o, t.item.id());
		}
	}
	
	public static class TileSerializer extends Serializer<Tile>{
		@Override
		public Tile read(Kryo k, Input i, Class<Tile> c){
			Tile tile = Tile.unloadedTile();
			
			tile.floors = new short[]{k.readObject(i, short.class)};
			tile.wallid = k.readObject(i, short.class);
			tile.light = k.readObject(i, byte.class);
			tile.top = k.readObject(i, byte.class);
			
			return tile;
		}

		@Override
		public void write(Kryo k, Output o, Tile t){
			k.writeObject(o, t.tileid());
			k.writeObject(o, t.wallid);
			k.writeObject(o, t.light);
			k.writeObject(o, t.top);
		}
	}
	
	public static class MenuPacketSerializer extends Serializer<MenuOpenPacket>{

		@Override
		public void write(Kryo kryo, Output output, MenuOpenPacket object){
			kryo.writeObject(output, object.type.getName());
		}

		@Override
		public MenuOpenPacket read(Kryo kryo, Input input, Class<MenuOpenPacket> type){
			MenuOpenPacket packet = new MenuOpenPacket();
			try{
				packet.type = (Class<? extends Menu>) Class.forName(kryo.readObject(input, String.class));
				return packet;
			}catch (Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class MaterialsSerializer extends Serializer<Material>{
		@Override
		public Material read(Kryo k, Input i, Class<Material> c){
			return (Material)Material.getMaterial(k.readObject(i, int.class));
		}

		@Override
		public void write(Kryo k, Output o, Material m){
			k.writeObject(o, m.id());
		}
	}
	
	/**
	 * Format:
	 * 1) type ID
	 * 2) entity ID
	 * 3) all the components, in no particular order
	 */
	static class SparkSerializer extends Serializer<Spark>{

		@Override
		public Spark read(Kryo k, Input input, Class<Spark> c){
			Array<Trait> traits = new Array<Trait>();
			int typeid = k.readObject(input, int.class);
			int id = k.readObject(input, int.class);
			
			Prototype type = Prototype.getAllTypes().get(typeid);
			
			Array<Trait> typetraits = type.traits().asArray();
			
			int serialized = 0;
			
			for(Trait trait : typetraits){
				if(!serialize(trait)){
					traits.add(trait);
				}else{
					serialized ++;
				}
			}
			
			for(int i = 0; i < serialized; i ++){
				Trait trait = (Trait)k.readClassAndObject(input);
				if(trait != null)
					traits.add(trait);
			}
			
			Spark spark = Spark.createCustom(Prototype.getAllTypes().get(typeid), traits);
			spark.resetID(id);
			
			return spark;
		}

		@Override
		public void write(Kryo k, Output output, Spark entity){
			k.writeObject(output, entity.getType().getTypeID());
			k.writeObject(output, entity.getID());
			
			for(Trait trait : entity.getTraits()){
				if(serialize(trait)){
					k.writeClassAndObject(output, trait);
				}
			}
		}
		
		//TODO!!!
		boolean serialize(Trait trait){
			if(notSynced.contains(trait.getClass())){
				return false;
			}else{
				if(!synced.contains(trait.getClass())){
					Annotation a = ClassReflection.getAnnotation(trait.getClass(), Synced.class);
					if(a == null){
						notSynced.add(trait.getClass());
						return false;
					}else{
						synced.add(trait.getClass());
						return true;
					}
				}else{
					return true;
				}
			}
		}

	}
}
