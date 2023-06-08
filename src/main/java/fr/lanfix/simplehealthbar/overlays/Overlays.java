package fr.lanfix.simplehealthbar.overlays;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

public class Overlays {

    public static HealthBar healthBar = new HealthBar();

    public static void registerOverlays() {
        OverlayRegistry.registerOverlayBelow(ForgeIngameGui.CHAT_PANEL_ELEMENT, "healthBar", Overlays.healthBar);
    }

}
