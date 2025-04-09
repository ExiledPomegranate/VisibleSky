package com.exiledpomegranate;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BorderSettingsScreen extends Screen {
    private EditBox borderWidthField;
    private EditBox borderColorField;
    private String errorText = "";

    public BorderSettingsScreen() {
        super(Component.literal("Sky Block Border Settings"));
    }

    @Override
    public void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Text Field for Border Width
        borderWidthField = new EditBox(this.font, centerX - 50, centerY - 70, 100, 20, Component.literal("Border Width"));
        borderWidthField.setMaxLength(10);
        borderWidthField.setValue(Float.toString(VisibleSkyClient.getSavedBorderWidth()));

        // Text Field for Border Color (comma-separated RGBA)
        borderColorField = new EditBox(this.font, centerX - 50, centerY - 30, 100, 20, Component.literal("Border Color"));
        borderColorField.setMaxLength(20);
        float[] color = VisibleSkyClient.getSavedBorderColor();
        borderColorField.setValue(color[0] + ", " + color[1] + ", " + color[2] + ", " + color[3]);

        // Add them to the screen
        this.addRenderableWidget(borderWidthField);
        this.addRenderableWidget(borderColorField);

        // Add a button to apply changes
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button ->
                applySettings()).bounds(centerX - 120, centerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button ->
                VisibleSkyClient.saveConfig()).bounds(centerX + 20, centerY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Load defaults"), button -> {
            borderWidthField.setValue("0.02");
            float[] newcolor = new float[]{0.0f, 0.2f, 0.0f, 0.45f};
            borderColorField.setValue(newcolor[0] + ", " + newcolor[1] + ", " + newcolor[2] + ", " + newcolor[3]);
        }).bounds(centerX - 120, centerY + 30, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Load from save"), button -> {
            VisibleSkyClient.loadConfig();
            borderWidthField.setValue(Float.toString(VisibleSkyClient.getSavedBorderWidth()));
            float[] newcolor = VisibleSkyClient.getSavedBorderColor();
            borderColorField.setValue(newcolor[0] + ", " + newcolor[1] + ", " + newcolor[2] + ", " + newcolor[3]);
        }).bounds(centerX + 20, centerY + 30, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(null);
            }
        }).bounds(centerX - 120, centerY + 60, 240 ,20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawString(this.font, "Border Width",
                (this.width / 2) - (this.font.width("Border Width") / 2), (this.height / 2) - 80, 0xFFFFFF, true);
        guiGraphics.drawString(this.font, "Border Color (RGBA 0-1)",
                (this.width / 2) - (this.font.width("Border Color (RGBA 0-1)") / 2), (this.height / 2) - 40, 0xFFFFFF, true);
        guiGraphics.drawString(this.font, errorText, (this.width / 2) - (this.font.width(errorText) / 2), 20, 0xFF0000, true);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }

    private void applySettings() {
        float newBorderWidth;
        try {
            newBorderWidth = Float.parseFloat(borderWidthField.getValue().strip());
        } catch (NumberFormatException e) {
            errorText = "Invalid border width :(";
            return;
        }

        float r;
        float g;
        float a;
        float b;
        try {
            String[] rgba = borderColorField.getValue().strip().split(", ");
            if (rgba.length == 4) {
                r = Float.parseFloat(rgba[0]);
                g = Float.parseFloat(rgba[1]);
                b = Float.parseFloat(rgba[2]);
                a = Float.parseFloat(rgba[3]);
            } else {
                errorText = "Invalid border color :(";
                return;
            }
        } catch (NumberFormatException e) {
            errorText = "Invalid border color :(";
            return;
        }

        // if anything is outside of the range 0f to 1f
        if (r < 0f || r > 1f || g < 0f || g > 1f || a < 0f || a > 1f || b < 0f || b > 1f) {
            errorText = "Border color outside of range 0 to 1 :(";
            return;
        }
        errorText = "";
        VisibleSkyClient.borderColor.set(new float[]{r, g, b, a});
        VisibleSkyClient.updateSavedBorderColor(new float[]{r, g, b, a});
        VisibleSkyClient.borderWidth.set(newBorderWidth);
        VisibleSkyClient.updateSavedBorderWidth(newBorderWidth);
    }
}
