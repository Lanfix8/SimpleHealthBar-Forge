package fr.lanfix.simplehealthbar.events;

import fr.lanfix.simplehealthbar.SimpleHealthBar;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleHealthBar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class VanillaGuiHandler {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final ResourceLocation vanillaHealthBar = new ResourceLocation("minecraft", "player_health");

    @SubscribeEvent
    public static void disableVanillaHealth(RenderGuiOverlayEvent.Pre event) {
        final ForgeGui gui = ((ForgeGui) mc.gui);
        if (!event.isCanceled()
                && event.getOverlay().id().equals(vanillaHealthBar)
                && !mc.options.hideGui
                && gui.shouldDrawSurvivalElements()
                && mc.cameraEntity instanceof Player) {
            gui.leftHeight += 10;
            if (((Player) mc.cameraEntity).getAbsorptionAmount() > 0)
                gui.leftHeight += 10;
            event.setCanceled(true);
        }
    }

}
