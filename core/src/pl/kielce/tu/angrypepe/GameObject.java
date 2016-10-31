package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by patryk on 30.10.2016.
 */
public class GameObject {


    public btRigidBody body;
    public Model model;
    public String node;
    public ModelInstance instance;
    public btDefaultMotionState motionState = null;


    public GameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo,
                      Vector3 position, boolean createMotionState) {
        this.model = model;
        this.node = node;
        body = new btRigidBody(constructionInfo);
        instance = new ModelInstance(model);
        instance.transform.trn(position);
        if (createMotionState) {
            motionState = new btDefaultMotionState(instance.transform);
            motionState.setWorldTransform(instance.transform);
            body.setMotionState(motionState);
        } else
            body.setWorldTransform(instance.transform);
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
        public btCollisionShape shape;
        public Vector3 position = Vector3.Zero;
        public boolean motionStateCreate;
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static Vector3 localInertia = new Vector3();

        public BodyConstructor(Model model, String node, btCollisionShape shape, Vector3 pos, float mass,
                               boolean motionStateCreate) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            this.position = pos;
            this.motionStateCreate = motionStateCreate;
            if (this.shape == null) {
                this.shape = createConvexHullShape(this.model, true);
            }
            this.shape.setMargin(0);
            if (mass > 0f)
                this.shape.calculateLocalInertia(mass, localInertia);
            else
                localInertia.set(0, 0, 0);
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, this.shape, localInertia);
        }

        public static btConvexHullShape createConvexHullShape(final Model model, boolean optimize) {
            final Mesh mesh = model.meshes.get(0);
            final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
            if (!optimize) return shape;
            final btShapeHull hull = new btShapeHull(shape);
            hull.buildHull(shape.getMargin());
            final btConvexHullShape result = new btConvexHullShape(hull);
            shape.dispose();
            hull.dispose();
            return result;
        }


        public GameObject construct () {
            return new GameObject(model, node, constructionInfo, position, motionStateCreate);
        }

        @Override
        public void dispose () {
            shape.dispose();
            constructionInfo.dispose();
        }

    }
}
