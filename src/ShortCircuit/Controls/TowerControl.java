package ShortCircuit.Controls;

import ShortCircuit.DataStructures.STC;
import ShortCircuit.DataStructures.STCCreepCompare;
import ShortCircuit.States.Game.GraphicsState;
import ShortCircuit.Objects.Game.Charges;
import ShortCircuit.States.Game.FriendlyState;
import com.jme3.audio.AudioNode;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Control for user-controlled towers. TODO: Documentation for TowerParams
 *
 * @author Connor Rice
 */
public class TowerControl extends AbstractControl {

    protected GraphicsState GraphicsState;
    public int[] reachableSpawners;
    public STC<Spatial> reachable;
    public ArrayList<Charges> charges = new ArrayList<Charges>();
    private Vector3f towerloc;
    protected FriendlyState FriendlyState;
    private ScheduledThreadPoolExecutor ex;
    private boolean isActive = false;
    private float searchTimer = .0f;
    private float searchDelay = .2f;
    private int[] allowedSpawners;
    private Comparator<Spatial> cc;
    private Future future;
    private AudioNode emptySound;
    private boolean isGlobbed = false;
    private float beamwidth;

    public TowerControl(FriendlyState _tstate, Vector3f towerloc) {
        FriendlyState = _tstate;
        GraphicsState = FriendlyState.getApp().getStateManager().getState(GraphicsState.class);
        FriendlyState = FriendlyState.getApp().getStateManager().getState(FriendlyState.class);
        cc = new STCCreepCompare(towerloc);
        this.towerloc = towerloc;
        this.ex = FriendlyState.getEx();
        // TODO: Remove emptySound from TowerControl
        emptySound = new AudioNode(FriendlyState.getApp().getAssetManager(), "Audio/emptytower.wav");
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (FriendlyState.isEnabled() && isActive) {
            if (searchTimer > searchDelay) {
                decideShoot();
                reachable = null;
                searchForCreeps();
                searchTimer = 0;
            } else {
                searchTimer += tpf;
            }
        }
    }

    protected void decideShoot() {
        if (!charges.isEmpty()) {
            if (reachable != null) {
                if (!reachable.isEmpty()) {
                    shootCreep();
                }
            }
        } else {
            setTowerEmpty();
        }

    }

    public void globTower() {
        setInactive();
        FriendlyState.getGlobbedTowerList().add(getIndex());
        isGlobbed = true;
    }

    public void setActive() {
        isActive = true;
    }

    public void unglobTower() {
        if (!spatial.getUserData("Type").equals("TowerUnbuilt")) {
            setActive();
        }
        isGlobbed = false;
        if (FriendlyState.getGlobbedTowerList().indexOf(getIndex()) != -1) {
            FriendlyState.getGlobbedTowerList().remove(FriendlyState.getGlobbedTowerList().indexOf(getIndex()));
        }
    }

    public ArrayList<Integer> getGlobbedTowerIndices() {
        return FriendlyState.getGlobbedTowerList();
    }

    public boolean getIsGlobbed() {
        return isGlobbed;
    }

    /*private void excludeCreeps() {
     ArrayList<CreepSpawnerParams> creepSpawners = FriendlyState.getCreepSpawnerList();
     allowedSpawners = new int[creepSpawners.size()];
     for (int i = 0; i < creepSpawners.size(); i++) {
     if (creepSpawners.get(i).getLocalTranslation().distance(towerloc) < 10.0f) {
     allowedSpawners[i] = creepSpawners.get(i).getUserData("Parent");
     }
     }
     }*/
    public int[] getAllowedSpawners() {
        return allowedSpawners;
    }

    private void searchForCreeps() {
        try {
            if (reachable == null && future == null) {
                future = ex.submit(callableCreepFind);
            } else if (future != null) {
                if (future.isDone()) {
                    reachable = (STC<Spatial>) future.get();
                    future = null;
                } else if (future.isCancelled()) {
                    future = null;
                }
            }
        } catch (Exception excpt) {
            System.out.println("TowerControl.searchForCreeps()" + excpt.getLocalizedMessage());
        }
    }
    /**
     * Here we are cloning the entire list of creeps ultimately from GameState.
     * Current goal is to parse this list in a way that fewer offerings are
     * being made.
     *
     * Future goal would be to only build the relevant list of creeps in the
     * first place.
     */
    private Callable<STC<Spatial>> callableCreepFind = new Callable<STC<Spatial>>() {
        public STC<Spatial> call() throws Exception {
            STC<Spatial> reach = new STC<Spatial>(cc);
            ArrayList<Spatial> creepClone = FriendlyState.getApp().enqueue(new Callable<ArrayList<Spatial>>() {
                public ArrayList<Spatial> call() throws Exception {
                    return (ArrayList<Spatial>) FriendlyState.getCreepList().clone();
                }
            }).get();

            for (int i = 0; i < creepClone.size(); i++) {
                if (creepClone.get(i).getLocalTranslation().distance(towerloc) < 5.0f) {
                    reach.offer(creepClone.get(i));
                }

            }

            return reach;

        }
    };

    protected void setTowerEmpty() {
        setInactive();
        emptySound.play();
        GraphicsState.towerTextureEmpty(this);
        if (!FriendlyState.getEmptyTowers().contains(this)) {
            FriendlyState.addEmptyTower(this);
        }
    }

    protected void shootCreep() {
        if (charges.get(0).getRemBeams() > 0) {
            if (reachable.peek().getControl(RegCreepControl.class) != null) {
                GraphicsState.makeLaserBeam(towerloc, reachable.peek().getLocalTranslation(),
                        getTowerType(), getBeamWidth());
                if (reachable.peek()
                        .getControl(RegCreepControl.class).decCreepHealth(charges.get(0).shoot()) <= 0) {
                    reachable.remove();
                }
            } else {
                reachable.remove();
            }
        } else {
            charges.remove(0);
        }
    }

    public void setBuilt() {
        isActive = true;
    }

    public void setInactive() {
        isActive = false;
    }

    public void setBeamWidth(float beamwidth) {
        this.beamwidth = beamwidth;
    }

    public float getBeamWidth() {
        return beamwidth;
    }

    public int getIndex() {
        return spatial.getUserData("Index");
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getBeamType() {
        return spatial.getUserData("BeamType");
    }

    public void setBeamType(String newtype) {
        spatial.setUserData("BeamType", newtype);
    }

    public String getTowerType() {
        return spatial.getUserData("Type");
    }

    public void setTowerType(String newtype) {
        spatial.setUserData("Type", newtype);
    }

    public void addCharges() {
        charges.add(new Charges(getTowerType()));
        if (!isGlobbed) {
            setActive();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        TowerControl control = new TowerControl(FriendlyState, towerloc);
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
    }
}