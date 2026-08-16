package com.impossibles5.heatandsteam.client;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.stove.ThermometerBlock;
import com.impossibles5.heatandsteam.stove.ThermometerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ThermometerRenderer implements BlockEntityRenderer<ThermometerBlockEntity> {
    private static final ResourceLocation MERCURY =
            ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "textures/block/mercury.png");

    private static final int COLOR_TEMPERATURE = 0xFFC44A30;
    private static final int COLOR_HUMIDITY = 0xFF3E70A8;

    private static final int COLOR_SEALED = 0xFF4E8F3A;
    private static final int COLOR_LEAKING = 0xFFBE2D26;

    private static final float FACE_Z = 14.0F / 16.0F - 0.001F;

    private static final float DIAL_TEMPERATURE_X = 12.0F / 16.0F;
    private static final float DIAL_HUMIDITY_X = 4.0F / 16.0F;
    private static final float DIAL_Y = 8.0F / 16.0F;

    private static final float NEEDLE_LENGTH = 1.7F / 16.0F;
    private static final float NEEDLE_TAIL = 0.45F / 16.0F;
    private static final float NEEDLE_HALF_WIDTH = 0.16F / 16.0F;

    private static final float LAMP_X0 = 7.0F / 16.0F;
    private static final float LAMP_X1 = 9.0F / 16.0F;
    private static final float LAMP_Y0 = 12.0F / 16.0F;
    private static final float LAMP_Y1 = 14.0F / 16.0F;

    private static final float NEEDLE_EASE_PER_SECOND = 2.5F;

    private static final float SWEEP_START = -135.0F;
    private static final float SWEEP = 270.0F;

    public ThermometerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ThermometerBlockEntity gauge, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        Direction facing = gauge.getBlockState().getValue(ThermometerBlock.FACING);

        pose.pushPose();

        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-modelAngle(facing)));
        pose.translate(-0.5F, 0.0F, -0.5F);

        float[] eased = gauge.easedFill(NEEDLE_EASE_PER_SECOND);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(MERCURY));

        needle(consumer, pose, light, overlay, DIAL_TEMPERATURE_X, eased[0], COLOR_TEMPERATURE);
        needle(consumer, pose, light, overlay, DIAL_HUMIDITY_X, eased[1], COLOR_HUMIDITY);

        if (gauge.isAttached()) {
            lamp(consumer, pose, light, overlay, gauge.isSealed() ? COLOR_SEALED : COLOR_LEAKING);
        }
        pose.popPose();
    }

    private static int modelAngle(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    private void lamp(VertexConsumer consumer, PoseStack pose, int light, int overlay, int colour) {
        PoseStack.Pose last = pose.last();

        vertex(consumer, last, LAMP_X1, LAMP_Y0, light, overlay, colour, 0.0F, 1.0F);
        vertex(consumer, last, LAMP_X0, LAMP_Y0, light, overlay, colour, 1.0F, 1.0F);
        vertex(consumer, last, LAMP_X0, LAMP_Y1, light, overlay, colour, 1.0F, 0.0F);
        vertex(consumer, last, LAMP_X1, LAMP_Y1, light, overlay, colour, 0.0F, 0.0F);
    }

    private void needle(VertexConsumer consumer, PoseStack pose, int light, int overlay,
                        float dialX, float fill, int colour) {
        pose.pushPose();
        pose.translate(dialX, DIAL_Y, 0.0F);

        pose.mulPose(Axis.ZP.rotationDegrees(SWEEP_START + SWEEP * fill));
        PoseStack.Pose last = pose.last();

        vertex(consumer, last, NEEDLE_HALF_WIDTH, -NEEDLE_TAIL, light, overlay, colour, 0.0F, 1.0F);
        vertex(consumer, last, -NEEDLE_HALF_WIDTH, -NEEDLE_TAIL, light, overlay, colour, 1.0F, 1.0F);
        vertex(consumer, last, -NEEDLE_HALF_WIDTH, NEEDLE_LENGTH, light, overlay, colour, 1.0F, 0.0F);
        vertex(consumer, last, NEEDLE_HALF_WIDTH, NEEDLE_LENGTH, light, overlay, colour, 0.0F, 0.0F);
        pose.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y,
                        int light, int overlay, int colour, float u, float v) {
        consumer.addVertex(pose, x, y, FACE_Z)
                .setColor(colour)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, -1.0F);
    }
}
