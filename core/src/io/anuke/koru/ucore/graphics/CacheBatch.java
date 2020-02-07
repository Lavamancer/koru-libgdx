package io.anuke.koru.ucore.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;

import io.anuke.koru.ucore.util.Tmp;

/**A 'batch' that calls Caches.draw() for most operations.
 * mostly unfinished*/
//TODO implementation
public class CacheBatch implements Batch{

	@Override
	public void dispose(){
		stub();
	}

	@Override
	public void begin(){
		stub();
	}

	@Override
	public void end(){
		stub();
	}

	@Override
	public void setColor(Color tint){
		Caches.color(tint);
	}

	@Override
	public void setColor(float r, float g, float b, float a){
		setColor(Tmp.c1.set(r, g, b, a));
	}

	@Override
	public void setColor(float color){
		//TODO probably incorrect color
		Color.abgr8888ToColor(Tmp.c1, color);
		setColor(Tmp.c1);
	}

	@Override
	public Color getColor(){
		return Caches.getColor();
	}

	@Override
	public float getPackedColor(){
		return Caches.getColor().toFloatBits();
	}

	@Override
	public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY){
		stub();
	}

	@Override
	public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY){
		stub();
	}

	@Override
	public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight){
		stub();
	}

	@Override
	public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2){
		stub();
	}

	@Override
	public void draw(Texture texture, float x, float y){
		stub();
	}

	@Override
	public void draw(Texture texture, float x, float y, float width, float height){
		stub();
	}

	@Override
	public void draw(Texture texture, float[] spriteVertices, int offset, int count){
		stub();
	}

	@Override
	public void draw(TextureRegion region, float x, float y){
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float width, float height){
		Caches.draw(region, x, y, width, height);
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation){
		Caches.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise){
		stub();
		draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, false);
	}

	@Override
	public void draw(TextureRegion region, float width, float height, Affine2 transform){
		stub();
	}

	@Override
	public void flush(){
		stub();
	}

	@Override
	public void disableBlending(){
		stub();
	}

	@Override
	public void enableBlending(){
		stub();
	}

	@Override
	public void setBlendFunction(int srcFunc, int dstFunc){
		stub();
	}

	@Override
	public int getBlendSrcFunc(){
		stub();
		return 0;
	}

	@Override
	public int getBlendDstFunc(){
		stub();
		return 0;
	}

	@Override
	public Matrix4 getProjectionMatrix(){
		stub();
		return null;
	}

	@Override
	public Matrix4 getTransformMatrix(){
		stub();
		return null;
	}

	@Override
	public void setProjectionMatrix(Matrix4 projection){
		stub();
	}

	@Override
	public void setTransformMatrix(Matrix4 transform){
		stub();
	}

	@Override
	public void setShader(ShaderProgram shader){
		stub();
	}

	@Override
	public ShaderProgram getShader(){
		stub();
		return null;
	}

	@Override
	public boolean isBlendingEnabled(){
		stub();
		return false;
	}

	@Override
	public boolean isDrawing(){
		stub();
		return false;
	}
	
	private void stub(){
		throw new IllegalArgumentException("Stub method!");
	}

}
