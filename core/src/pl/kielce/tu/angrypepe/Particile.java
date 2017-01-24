package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Klasa reprezentująca efekt cząsteczkowy.
 * @author Patryk Eliasz, Karol Rębiś */
public class Particile {

    /**
     * Zminna przechowywująca efekty cząsteczkowe.
     */
    public AssetManager particleManager;
    private ParticleSystem particleSystem;

    /**
     * Konstruktor klasy Particile
     */
    public Particile() {
        particleSystem = ParticleSystem.get();
    }

    /**
     * Update - rysowanie efektu cząsteczkowego.
     */
    public void update() {
        particleSystem.begin();
        particleSystem.updateAndDraw();
        particleSystem.end();
    }

    /**
     * Inicjowanie systemu cząsteczek - billboard
     *
     * @param camera kamera
     */
    public void initBillBoardParticles(CameraManager camera) {
        BillboardParticleBatch billboardParticleBatch = new BillboardParticleBatch();
        billboardParticleBatch.setCamera(camera);
        particleSystem.add(billboardParticleBatch);
        initManager();
    }

    /**
     * Wybuch - załadowanie modelu cząsteczki oraz jego dodanie do listy do wykonania.
     *
     * @param translation pozycja efektu
     * @param scale       skala
     */
    public void boomEffect(Vector3 translation, float scale) {
        ParticleEffect effect = particleManager.get(ModelManager.PARTICLE, ParticleEffect.class).copy();
        effect.init();
        effect.start();
        effect.translate(translation);
        effect.scale(scale, scale, scale);
        effect.rotate(new Vector3(0, 1, 0), 90);
        particleSystem.add(effect);
    }

    /**
     * Inicjowanie systemu cząsteczek - ładowanie ich
     */
    public void initManager() {
        particleManager = new AssetManager();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        particleManager.setLoader(ParticleEffect.class, loader);
        particleManager.load(ModelManager.PARTICLE, ParticleEffect.class, loadParam);
        particleManager.finishLoading();
    }

    /**
     * Rysowanie cząsteczki
     *
     * @return the particle system
     */
    public ParticleSystem updateParticile() {
        update();
        return particleSystem;
    }

    /**
     * Dispose.
     */
    public void dispose() {
        particleSystem.removeAll();
        particleSystem.getBatches().clear();
        particleManager.dispose();
    }

}
