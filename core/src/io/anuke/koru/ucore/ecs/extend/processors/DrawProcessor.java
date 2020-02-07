package io.anuke.koru.ucore.ecs.extend.processors;

import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.ecs.TraitProcessor;
import io.anuke.koru.ucore.ecs.extend.traits.DrawTrait;

public class DrawProcessor extends TraitProcessor{
	
	public DrawProcessor(){
		super(DrawTrait.class);
	}

	@Override
	public void update(Spark spark){
		spark.get(DrawTrait.class).drawer.accept(spark);
	}
}
