package io.anuke.koru.ucore.facet;

import com.badlogic.gdx.math.MathUtils;

import io.anuke.koru.ucore.core.Core;
import io.anuke.koru.ucore.core.Graphics;

public abstract class FacetLayer{
	public final String name;
	public final float layer;
	public final int bind;

	public FacetLayer(String name, float layer, int bind){
		this.name = name;
		this.layer = layer;
		this.bind = bind;
		
		Graphics.addSurface(name, Core.cameraScale, bind);
	}

	public void end(){
		Graphics.surface();
	}

	public void begin(){
		Graphics.surface(name);
	}
	
	public boolean acceptFacet(Facet facet){
		return layerEquals(facet.getLayer());
	}

	public boolean layerEquals(float f){
		return MathUtils.isEqual(f, layer, 1f);
	}
}
