package io.anuke.koru.ucore.ecs.extend.traits;

import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.ecs.Trait;
import io.anuke.koru.ucore.function.Consumer;

public class DrawTrait extends Trait{
	public Consumer<Spark> drawer;
	
	public DrawTrait(Consumer<Spark> drawer){
		this.drawer = drawer;
	}
	
	//no-arg constructor for things like Kryo/JSON
	private DrawTrait(){}
}
