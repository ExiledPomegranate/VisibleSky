package com.exiledpomegranate.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(value = RenderPipeline.Builder.class, remap = false)
public class UniformAssigner {
    @Shadow
    private Optional<ResourceLocation> fragmentShader;

    /**
     * @author ExiledPomegranate
     * @reason Need to give shader my uniforms
     */
    @Overwrite
    public RenderPipeline.Builder withFragmentShader(String string) {
        this.fragmentShader = Optional.of(ResourceLocation.withDefaultNamespace(string));
        RenderPipeline.Builder outputBuilder = ((RenderPipeline.Builder) (Object) this);
        if(string.equals("core/sky_block")) {
            return outputBuilder
                    .withUniform("borderWidth", UniformType.FLOAT)
                    .withUniform("borderColor", UniformType.VEC4);
        } else {
            return outputBuilder;
        }
    }
}
