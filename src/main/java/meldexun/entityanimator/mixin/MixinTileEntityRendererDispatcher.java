package meldexun.entityanimator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import meldexun.entityanimator.util.ChunkAnimatorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

	@Redirect(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V"))
	public void onRenderEntityStatic(TileEntityRendererDispatcher renderer, TileEntity tileEntityIn, double x, double y, double z, float partialTicks,
			int destroyStage, float p_192854_10_) {
		RenderChunk renderChunk = Minecraft.getMinecraft().renderGlobal.viewFrustum.getRenderChunk(tileEntityIn.getPos());
		if (renderChunk != null) {
			Vec3d offset = ChunkAnimatorHelper.getOffset(renderChunk);
			if (offset != null) {
				x += offset.x;
				y += offset.y;
				z += offset.z;
			}
		}
		renderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, 1.0F);
	}

}
