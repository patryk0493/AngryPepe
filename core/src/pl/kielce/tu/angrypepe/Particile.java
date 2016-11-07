package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by patryk on 06.11.2016.
 */
public class Particile {

    public AssetManager particleManager;
    private ParticleSystem particleSystem;

    public Particile() {
        particleSystem = ParticleSystem.get();
    }

    public void update() {
        particleSystem.begin();
        particleSystem.updateAndDraw();
        particleSystem.end();
    }

    public void initBillBoardParticles(CameraManager camera) {
        BillboardParticleBatch billboardParticleBatch = new BillboardParticleBatch();
        billboardParticleBatch.setCamera(camera);
        particleSystem.add(billboardParticleBatch);
        initManager();
    }

    public void boomEffect(Vector3 translation) {
        ParticleEffect effect = particleManager.get(ModelManager.PARTICLE, ParticleEffect.class).copy();
        effect.init();
        effect.start();
        effect.translate(translation);
        effect.scale(1f, 1f, 1f);
        effect.rotate(new Vector3(0, 1, 0), 90);
        particleSystem.add(effect);
    }

    private void initManager() {
        particleManager = new AssetManager();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        particleManager.setLoader(ParticleEffect.class, loader);
        particleManager.load(ModelManager.PARTICLE, ParticleEffect.class, loadParam);
        particleManager.finishLoading();
    }

    public ParticleSystem updateAndDraw() {
        update();
        return particleSystem;
    }

    public void dispose() {
        particleSystem.removeAll();
        particleSystem.getBatches().clear();
        particleManager.dispose();
    }

}
