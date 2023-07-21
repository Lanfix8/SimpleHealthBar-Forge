package fr.lanfix.simplehealthbar.overlays;

import fr.lanfix.simplehealthbar.SimpleHealthBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HealthBar implements IGuiOverlay {

    private static final ResourceLocation fullHealthBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/full.png");
    private static final ResourceLocation witherBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/wither.png");
    private static final ResourceLocation poisonBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/poison.png");
    private static final ResourceLocation frozenBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/frozen.png");
    private ResourceLocation currentBarLocation = fullHealthBarLocation;
    private static final ResourceLocation intermediateHealthBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/intermediate.png");
    private static final ResourceLocation emptyHealthBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/empty.png");

    private static final ResourceLocation absorptionBarLocation = new ResourceLocation(SimpleHealthBar.MOD_ID, "textures/gui/healthbars/absorption.png");
    private static final ResourceLocation guiIconsLocation = new ResourceLocation("minecraft", "textures/gui/icons.png");

    private float intermediateHealth = 0;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
        if (gui.shouldDrawSurvivalElements()) {
            Font font = gui.getFont();
            Player player = (Player) Minecraft.getInstance().cameraEntity;
            if (player == null) return;
            int x = width / 2 - 91;
            int y = height - 39;
            updateBarTextures(player);
            renderHealthValue(font, guiGraphics, x, y, player);
            renderHealthBar(guiGraphics, partialTick, x, y, player);
            if (player.getAbsorptionAmount() > 0) {
                renderAbsorptionBar(guiGraphics, x, y, player);
                renderAbsorptionValue(font, guiGraphics, x, y, player);
            }
        }
    }

    public void updateBarTextures(Player player) {
        if (player.hasEffect(MobEffects.WITHER)) {
            currentBarLocation = witherBarLocation;
        } else if (player.hasEffect(MobEffects.POISON)) {
            currentBarLocation = poisonBarLocation;
        } else if (player.isFullyFrozen()) {
            currentBarLocation = frozenBarLocation;
        } else {
            currentBarLocation = fullHealthBarLocation;
        }
    }

    private void renderHealthValue(Font font, GuiGraphics guiGraphics, int x, int y, Player player) {
        double health = Math.ceil(player.getHealth() * 10) / 10;
        String text = health + "/" + (int) player.getMaxHealth();
        text = text.replace(".0", "");
        guiGraphics.drawString(font, text, x - font.width(text) - 6, y + 1, 0xFF0000);
    }

    private void renderHealthBar(GuiGraphics guiGraphics, float partialTick, int x, int y, Player player) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        // Calculate bar proportions
        float healthProportion;
        float intermediateProportion;
        if (health < intermediateHealth) {
            healthProportion = health / maxHealth;
            intermediateProportion = (intermediateHealth - health) / maxHealth;
        } else {
            healthProportion = intermediateHealth / maxHealth;
            intermediateProportion = 0;
        }
        if (healthProportion > 1) healthProportion = 1F;
        if (healthProportion + intermediateProportion > 1) intermediateProportion = 1 - healthProportion;
        int healthWidth = (int) Math.ceil(80 * healthProportion);
        int intermediateWidth = (int) Math.ceil(80 * intermediateProportion);
        // Display full part
        guiGraphics.blit(currentBarLocation,
                x, y,
                0, 0,
                healthWidth, 9,
                80, 9);
        // Display intermediate part
        guiGraphics.blit(intermediateHealthBarLocation,
                x + healthWidth, y,
                healthWidth, 0,
                intermediateWidth, 9,
                80, 9);
        // Display empty part
        guiGraphics.blit(emptyHealthBarLocation,
                x + healthWidth + intermediateWidth, y,
                healthWidth + intermediateWidth, 0,
                80 - healthWidth - intermediateWidth, 9,
                80, 9);
        // Update intermediate health
        this.intermediateHealth += (health - intermediateHealth) * partialTick * 0.08;
        if (Math.abs(health - intermediateHealth) <= 0.25) {
            this.intermediateHealth = health;
        }
    }

    private void renderAbsorptionValue(Font font, GuiGraphics guiGraphics, int x, int y, Player player) {
        double absorption = Math.ceil(player.getAbsorptionAmount());
        String text = String.valueOf(absorption / 2);
        text = text.replace(".0", "");
        guiGraphics.drawString(font, text, x - font.width(text) - 16, y - 9, 0xFFFF00);
        // blit heart container
        guiGraphics.blit(guiIconsLocation,
                x - 16, y - 10,
                16, 0,
                9, 9,
                256, 256);
        // blit heart
        guiGraphics.setColor(127F, 127F, 0F, 0.5F);
        guiGraphics.blit(guiIconsLocation,
                x - 16, y - 10,
                160, 0,
                9, 9,
                256, 256);
        guiGraphics.setColor(1F, 1F, 1F, 1F);
    }

    private void renderAbsorptionBar(GuiGraphics guiGraphics, int x, int y, Player player) {
        float absorption = player.getAbsorptionAmount();
        float maxHealth = player.getMaxHealth();
        // Calculate bar proportions
        float absorptionProportion = absorption / maxHealth;
        if (absorptionProportion > 1) absorptionProportion = 1F;
        int absorptionWidth = (int) Math.ceil(80 * absorptionProportion);
        // Display full part
        guiGraphics.blit(absorptionBarLocation,
                x, y - 10,
                0, 0,
                absorptionWidth, 9,
                80, 9);
        // Display empty part
        guiGraphics.blit(emptyHealthBarLocation,
                x + absorptionWidth, y - 10,
                absorptionWidth, 0,
                80 - absorptionWidth, 9,
                80, 9);
    }

}
