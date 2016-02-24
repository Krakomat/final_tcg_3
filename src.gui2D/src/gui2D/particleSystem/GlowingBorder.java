package src.gui2D.particleSystem;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;

import gui2d.GUI2D;
import model.database.Database;

public class GlowingBorder extends ParticleEmitter {
	public GlowingBorder(float xPos, float yPos, float zPos, float width, float height, ColorRGBA color, BlendMode blendMode) {
		super("Emitter", ParticleMesh.Type.Triangle, 30);
		Material mat_red = new Material(GUI2D.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
		mat_red.setTexture("Texture", GUI2D.getInstance().getAssetManager().loadTexture(Database.getAssetKey("Particle")));
		mat_red.getAdditionalRenderState().setBlendMode(blendMode);
		this.setMaterial(mat_red);
		this.setImagesX(2);
		this.setImagesY(2); // 2x2 texture animation
		this.setEndColor(new ColorRGBA(color.r, color.g, color.b, 1.0f)); // red
		this.setStartColor(new ColorRGBA(color.r, color.g, color.b, 1.0f)); // yellow
		this.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1, 0));
		this.setStartSize(1.0f);
		this.setEndSize(0.6f);
		this.setGravity(0, -1, 0);
		this.setLowLife(1f);
		this.setHighLife(1.5f);
		this.getParticleInfluencer().setVelocityVariation(0.4f);

		this.setLocalTranslation(xPos, yPos, zPos); // doesn't have to be this just pick somewhere on screen
		this.setLocalScale(width, height, 1); // scale it to your need so it's not few pixels wide
		this.setInWorldSpace(false); // use this or it will ignore previous line
		this.setQueueBucket(RenderQueue.Bucket.Gui); // that's the trick!!
		this.setFaceNormal(Vector3f.UNIT_Z); // that's the second trick!!
	}
}
