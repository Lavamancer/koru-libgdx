package io.anuke.koru.ucore.ecs.extend.traits;

import io.anuke.koru.ucore.ecs.Prototype;
import io.anuke.koru.ucore.ecs.Trait;
import io.anuke.koru.ucore.ecs.extend.Events.Collision;
import io.anuke.koru.ucore.ecs.extend.Events.Damaged;

public class ContactDamageTrait extends Trait{
	public int damage;
	
	public ContactDamageTrait(){
		
	}

	public ContactDamageTrait(int damage){
		this.damage = damage;
	}
	
	@Override
	public void registerEvents(Prototype type){
		
		type.traitEvent(Collision.class, (spark, other)->{
			
			if(other.has(HealthTrait.class)){
				int damage = spark.get(ContactDamageTrait.class).damage;
				
				other.getType().callEvent(Damaged.class, other, spark, damage);
			}
		});
	}
	
}
