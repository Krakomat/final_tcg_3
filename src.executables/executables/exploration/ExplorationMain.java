package executables.exploration;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;

/** Sample 11 - how to create fire, water, and explosion effects. */
public class ExplorationMain extends SimpleApplication {

	public static void main(String[] args) {
		ExplorationMain app = new ExplorationMain();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
		Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		// Effects/Explosion/Debris.png (3:3)
		// Effects/Explosion/flame.png (2:2)
		// Effects/Explosion/shockwave.png (1:1)
		// Effects/Explosion/smoketrail.png (1:3)
		// Effects/Smoke/Smoke.png (1:15)
		mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
		fire.setMaterial(mat_red);
		fire.setImagesX(1);
		fire.setImagesY(1); // 2x2 texture animation
		fire.setStartColor(new ColorRGBA(0f, 1f, 1f, 1.0f)); // yellow
		fire.setEndColor(new ColorRGBA(0f, 0f, 1f, 0.5f)); // red
		fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
		fire.setStartSize(1.5f);
		fire.setEndSize(0.1f);
		fire.setGravity(0, 0, 0);
		fire.setLowLife(1f);
		fire.setHighLife(3f);
		fire.getParticleInfluencer().setVelocityVariation(1.0f);
		fire.setLocalTranslation(800, 400, 0); // doesn't have to be this just pick somewhere on screen
		fire.setLocalScale(50, 50, 1); // scale it to your need so it's not few pixels wide
		fire.setInWorldSpace(false); // use this or it will ignore previous line
		fire.setQueueBucket(RenderQueue.Bucket.Gui); // that's the trick!!
		fire.setFaceNormal(Vector3f.UNIT_Z); // that's the second trick!!
		guiNode.attachChild(fire);

		ParticleEmitter fire2 = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
		Material mat_red2 = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		// Effects/Explosion/Debris.png (3:3)
		// Effects/Explosion/flame.png (2:2)
		// Effects/Explosion/shockwave.png (1:1)
		// Effects/Explosion/smoketrail.png (1:3)
		// Effects/Smoke/Smoke.png (1:15)
		mat_red2.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
		fire2.setMaterial(mat_red2);
		fire2.setImagesX(1);
		fire2.setImagesY(1); // 2x2 texture animation
		fire2.setStartColor(new ColorRGBA(0f, 1f, 1f, 1.0f)); // yellow
		fire2.setEndColor(new ColorRGBA(0f, 1f, 1f, 0.5f)); // red
		fire2.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
		fire2.setStartSize(1.5f);
		fire2.setEndSize(1.1f);
		fire2.setGravity(-6.0f, 0, 0);
		fire2.setLowLife(2f);
		fire2.setHighLife(2f);
		fire2.getParticleInfluencer().setVelocityVariation(0.5f);
		fire2.setLocalTranslation(300, 400, 0); // doesn't have to be this just pick somewhere on screen
		fire2.setLocalScale(50, 50, 1); // scale it to your need so it's not few pixels wide
		fire2.setInWorldSpace(false); // use this or it will ignore previous line
		fire2.setQueueBucket(RenderQueue.Bucket.Gui); // that's the trick!!
		fire2.setFaceNormal(Vector3f.UNIT_Z); // that's the second trick!!
		guiNode.attachChild(fire2);

		// ParticleEmitter debris = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
		// Material debris_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		// debris_mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
		// debris.setMaterial(debris_mat);
		// debris.setImagesX(3);
		// debris.setImagesY(3); // 3x3 texture animation
		// debris.setStartColor(new ColorRGBA(1f, 1f, 0f, 1.0f)); // yellow
		// debris.setEndColor(new ColorRGBA(1f, 0f, 0f, 0.5f)); // red
		// debris.setRotateSpeed(4);
		// debris.setSelectRandomImage(true);
		// debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
		// debris.setStartColor(ColorRGBA.White);
		// debris.setGravity(0, 6, 0);
		// debris.getParticleInfluencer().setVelocityVariation(.60f);
		// debris.setLocalTranslation(600, 400, 0); // doesn't have to be this just pick somewhere on screen
		// debris.setLocalScale(50, 50, 1); // scale it to your need so it's not few pixels wide
		// debris.setInWorldSpace(false); // use this or it will ignore previous line
		// debris.setQueueBucket(RenderQueue.Bucket.Gui); // that's the trick!!
		// debris.setFaceNormal(Vector3f.UNIT_Z); // that's the second trick!!
		// guiNode.attachChild(debris);
		// debris.emitAllParticles();
	}
}