package executables.clients;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Sample 4 - how to trigger repeating actions from the main event loop. In this example, you use the loop to make the player character rotate continuously.
 */
public class HelloLoop extends SimpleApplication {

	public static void main(String[] args) {
		HelloLoop app = new HelloLoop();
		app.start();
	}

	protected Geometry player, player2;

	@Override
	public void simpleInitApp() {
		/** this blue box is our player character */
		Box b = new Box(1, 1, 1);
		player = new Geometry("blue cube", b);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		player.setMaterial(mat);
		rootNode.attachChild(player);

		Box b2 = new Box(1, 1, 1);
		player2 = new Geometry("blue cube", b2);
		player2.setLocalTranslation(3, 0, 0);
		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setColor("Color", ColorRGBA.Cyan);
		player2.setMaterial(mat2);
		rootNode.attachChild(player2);
	}

	/* Use the main event loop to trigger repeating actions. */
	@Override
	public void simpleUpdate(float tpf) {
		// make the player rotate:
		player.rotate(0, 2 * tpf, 0);
		player2.rotate(0, 8 * tpf, 0);
	}
}
