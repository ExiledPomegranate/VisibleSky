package com.exiledpomegranate.mixins;

import com.exiledpomegranate.VisibleSkyClient;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(GlProgram.class)
public class UniformInjector {
    @Final @Shadow
    private int programId;
    @Final @Shadow
    private List<Uniform> uniforms;
    @Final @Shadow
    private Map<String, Uniform> uniformsByName;
    @Final @Shadow
    private static Logger LOGGER;
    @Final @Shadow
    private List<String> samplers;
    @Final @Shadow
    private IntList samplerLocations;
    @Final @Shadow
    private String debugLabel;
    @Nullable @Shadow
    public Uniform MODEL_VIEW_MATRIX;
    @Nullable @Shadow
    public Uniform PROJECTION_MATRIX;
    @Nullable @Shadow
    public Uniform TEXTURE_MATRIX;
    @Nullable @Shadow
    public Uniform SCREEN_SIZE;
    @Nullable @Shadow
    public Uniform COLOR_MODULATOR;
    @Nullable @Shadow
    public Uniform LIGHT0_DIRECTION;
    @Nullable @Shadow
    public Uniform LIGHT1_DIRECTION;
    @Nullable @Shadow
    public Uniform GLINT_ALPHA;
    @Nullable @Shadow
    public Uniform FOG_START;
    @Nullable @Shadow
    public Uniform FOG_END;
    @Nullable @Shadow
    public Uniform FOG_COLOR;
    @Nullable @Shadow
    public Uniform FOG_SHAPE;
    @Nullable @Shadow
    public Uniform LINE_WIDTH;
    @Nullable @Shadow
    public Uniform GAME_TIME;
    @Nullable @Shadow
    public Uniform MODEL_OFFSET;

    @Shadow
    private Uniform createUniform(RenderPipeline.UniformDescription uniformDescription) {
        throw new RuntimeException("What did you do?");
    }

    @Shadow
    @Nullable
    private static UniformType getTypeFromGl(int i) {
        throw new RuntimeException("What did you do?");
    }

    @Shadow
    @Nullable
    public Uniform getUniform(String string) {
        throw new RuntimeException("What did you do?");
    }

    /**
     * @author ExiledPomegranate
     * @reason idk how to add uniforms correctly
     */
    @Overwrite
    public void setupUniforms(List<RenderPipeline.UniformDescription> badlist, List<String> list2) {
        RenderSystem.assertOnRenderThread();

        List<RenderPipeline.UniformDescription> list = new ArrayList<>(badlist);

        list.add(new RenderPipeline.UniformDescription("borderWidth", UniformType.FLOAT));
        list.add(new RenderPipeline.UniformDescription("borderColor", UniformType.VEC4));

        for (RenderPipeline.UniformDescription uniformDescription : list) {
            String string = uniformDescription.name();
            int i = Uniform.glGetUniformLocation(this.programId, string);
            if (i != -1) {
                Uniform uniform = this.createUniform(uniformDescription);
                uniform.setLocation(i);
                this.uniforms.add(uniform);
                this.uniformsByName.put(string, uniform);
            }
        }

        for (String string2 : list2) {
            int j = Uniform.glGetUniformLocation(this.programId, string2);
            if (j == -1) {
                LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", this.debugLabel, string2);
            } else {
                this.samplers.add(string2);
                this.samplerLocations.add(j);
            }
        }

        int k = GlStateManager.glGetProgrami(this.programId, 35718);

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);

            for (int l = 0; l < k; l++) {
                String string3 = GL20.glGetActiveUniform(this.programId, l, intBuffer, intBuffer2);
                UniformType uniformType = getTypeFromGl(intBuffer2.get(0));
                if (!this.uniformsByName.containsKey(string3) && !list2.contains(string3)) {
                    if (uniformType != null) {
                        LOGGER.info("Found unknown but potentially supported uniform {} in {}", string3, this.debugLabel);
                        Uniform uniform2 = new Uniform(string3, uniformType);
                        uniform2.setLocation(l);
                        this.uniforms.add(uniform2);
                        this.uniformsByName.put(string3, uniform2);
                    } else {
                        LOGGER.warn("Found unknown and unsupported uniform {} in {}", string3, this.debugLabel);
                    }
                }
            }
        }

        if(this.getUniform("borderWidth") instanceof Uniform uniform) {
            uniform.set(VisibleSkyClient.getSavedBorderWidth());
            VisibleSkyClient.borderWidth = uniform;
        }
        if(this.getUniform("borderColor") instanceof Uniform uniform) {
            uniform.set(VisibleSkyClient.getSavedBorderColor());
            VisibleSkyClient.borderColor = uniform;
        }

        this.MODEL_VIEW_MATRIX = this.getUniform("ModelViewMat");
        this.PROJECTION_MATRIX = this.getUniform("ProjMat");
        this.TEXTURE_MATRIX = this.getUniform("TextureMat");
        this.SCREEN_SIZE = this.getUniform("ScreenSize");
        this.COLOR_MODULATOR = this.getUniform("ColorModulator");
        this.LIGHT0_DIRECTION = this.getUniform("Light0_Direction");
        this.LIGHT1_DIRECTION = this.getUniform("Light1_Direction");
        this.GLINT_ALPHA = this.getUniform("GlintAlpha");
        this.FOG_START = this.getUniform("FogStart");
        this.FOG_END = this.getUniform("FogEnd");
        this.FOG_COLOR = this.getUniform("FogColor");
        this.FOG_SHAPE = this.getUniform("FogShape");
        this.LINE_WIDTH = this.getUniform("LineWidth");
        this.GAME_TIME = this.getUniform("GameTime");
        this.MODEL_OFFSET = this.getUniform("ModelOffset");
    }
}
