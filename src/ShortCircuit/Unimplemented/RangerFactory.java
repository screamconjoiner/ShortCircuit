package ShortCircuit.Unimplemented;

import ShortCircuit.States.Game.EnemyState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Connor
 */
public class RangerFactory {

    private EnemyState cs;

    public RangerFactory(EnemyState _cs) {
        cs = _cs;
    }

    public Spatial getRanger(Vector3f location, int index) {
        /*Geometry ranger_geom = cs.getRangerGeom();
        ranger_geom.setMaterial(cs.getAssetManager().loadMaterial(
                "Materials/" + cs.getMatDir() + "/RangerCreep.j3m"));
        ranger_geom.setLocalTranslation(location);
        Spatial ranger = ranger_geom;
        ranger.setUserData("Name", "Ranger");
        ranger.setUserData("Health", 200);
        ranger.setUserData("VictimTower", index);
        //ranger.addControl(new RangerControl(cs, ));
        return ranger;*/
        return null;
    }
}
