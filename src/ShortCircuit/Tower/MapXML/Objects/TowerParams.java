package ShortCircuit.Tower.MapXML.Objects;

import ShortCircuit.Tower.Controls.TowerControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Connor Rice
 */
public class TowerParams {
    
    private Vector3f vec;
    private boolean starter;
    private int index;
    private String type;
    private Vector3f scale;
    private Spatial towerSpatial;
    private TowerControl towerControl;
    
    public TowerParams(Vector3f vec,  boolean starter) {
        this.vec = vec;
        this.starter = starter;
        type = "TowerUnbuilt";
    }
    
    public void setScale(Vector3f s) {
        scale = s;
        getSpatial().setLocalScale(scale);
    }
    
    public void setIndex(int i) {
        index = i;
        getSpatial().setUserData("Index", i);
    }
    
    public void setType(String t) {
        type = t;
    }
    
    public Vector3f getTowerVec() {
        return vec;
    }
    
    public boolean getIsStarter() {
        return starter;
    }
    
    public void setSpatial(Spatial s) {
        this.towerSpatial = s;
    }
    
    public Spatial getSpatial() {
        return towerSpatial;
    }
       
    public void setControl(TowerControl c) {
        this.towerControl = c;
    }
 
    
    public TowerControl getControl() {
        return towerControl;
    }
    
    public String getType() {
        return type;
    }
    
    public int getIndex() {
        return index;
    }

    
}
