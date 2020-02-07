package io.anuke.koru.entities.types;

import com.badlogic.gdx.utils.Align;

import io.anuke.koru.Koru;
import io.anuke.koru.traits.ChildTrait;
import io.anuke.koru.traits.TextTrait;
import io.anuke.koru.ucore.core.Draw;
import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.ecs.TraitList;
import io.anuke.koru.ucore.ecs.extend.traits.*;

public class DamageIndicator extends Prototype{

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(), 
			new FacetTrait((trait, spark)->{
				trait.draw(p->{
					ChildTrait child = spark.get(ChildTrait.class);
					Spark parent = Koru.basis.getSpark(child.parent);
					if(parent == null) return;
					
					p.layer = parent.pos().y-1;
					
					Draw.tcolor(spark.life().ifract());
					Draw.text(spark.get(TextTrait.class).text, parent.pos().x, parent.pos().y + parent.get(ColliderTrait.class).height*1.5f
							+ spark.life().life/6f, Align.center);
					Draw.tcolor();
				});
			}),
			new ChildTrait(), 
			new TextTrait(), 
			new LifetimeTrait(20)
		);
	}

}
