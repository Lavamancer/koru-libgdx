package io.anuke.koru.entities.types;

import io.anuke.koru.network.SyncType;
import io.anuke.koru.traits.SyncTrait;
import io.anuke.koru.ucore.core.Draw;
import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.TraitList;
import io.anuke.koru.ucore.ecs.extend.traits.*;

public class TestEntity extends Prototype{

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(),
			//TODO
			//new RenderComponent(new EnemyRenderer()), 
			new FacetTrait((trait, spark)->{
				trait.draw(d->{
					d.layer = spark.pos().y;
					
					Draw.grect("player", spark.pos().x, spark.pos().y);
				});
				
				trait.drawShadow(spark, 8, 0);
				
			}),
			new ColliderTrait(7, 7),
			new SyncTrait(SyncType.physics),
			new HealthTrait()
		);
	}

}
