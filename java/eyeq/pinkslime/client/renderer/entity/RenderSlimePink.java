package eyeq.pinkslime.client.renderer.entity;

import eyeq.util.client.renderer.EntityRenderResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

import static eyeq.pinkslime.PinkSlime.MOD_ID;

public class RenderSlimePink extends RenderSlime {
    protected static final ResourceLocation textures = new EntityRenderResourceLocation(MOD_ID, "slime_pink");

    public RenderSlimePink(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySlime entity)
    {
        return textures;
    }
}
