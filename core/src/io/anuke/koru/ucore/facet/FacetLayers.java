package io.anuke.koru.ucore.facet;

import io.anuke.koru.ucore.core.Draw;
import io.anuke.koru.ucore.core.Graphics;

public class FacetLayers{
	public static final FacetLayer 
	
	shadow = new FacetLayer("shadow", Sorter.shadow, 0){
		
		@Override
		public void end(){
			Draw.color(0,0,0,0.13f);
			Graphics.flushSurface();
			Draw.color();
		}
	}, 
	light = new FacetLayer("light", Sorter.light, 6){

	},
	darkness = new FacetLayer("darkness", Sorter.dark, 0){
		
	};
}
