package meldexun.entityanimator.util;

import java.util.WeakHashMap;

import javax.annotation.Nullable;

import lumien.chunkanimator.ChunkAnimator;
import lumien.chunkanimator.handler.AnimationHandler;
import meldexun.reflectionutil.ReflectionField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import penner.easing.Back;
import penner.easing.Bounce;
import penner.easing.Circ;
import penner.easing.Cubic;
import penner.easing.Elastic;
import penner.easing.Expo;
import penner.easing.Linear;
import penner.easing.Quad;
import penner.easing.Quart;
import penner.easing.Quint;
import penner.easing.Sine;

public class ChunkAnimatorHelper {

	private static final ReflectionField<WeakHashMap<RenderChunk, ?>> TIME_STAMPS = new ReflectionField<>(AnimationHandler.class, "timeStamps", "timeStamps");
	private static final ReflectionField<Long> TIME_STAMP = new ReflectionField<>("lumien.chunkanimator.handler.AnimationHandler$AnimationData", "timeStamp",
			"timeStamp");
	private static final ReflectionField<EnumFacing> CHUNK_FACING = new ReflectionField<>("lumien.chunkanimator.handler.AnimationHandler$AnimationData",
			"chunkFacing", "chunkFacing");

	@Nullable
	public static Vec3d getOffset(RenderChunk renderChunk) {
		if (TIME_STAMPS.get(ChunkAnimator.INSTANCE.animationHandler).containsKey(renderChunk)) {
			Object animationData = TIME_STAMPS.get(ChunkAnimator.INSTANCE.animationHandler).get(renderChunk);
			long time = TIME_STAMP.getLong(animationData);
			int mode = ChunkAnimator.INSTANCE.config.getMode();

			if (time == -1L) {
				time = System.currentTimeMillis();

				TIME_STAMP.setLong(animationData, time);

				// Mode 4 Set Chunk Facing
				if (mode == 4) {
					BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().player.getPosition();
					zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);

					BlockPos zeroedCenteredChunkPos = renderChunk.getPosition().add(8, -renderChunk.getPosition().getY(), 8);

					Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

					int difX = Math.abs(dif.getX());
					int difZ = Math.abs(dif.getZ());

					EnumFacing chunkFacing;

					if (difX > difZ) {
						if (dif.getX() > 0) {
							chunkFacing = EnumFacing.EAST;
						} else {
							chunkFacing = EnumFacing.WEST;
						}
					} else {
						if (dif.getZ() > 0) {
							chunkFacing = EnumFacing.SOUTH;
						} else {
							chunkFacing = EnumFacing.NORTH;
						}
					}

					CHUNK_FACING.set(animationData, chunkFacing);
				}
			}

			long timeDif = System.currentTimeMillis() - time;

			int animationDuration = ChunkAnimator.INSTANCE.config.getAnimationDuration();

			if (timeDif < animationDuration) {
				int chunkY = renderChunk.getPosition().getY();
				double modY;

				if (mode == 2) {
					if (chunkY < Minecraft.getMinecraft().world.provider.getHorizon()) {
						mode = 0;
					} else {
						mode = 1;
					}
				}

				if (mode == 4) {
					mode = 3;
				}

				switch (mode) {
				case 0:
					return new Vec3d(0, -chunkY + getFunctionValue(timeDif, 0, chunkY, animationDuration), 0);
				case 1:
					return new Vec3d(0, 256 - chunkY - getFunctionValue(timeDif, 0, 256 - chunkY, animationDuration), 0);
				case 3:
					EnumFacing chunkFacing = CHUNK_FACING.get(animationData);

					if (chunkFacing != null) {
						Vec3i vec = chunkFacing.getDirectionVec();
						double mod = -(200D - (200D / animationDuration * timeDif));

						mod = -(200 - getFunctionValue(timeDif, 0, 200, animationDuration));

						return new Vec3d(vec.getX() * mod, 0, vec.getZ() * mod);
					}
					break;
				}
			} else {
				TIME_STAMPS.get(ChunkAnimator.INSTANCE.animationHandler).remove(renderChunk);
			}
		}
		return null;
	}

	private static float getFunctionValue(float t, float b, float c, float d) {
		switch (ChunkAnimator.INSTANCE.config.getEasingFunction()) {
		case 0: // Linear
			return Linear.easeOut(t, b, c, d);
		case 1: // Quadratic Out
			return Quad.easeOut(t, b, c, d);
		case 2: // Cubic Out
			return Cubic.easeOut(t, b, c, d);
		case 3: // Quartic Out
			return Quart.easeOut(t, b, c, d);
		case 4: // Quintic Out
			return Quint.easeOut(t, b, c, d);
		case 5: // Expo Out
			return Expo.easeOut(t, b, c, d);
		case 6: // Sin Out
			return Sine.easeOut(t, b, c, d);
		case 7: // Circle Out
			return Circ.easeOut(t, b, c, d);
		case 8: // Back
			return Back.easeOut(t, b, c, d);
		case 9: // Bounce
			return Bounce.easeOut(t, b, c, d);
		case 10: // Elastic
			return Elastic.easeOut(t, b, c, d);
		}

		return Sine.easeOut(t, b, c, d);
	}

}
