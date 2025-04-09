package com.exiledpomegranate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VisibleSkyClient implements ClientModInitializer {
	public static final String MODID = "assets/visiblesky";
	private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "visiblesky.json");
	public static JsonObject configData = new JsonObject();

	public static Uniform borderWidth;
	public static Uniform borderColor;

	private static KeyMapping keyMapping;

	@Override
	public void onInitializeClient() {
		loadConfig();

		keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.visiblesky.bordersettings",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"key.categories.misc"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(keyMapping.isDown()) {
				client.setScreen(new BorderSettingsScreen());
			}
		});
	}

	public static void loadConfig() {
		if (configFile.exists()) {
			try (FileReader reader = new FileReader(configFile)) {
				configData = new Gson().fromJson(reader, JsonObject.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveConfig() {
		try (FileWriter writer = new FileWriter(configFile)) {
			new Gson().toJson(configData, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static float getSavedBorderWidth() {
		return configData.has("borderWidth") ? configData.get("borderWidth").getAsFloat() : 0.02f;
	}

	public static void updateSavedBorderWidth(float newBorderWidth) {
		configData.addProperty("borderWidth", newBorderWidth);
	}

	public static float[] getSavedBorderColor() {
		String data = configData.has("borderColor") ? configData.get("borderColor").getAsString() : "0.0, 0.2, 0.0, 0.45";
		String[] floats = data.split(", ");
		if(floats.length != 4) return new float[]{0f, 0.2f, 0f, 0.45f};
		float[] output = new float[4];
		try {
			for (int i = 0; i < 4; i++) {
				output[i] = Float.parseFloat(floats[i]);
			}
		} catch (NumberFormatException e) {
			return new float[]{0f, 0.2f, 0f, 0.45f};
		}
		for(float value : output) {
			if(value < 0f || value > 1f) {
				return new float[]{0f, 0.2f, 0f, 0.45f};
			}
		}
		return output;
	}

	public static void updateSavedBorderColor(float[] newBorderColor) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			sb.append(newBorderColor[i]);
			if (i < 3) {
				sb.append(", ");
			}
		}

		configData.addProperty("borderColor", sb.toString());
	}
}
