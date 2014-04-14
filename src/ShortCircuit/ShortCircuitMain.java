package ShortCircuit;

import ShortCircuit.GUI.StartGUI;
import ShortCircuit.Tower.MainState.TowerMainState;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ShortCircuit: A tower defense game
 *
 * @author Connor Rice
 */
public class ShortCircuitMain extends SimpleApplication {
    
    private StartGUI sgui;
    private TowerMainState tms;

    public static void main(String[] args) {
        //Logger.getLogger("").setLevel(Level.OFF);
        ShortCircuitMain app = new ShortCircuitMain();
        AppSettings sets = new AppSettings(true);
        sets.setResolution(1920,1080);
        sets.setFrequency(60);
        sets.setFullscreen(false);
        sets.setVSync(true);
        sets.setFrameRate(60);
        app.setSettings(sets);
        app.setShowSettings(true);
        sets.setSettingsDialogImage("Interface/loading.jpg");
        sets.setFrameRate(60);
        app.setSettings(sets);
        app.start();
    }


    /**
     * Initialize camera, add input mapping, bring up start menu. Also adjust
     * statview/fps
     */
    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);
        flyCam.setRotationSpeed(0.0f);
        flyCam.setZoomSpeed(0.0f);
        setDisplayFps(false);
        setDisplayStatView(false);
        startGUI();
    }
    
    public void startGUI() {
        sgui = new StartGUI();
        stateManager.attach(sgui);
    }
  
    @Override
    public void destroy() {
        super.destroy();
        rootNode.detachAllChildren();
    }
}
