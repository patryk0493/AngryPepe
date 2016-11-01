package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
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
    protected float scaleRatio;


    public GameObject(Model model, ModelInstance instance, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo,
                       float scaleRatio, boolean createMotionState) {
        this.model = model;
        this.node = node;
        this.scaleRatio = scaleRatio;
        this.instance = instance;
        body = new btRigidBody(constructionInfo);

        if (createMotionState) {
            motionState = new btDefaultMotionState(this.instance.transform);
            motionState.setWorldTransform(this.instance.transform);
            body.setMotionState(motionState);
        } else
            body.setWorldTransform(this.instance.transform);
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

    public void getWorldTransform() {
        if (this.motionState != null)
            this.motionState.getWorldTransform(this.getInstance().transform);
    }

    static class BodyConstructor implements Disposable {
        public final Model model;
        public final String node;
        private final float scaleRatio;
        public btCollisionShape shape;
        public Vector3 position = Vector3.Zero;
        public boolean motionStateCreate;
        public ModelInstance instance;
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static Vector3 localInertia = new Vector3();

        public BodyConstructor(Model model, String node, btCollisionShape shape, Vector3 pos, float mass,
                               float scaleRatio, boolean motionStateCreate) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            this.scaleRatio = scaleRatio;
            this.position = pos;
            this.motionStateCreate = motionStateCreate;

            // INSTANCE
            this.instance = new ModelInstance(model);
            instance.transform.trn(position);
            this.instance.transform.scl(scaleRatio, scaleRatio,scaleRatio);

            // SHAPE
            if (this.shape == null) {
                this.shape = createConvexHullShape(this.model, true);
            }
            this.shape.setMargin(0);
            //this.shape.setLocalScaling(new Vector3(scaleRatio, scaleRatio, scaleRatio));
            //TODO FIX SCALING
            this.shape = new btUniformScalingShape((btConvexShape) this.shape, scaleRatio);

            // INERTIA
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
            return new GameObject(model, instance, node, constructionInfo, scaleRatio, motionStateCreate);
        }

        @Override
        public void dispose () {
            shape.dispose();
            constructionInfo.dispose();
        }

    }
}
