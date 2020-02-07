package io.anuke.koru.ucore.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;

import io.anuke.koru.ucore.function.Callable;
import io.anuke.koru.ucore.function.DelayRun;
import io.anuke.koru.ucore.function.Supplier;

public class Timers{
	private static float time;
	private static ObjectMap<Integer, Float> timers = new ObjectMap<>();
	private static DelayedRemovalArray<DelayRun> runs = new  DelayedRemovalArray<>();
	private static long lastMark = 0;
	private static Supplier<Float> deltaimpl = ()->Math.min(Gdx.graphics.getDeltaTime()*60f, 3f);
	
	public static void run(float delay, Callable r){
		DelayRun run = Pools.obtain(DelayRun.class);
		run.finish = r;
		run.delay = delay;
		runs.add(run);
	}
	
	public static void runTask(float delay, Callable r){
		Timer.schedule(new Task(){
			@Override
			public void run(){
				r.run();
			}
		}, delay / 60f);
	}
	
	public static void runFor(float duration, Callable r){
		DelayRun run = Pools.obtain(DelayRun.class);
		run.run = r;
		run.delay = duration;
		runs.add(run);
	}
	
	public static void runFor(float duration, Callable r, Callable finish){
		DelayRun run = Pools.obtain(DelayRun.class);
		run.run = r;
		run.delay = duration;
		run.finish = finish;
		runs.add(run);
	}
	
	public static void reset(Object o, String label, float duration){
		timers.put(hash(o, label), time - duration);
	}
	
	public static void clear(){
		runs.clear();
		timers.clear();
	}
	
	public static float getTime(Object object, String label){
		return time() - timers.get(hash(object, label), 0f);
	}
	
	public static boolean get(String label, float frames){
		return get(hash(label), frames);
	}
	
	public static boolean get(Object object, String label, float frames){
		return get(hash(object, label), frames);
	}
	
	public static boolean get(int hash, float frames){
		if(timers.containsKey(hash)){
			float out = timers.get(hash);
			if(time - out > frames){
				timers.put(hash, time);
				return true;
			}else{
				return false;
			}
		}else{
			timers.put(hash, time);
			return true;
		}
	}
	
	public static float time(){
		return time;
	}
	
	public static void mark(){
		lastMark = TimeUtils.millis();
	}
	
	public static long elapsed(){
		return TimeUtils.timeSinceMillis(lastMark);
	}
	
	/**Use normal delta time (e. g. gdx delta * 60)*/
	public static void update(){
		float delta = delta();
		time += delta;
		
		runs.begin();
		
		for(DelayRun run : runs){
			run.delay -= delta;
			
			if(run.run != null)
				run.run.run();
			
			if(run.delay <= 0){
				if(run.finish != null)
					run.finish.run();
				runs.removeValue(run, true);
				Pools.free(run);
			}
		}
		
		runs.end();
	}

	public static float delta(){
		return deltaimpl.get();
	}

	public static void setDeltaProvider(Supplier<Float> impl){
		deltaimpl = impl;
	}
	
	private static int hash(Object object, String label){
		return hash(label) + object.hashCode();
	}
	
	private static int hash(String label){
		return label.hashCode();
	}
	
	static void dispose(){
		timers.clear();
		runs.clear();
	}
}
