package com.impossibles5.heatandsteam.client;

import com.impossibles5.heatandsteam.wood.ChoppingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ChoppingBlockRenderer implements BlockEntityRenderer<ChoppingBlockEntity> {
    private static final float STUMP_TOP = 12.0F / 16.0F;

    private static final float SCALE = 0.45F;

    private final ItemRenderer items;

    public ChoppingBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.items = context.getItemRenderer();
    }

    @Override
    public AABB getRenderBoundingBox(ChoppingBlockEntity chopping) {
        return new AABB(chopping.getBlockPos()).expandTowards(0.0, 0.5, 0.0);
    }

    @Override
    public void render(ChoppingBlockEntity chopping, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        ItemStack log = chopping.getLog();
        if (log.isEmpty()) {
            return;
        }
        pose.pushPose();

        pose.translate(0.5F, STUMP_TOP + SCALE / 2.0F, 0.5F);
        pose.scale(SCALE, SCALE, SCALE);

        pose.mulPose(Axis.YP.rotationDegrees(22.5F));
        items.renderStatic(log, ItemDisplayContext.NONE, light, overlay, pose, buffers,
                chopping.getLevel(), (int) chopping.getBlockPos().asLong());
        pose.popPose();
    }
}
