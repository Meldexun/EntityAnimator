package meldexun.entityanimator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import meldexun.entityanimator.util.ChunkAnimatorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(RenderManager.class)
public class MixinRenderManager {

	@Redirect(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"))
	public void onRenderEntityStatic(RenderManager renderManager, Entity entityIn, double x, double y, double z, float yaw, float partialTicks,
			boolean p_188391_10_) {
		RenderChunk renderChunk = Minecraft.getMinecraft().renderGlobal.viewFrustum.getRenderChunk(new BlockPos(entityIn));
		if (renderChunk != null) {
			Vec3d offset = ChunkAnimatorHelper.getOffset(renderChunk);
			if (offset != null) {
				x += offset.x;
				y += offset.y;
				z += offset.z;
			}
		}
		renderManager.renderEntity(entityIn, x, y, z, yaw, partialTicks, p_188391_10_);
	}

	@Redirect(method = "renderMultipass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/Render;renderMultipass(Lnet/minecraft/entity/Entity;DDDFF)V"))
	public void onRenderMultipass(Render<Entity> render, Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
		RenderChunk renderChunk = Minecraft.getMinecraft().renderGlobal.viewFrustum.getRenderChunk(new BlockPos(entityIn));
		if (renderChunk != null) {
			Vec3d offset = ChunkAnimatorHelper.getOffset(renderChunk);
			if (offset != null) {
				x += offset.x;
				y += offset.y;
				z += offset.z;
			}
		}
		render.renderMultipass(entityIn, x, y, z, entityYaw, partialTicks);
	}

}
