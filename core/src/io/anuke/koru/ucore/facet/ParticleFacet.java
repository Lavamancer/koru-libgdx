package io.anuke.koru.ucore.facet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import io.anuke.koru.ucore.core.Core;

public class ParticleFacet extends Facet{
	public PooledEffect effect;
	
	public ParticleFacet(PooledEffect effect){
		this.effect = effect;
		sort(Sorter.object);
	}
	
	@Override
	public void draw(){
		effect.draw(Core.batch, Gdx.graphics.getDeltaTime());
	}

	@Override
	public Facet set(float x, float y){
		effect.getEmitters().get(0).setPosition(x, y);
		return this;
	}

	@Override
	public float getLayer(){
		return effect.getEmitters().get(0).getY();
	}

	@Override
	public void reset(){
		effect = null;
	}
}
