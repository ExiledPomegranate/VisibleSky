package com.exiledpomegranate;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class VisibleSkyClient implements ClientModInitializer {
	public static final String MODID = "visiblesky";

	public static final Optional<ModContainer> MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MODID);

	@Override
	public void onInitializeClient() {
		List<String> packNames = List.of("Border30", "Border35", "Border40", "Border50", "Border55", "Border60", "Border100");
		packNames.forEach(this::registerPack);
		registerPack("Border45", ResourcePackActivationType.DEFAULT_ENABLED);
	}
	
	private void registerPack(String name) {
		registerPack(name, ResourcePackActivationType.NORMAL);
	}

	private void registerPack(String name, ResourcePackActivationType type) {
		MOD_CONTAINER.ifPresent((container) -> {
			ResourceManagerHelper.registerBuiltinResourcePack(
					new ResourceLocation(MODID, name),
					container,
					Component.literal(name),
					type
			);
		});
	}
}
