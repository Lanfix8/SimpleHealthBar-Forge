package fr.lanfix.simplehealthbar.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.lanfix.simplehealthbar.SimpleHealthBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
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
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        if (gui.shouldDrawSurvivalElements()) {
            Font font = gui.getFont();
            Player player = (Player) Minecraft.getInstance().cameraEntity;
            if (player == null) return;
            float x = (float) width / 2 - 91;
            float y = height - 39;
            updateBarTextures(player);
            renderHealthValue(font, poseStack, x, y, player);
            renderHealthBar(poseStack, partialTick, x, y, player);
            if (player.getAbsorptionAmount() > 0) {
                renderAbsorptionValue(font, poseStack, x, y, player);
                renderAbsorptionBar(poseStack, x, y, player);
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

    private void renderHealthValue(Font font, PoseStack poseStack, float x, float y, Player player) {
        double health = Math.ceil(player.getHealth() * 10) / 10;
        String text = health + "/" + (int) player.getMaxHealth();
        text = text.replace(".0", "");
        font.draw(poseStack, text, x - font.width(text) - 6, y + 1, 0xFF0000);
    }

    private void renderHealthBar(PoseStack poseStack, float partialTick, float x, float y, Player player) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
        RenderSystem.setShaderTexture(0, currentBarLocation);
        GuiComponent.blit(poseStack,
                (int) x, (int) y,
                0, 0,
                healthWidth, 9,
                80, 9);
        // Display intermediate part
        RenderSystem.setShaderTexture(0, intermediateHealthBarLocation);
        GuiComponent.blit(poseStack,
                (int) x + healthWidth, (int) y,
                healthWidth, 0,
                intermediateWidth, 9,
                80, 9);
        // Display empty part
        RenderSystem.setShaderTexture(0, emptyHealthBarLocation);
        GuiComponent.blit(poseStack,
                (int) x + healthWidth + intermediateWidth, (int) y,
                healthWidth + intermediateWidth, 0,
                80 - healthWidth - intermediateWidth, 9,
                80, 9);
        // Update intermediate health
        this.intermediateHealth += (health - intermediateHealth) * partialTick * 0.08;
        if (Math.abs(health - intermediateHealth) <= 0.25) {
            this.intermediateHealth = health;
        }
    }

    private void renderAbsorptionValue(Font font, PoseStack poseStack, float x, float y, Player player) {
        double absorption = Math.ceil(player.getAbsorptionAmount());
        String text = String.valueOf(absorption / 2);
        text = text.replace(".0", "");
        font.draw(poseStack, text, x - font.width(text) - 16, y - 9, 0xFFFF00);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, guiIconsLocation);
        // blit heart container
        GuiComponent.blit(poseStack,
                (int) x - 16, (int) y - 10,
                16, 0,
                9, 9,
                256, 256);
        // blit heart
        RenderSystem.setShaderColor(255.0F, 255.0F, 0.0F, 0.0F);
        GuiComponent.blit(poseStack,
                (int) x - 16, (int) y - 10,
                160, 0,
                9, 9,
                256, 256);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderAbsorptionBar(PoseStack poseStack, float x, float y, Player player) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float absorption = player.getAbsorptionAmount();
        float maxHealth = player.getMaxHealth();
        // Calculate bar proportions
        float absorptionProportion = absorption / maxHealth;
        if (absorptionProportion > 1) absorptionProportion = 1F;
        int absorptionWidth = (int) Math.ceil(80 * absorptionProportion);
        // Display full part
        RenderSystem.setShaderTexture(0, absorptionBarLocation);
        GuiComponent.blit(poseStack,
                (int) x, (int) y - 10,
                0, 0,
                absorptionWidth, 9,
                80, 9);
        // Display empty part
        RenderSystem.setShaderTexture(0, emptyHealthBarLocation);
        GuiComponent.blit(poseStack,
                (int) x + absorptionWidth, (int) y - 10,
                absorptionWidth, 0,
                80 - absorptionWidth, 9,
                80, 9);
    }

}
