package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by patryk on 30.10.2016.
 */
public class GameObject {


    public btRigidBody body;
    public Model model;
    public String node;
    public ModelInstance instance;

    public GameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo, Vector3 position) {
        this.model = model;
        this.node = node;
        body = new btRigidBody(constructionInfo);
        body.setRestitution(1.0f);
        instance = new ModelInstance(model);
        instance.transform.trn(position);
    }

    public btRigidBody getBody() {
        return body;
    }

    public Model getModel() {
        return model;
    }

    public String getNode() {
        return node;
    }

    public ModelInstance getInstance() {
        return instance;
    }

    static class BodyConstructor implements Disposable {
        public final Model model;
        public final String node;
        public final btCollisionShape shape;
        public Vector3 position = Vector3.Zero;
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static Vector3 localInertia = new Vector3();

        public BodyConstructor(Model model, String node, btCollisionShape shape, Vector3 pos, float mass) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            this.position = pos;
            if (mass > 0f)
                shape.calculateLocalInertia(mass, localInertia);
            else
                localInertia.set(0, 0, 0);
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        }

        public GameObject construct () {
            return new GameObject(model, node, constructionInfo, position);
        }

        @Override
        public void dispose () {
            shape.dispose();
            constructionInfo.dispose();
        }

    }
}
