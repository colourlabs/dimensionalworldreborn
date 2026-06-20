package net.colourlabs.dimensionalworldreborn.world;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class MiningWorldSettings extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "mining_world_settings";
    private static MiningWorldSettings.Values values = Values.defaults();

    public MiningWorldSettings() {
        super(GSON, DIRECTORY);
    }

    public static Values get() {
        return values;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        Values loaded = Values.defaults();

        objects.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                if (entry.getValue().isJsonObject()) {
                    loaded.apply(entry.getValue().getAsJsonObject());
                }
            });

        values = loaded;
        DimensionalWorldReborn.LOGGER.info(
            "Loaded mining world settings: arrivalY={}, coordinateScale={}, createPlatform={}, welcomeMessage='{}'",
            values.arrivalY(),
            values.coordinateScale(),
            values.createPlatform(),
            values.welcomeMessage()
        );
    }

    public static class Values {
        private int arrivalY;
        private double coordinateScale;
        private boolean createPlatform;
        private boolean showWelcomeMessage;
        private String welcomeMessage;

        private Values(int arrivalY, double coordinateScale, boolean createPlatform, boolean showWelcomeMessage, String welcomeMessage) {
            this.arrivalY = arrivalY;
            this.coordinateScale = coordinateScale;
            this.createPlatform = createPlatform;
            this.showWelcomeMessage = showWelcomeMessage;
            this.welcomeMessage = welcomeMessage;
        }

        public static Values defaults() {
            return new Values(72, 1.0D, true, true, "Welcome to the Mining World.");
        }

        public int arrivalY() {
            return arrivalY;
        }

        public double coordinateScale() {
            return coordinateScale;
        }

        public boolean createPlatform() {
            return createPlatform;
        }

        public boolean showWelcomeMessage() {
            return showWelcomeMessage;
        }

        public String welcomeMessage() {
            return welcomeMessage;
        }

        private void apply(JsonObject json) {
            arrivalY = clamp(GsonHelper.getAsInt(json, "arrival_y", arrivalY), -60, 320);
            coordinateScale = clamp(GsonHelper.getAsDouble(json, "coordinate_scale", coordinateScale), 0.01D, 100.0D);
            createPlatform = GsonHelper.getAsBoolean(json, "create_platform", createPlatform);
            showWelcomeMessage = GsonHelper.getAsBoolean(json, "show_welcome_message", showWelcomeMessage);
            welcomeMessage = GsonHelper.getAsString(json, "welcome_message", welcomeMessage);
        }

        private static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}
