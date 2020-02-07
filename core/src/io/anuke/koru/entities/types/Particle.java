package io.anuke.koru.entities.types;

import io.anuke.koru.traits.ParticleTrait;
import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.TraitList;
import io.anuke.koru.ucore.ecs.extend.traits.PosTrait;

public class Particle extends Prototype{

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(), 
			//TODO
			//new RenderComponent(new ParticleRenderer()), 
			new ParticleTrait()
		);
	}

}
