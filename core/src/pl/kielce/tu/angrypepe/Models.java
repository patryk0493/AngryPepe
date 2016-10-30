package pl.kielce.tu.angrypepe;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by patryk on 30.10.2016.
 */
public class Models {


    public static String MODEL_ISLAND_PROTOTYPE = "models/island_prototype.g3dj";
    public static String MODEL_CLOUD = "models/cloud_prototype.g3dj";
    public static String MODEL_TREE_PROTOTYPE = "models/tree/tree_prototype.g3dj";
    public static String MODEL_STUMP_PROTOTYPE = "models/tree/stump_prototype.g3dj";
    public static String MODEL_LOG_PROTOTYPE = "models/tree/log_prototype.g3dj";
    public static String MODEL_CAVE_PROTOTYPE = "models/cave_prototype.g3dj";
    public static String MODEL_STONE_PROTOTYPE = "models/stone_prototype.g3dj";

    private static String[] models = {
            MODEL_ISLAND_PROTOTYPE,
            MODEL_CLOUD,
            MODEL_STUMP_PROTOTYPE,
            MODEL_TREE_PROTOTYPE,
            MODEL_LOG_PROTOTYPE,
            MODEL_CAVE_PROTOTYPE,
            MODEL_STONE_PROTOTYPE
    };

    public static ArrayList<String> MODELS = new ArrayList<String>(Arrays.asList(models));

}
