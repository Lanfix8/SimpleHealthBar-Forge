package fr.lanfix.simplehealthbar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(SimpleHealthBar.MOD_ID)
public class SimpleHealthBar {

    public static final String MOD_ID = "simplehealthbar";

    public SimpleHealthBar() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

}
