// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.math.MathHelper;

public class DefaultKneelFallAnimation implements DeathPoseAnimation {
    public static final String ID = "default_kneel_fall";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void apply(PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float deathTicks) {
        if (deathTicks < 10.0f) {
            return; // Freeze stage: keep current pose.
        }

        ModelPart body = model.body;
        ModelPart head = model.head;
        ModelPart rightArm = model.rightArm;
        ModelPart leftArm = model.leftArm;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftLeg = model.leftLeg;

        float kneelProgress = stageProgress(deathTicks, 10.0f, 25.0f);
        if (kneelProgress > 0.0f) {
            body.pitch = MathHelper.lerp(kneelProgress, body.pitch, 0.25f);
            rightLeg.pitch = MathHelper.lerp(kneelProgress, rightLeg.pitch, -1.05f);
            leftLeg.pitch = MathHelper.lerp(kneelProgress, leftLeg.pitch, -1.05f);
            head.pitch = MathHelper.lerp(kneelProgress, head.pitch, 0.45f);
        }

        float fallProgress = stageProgress(deathTicks, 25.0f, 40.0f);
        if (fallProgress > 0.0f) {
            body.pitch = MathHelper.lerp(fallProgress, body.pitch, 1.55f);
            head.pitch = MathHelper.lerp(fallProgress, head.pitch, 0.0f);
            rightLeg.pitch = MathHelper.lerp(fallProgress, rightLeg.pitch, 0.35f);
            leftLeg.pitch = MathHelper.lerp(fallProgress, leftLeg.pitch, 0.35f);
        }

        float spreadProgress = stageProgress(deathTicks, 40.0f, 60.0f);
        if (spreadProgress > 0.0f) {
            rightArm.roll = MathHelper.lerp(spreadProgress, rightArm.roll, 1.2f);
            leftArm.roll = MathHelper.lerp(spreadProgress, leftArm.roll, -1.2f);
            rightArm.pitch = MathHelper.lerp(spreadProgress, rightArm.pitch, 0.15f);
            leftArm.pitch = MathHelper.lerp(spreadProgress, leftArm.pitch, 0.15f);
            rightLeg.roll = MathHelper.lerp(spreadProgress, rightLeg.roll, 0.3f);
            leftLeg.roll = MathHelper.lerp(spreadProgress, leftLeg.roll, -0.3f);
        }
    }

    private float stageProgress(float value, float start, float end) {
        return MathHelper.clamp((value - start) / (end - start), 0.0f, 1.0f);
    }
}
