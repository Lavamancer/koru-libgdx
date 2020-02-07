package io.anuke.koru.traits;

import io.anuke.koru.network.syncing.SyncData.Synced;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.ucore.ecs.Trait;

@Synced
public class MaterialTrait extends Trait{
	public int matid;
	
	public Material material(){
		return Material.getMaterial(matid);
	}
}
