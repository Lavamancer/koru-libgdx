package io.anuke.koru.traits;

import io.anuke.koru.network.syncing.SyncData.Synced;
import io.anuke.koru.ucore.ecs.Trait;

@Synced
public class ConnectionTrait extends Trait{
	public boolean local; //whether this is the local player
	public transient int connectionID;
	public String name;
}
