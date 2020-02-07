package io.anuke.koru.ucore.ecs.extend.traits;

import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.Spark;
import io.anuke.koru.ucore.ecs.Trait;
import io.anuke.koru.ucore.ecs.extend.Events.Damaged;
import io.anuke.koru.ucore.ecs.extend.Events.Death;
import io.anuke.koru.ucore.util.Mathf;

public class HealthTrait extends Trait{
	public int health, maxhealth;
	public boolean dead = false;
	
	public HealthTrait(){
		this(100);
	}
	
	public HealthTrait(int maxhealth){
		health = maxhealth;
		this.maxhealth = maxhealth;
	}
	
	@Override
	public void added(Spark spark){
		dead = false;
	}
	
	@Override
	public void removed(Spark spark){
		dead = false;
	}
	
	@Override
	public void registerEvents(Prototype type){
		type.traitEvent(Damaged.class, (spark, source, damage)->{
			HealthTrait htrait = spark.get(HealthTrait.class);
			
			htrait.health -= damage;
			
			if(htrait.health < 0 && !htrait.dead){
				type.callEvent(Death.class, spark);
				htrait.dead = true;
			}
		});
	}
	
	public void heal(){
		health = maxhealth;
	}
	
	public void clampHealth(){
		health = Mathf.clamp(health, 0, maxhealth);
	}
	
	/**Returns health/maxhealth.*/
	public float healthfrac(){
		return (float)health/maxhealth;
	}
}
