package fr.lanfix.simplehealthbar.overlays;

import net.minecraftforge.client.gui.OverlayRegistry;

public class Overlays {

    public static HealthBar healthBar = new HealthBar();

    public static void registerOverlays() {
        OverlayRegistry.registerOverlayTop("healthBar", Overlays.healthBar);
    }

}
