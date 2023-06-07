package fr.lanfix.simplehealthbar;

import fr.lanfix.simplehealthbar.overlays.Overlays;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SimpleHealthBar.MOD_ID)
public class SimpleHealthBar {

    public static final String MOD_ID = "simplehealthbar";

    public SimpleHealthBar() {
        // Register client setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // register health bar overlay
        Overlays.registerOverlays();
    }

}
