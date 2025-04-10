package com.exiledpomegranate;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        borderWidthField.setValue(Float.toString(VisibleSkyClient.getSavedBorderWidth() * 16));

        // Text Field for Border Color (comma-separated RGBA)
        borderColorField = new EditBox(this.font, centerX - 75, centerY - 30, 150, 20, Component.literal("Border Color"));
        borderColorField.setMaxLength(30);
        float[] color = VisibleSkyClient.getSavedBorderColor();
        borderColorField.setValue((color[0] * 255) + ", " + (color[1] * 255) + ", " + (color[2] * 255) + ", " + (color[3] * 255));

        // Add them to the screen
        this.addRenderableWidget(borderWidthField);
        this.addRenderableWidget(borderColorField);

        // Add a button to apply changes
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button ->
                applySettings()).bounds(centerX - 120, centerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button ->
                VisibleSkyClient.saveConfig()).bounds(centerX + 20, centerY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Load defaults"), button -> {
            borderWidthField.setValue("0.33");
            float[] newcolor = new float[]{0.0f, 50f, 0.0f, 115f};
            borderColorField.setValue(newcolor[0] + ", " + newcolor[1] + ", " + newcolor[2] + ", " + newcolor[3]);
        }).bounds(centerX - 120, centerY + 30, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Load from save"), button -> {
            VisibleSkyClient.loadConfig();
            borderWidthField.setValue(Float.toString(VisibleSkyClient.getSavedBorderWidth() * 16));
            float[] newcolor = VisibleSkyClient.getSavedBorderColor();
            borderColorField.setValue((newcolor[0] * 255) + ", " + (newcolor[1] * 255) + ", " + (newcolor[2] * 255) + ", " + (newcolor[3] * 255));
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

        guiGraphics.drawString(this.font, "Border Width (Pixels)",
                (this.width / 2) - (this.font.width("Border Width (Pixels)") / 2), (this.height / 2) - 80, 0xFFFFFF, true);
        guiGraphics.drawString(this.font, "Border Color (RGBA 0-255)",
                (this.width / 2) - (this.font.width("Border Color (RGBA 0-255)") / 2), (this.height / 2) - 40, 0xFFFFFF, true);
        guiGraphics.drawString(this.font, errorText, (this.width / 2) - (this.font.width(errorText) / 2), 20, 0xFF0000, true);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }

    private void applySettings() {
        float newBorderWidth;
        try {
            newBorderWidth = Float.parseFloat(borderWidthField.getValue().strip()) / 16;
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
                r = Float.parseFloat(rgba[0]) / 255;
                g = Float.parseFloat(rgba[1]) / 255;
                b = Float.parseFloat(rgba[2]) / 255;
                a = Float.parseFloat(rgba[3]) / 255;
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
