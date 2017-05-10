package eyeq.pinkslime;

import eyeq.pinkslime.client.renderer.entity.RenderSlimePink;
import eyeq.pinkslime.entity.monster.EntitySlimePink;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.common.registry.UEntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

import static eyeq.pinkslime.PinkSlime.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
public class PinkSlime {
    public static final String MOD_ID = "eyeq_pinkslime";

    @Mod.Instance(MOD_ID)
    public static PinkSlime instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        registerEntities();
        if(event.getSide().isServer()) {
            return;
        }
        registerEntityRenderings();
        createFiles();
    }

    public static void registerEntities() {
        UEntityRegistry.registerModEntity(resource, EntitySlimePink.class, "PinkSlime", 0, instance, 0xD1A5BD, 0xC696B0);
        EntityRegistry.addSpawn(EntitySlimePink.class, 4, 4, 4, EnumCreatureType.MONSTER, Biomes.OCEAN, Biomes.BEACH);
    }

	@SideOnly(Side.CLIENT)
    public static void registerEntityRenderings() {
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimePink.class, RenderSlimePink::new);
    }
	
    public static void createFiles() {
    	File project = new File("../1.11.2-PinkSlime");
    	
        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, EntitySlimePink.class, "Brain Slime");
        language.register(LanguageResourceManager.JA_JP, EntitySlimePink.class, "ピンクスライム");

        ULanguageCreator.createLanguage(project, MOD_ID, language);
    }
}
