package fr.lanfix.simplehealthbar.events;

import fr.lanfix.simplehealthbar.SimpleHealthBar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleHealthBar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderEventHandler {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void hideVanillaHealth(RenderGameOverlayEvent.PreLayer event) {
        final ForgeIngameGui gui = ((ForgeIngameGui) Minecraft.getInstance().gui);
        if (!event.isCanceled()
                && event.getOverlay().equals(ForgeIngameGui.PLAYER_HEALTH_ELEMENT)
                && !mc.options.hideGui
                && gui.shouldDrawSurvivalElements()
                && mc.cameraEntity instanceof Player) {
            gui.left_height += 10;
            if (((Player) mc.cameraEntity).getAbsorptionAmount() > 0)
                gui.left_height += 10;
            event.setCanceled(true);
        }
    }

}
