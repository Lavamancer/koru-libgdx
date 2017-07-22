package io.anuke.koru.entities.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.Prototypes;
import io.anuke.koru.modules.World;
import io.anuke.koru.network.IServer;
import io.anuke.koru.traits.MaterialTrait;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.world.materials.MaterialTypes;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.LifetimeTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.renderables.Renderable;
import io.anuke.ucore.renderables.Sorter;
import io.anuke.ucore.renderables.SpriteRenderable;
import io.anuke.ucore.util.Mathf;

public class BlockAnimation extends Prototype{
	//rotation speed
	final static float rspeed = 0.7f;

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(), 
			new RenderableTrait((trait, spark)->{
				final float rspeed = Koru.control.player.pos().x < spark.pos().x ? 
					-BlockAnimation.rspeed : BlockAnimation.rspeed;
				
				if(spark.get(MaterialTrait.class).matid == 0){
					throw new RuntimeException("No material specified in the data! Did you set the renderer material?");
				}
				
				Material material = (Material)spark.get(MaterialTrait.class).material();
				
				int x = (int)(spark.pos().x/World.tilesize);
				int y = (int)(spark.pos().y/World.tilesize);
				
				material.getType().draw(trait.list, material, Koru.module(World.class).getWorldTile(x, y), x, y);
				
				if(material.getType() == MaterialTypes.tree){
					
					SpriteRenderable bot = trait.list.renderables.peek().sprite();
					
					int theight = bot.sprite.getRegionHeight()/9;
					
					TextureRegion region = bot.sprite;
					
					TextureRegion tr = new TextureRegion(region);
					
					tr.setRegionHeight(region.getRegionHeight() - theight);
					
					region.setRegionY(region.getRegionY() + region.getRegionHeight()-theight);
					region.setRegionHeight(theight);
					
					bot.sprite.setSize(region.getRegionWidth(), theight);
					
					SpriteRenderable top = new SpriteRenderable(tr);
					top.set(bot.sprite.getX(), bot.sprite.getY() + theight).layer(bot.getLayer()).sort(Sorter.object);
					top.sprite.setOrigin(top.sprite.getWidth()/2, 0);
					top.add(trait.list);
				}
				
				trait.draw(d->{
					for(Renderable r : trait.list.renderables)
						r.sprite().alpha(spark.life().ifract());
					
					if(spark.get(MaterialTrait.class).material().getType() == MaterialTypes.tree)
						trait.list.renderables.get(2).sprite().sprite.rotate(rspeed*Mathf.delta());
				});
			}),
			new LifetimeTrait(50),
			new MaterialTrait()
		);
	}
	
	public static void create(Material material, float x, float y){
		Spark entity = new Spark(Prototypes.blockAnimation);
		entity.pos().set(x, y);
		entity.get(MaterialTrait.class).matid = material.id();
		IServer.instance().sendSpark(entity);
	}

}
