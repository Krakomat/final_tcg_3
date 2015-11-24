package executables.clients;
 
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
 
/** Sample 3 - how to load an OBJ model, and OgreXML model, 
 * a material/texture, or text. */
public class HelloAssets extends SimpleApplication {
 
    public static void main(String[] args) {
        HelloAssets app = new HelloAssets();
        app.start();
    }
    Spatial ninja;

    @Override
    public void simpleInitApp() { 
        // Load a model from test_data (OgreXML + material + texture)
        ninja = assetManager.loadModel("assets/models/coin/coin.scene");
        ninja.scale(1.05f, 1.05f, 1.05f);
        ninja.rotate(180.0f, 0.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, -0.0f, -0.0f);
        rootNode.attachChild(ninja);
        // You must add a light to make the model visible
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(al); 
    }
    public void simpleUpdate(float tpf){
    	ninja.rotate(-1*tpf, 0, 0);
    }
}