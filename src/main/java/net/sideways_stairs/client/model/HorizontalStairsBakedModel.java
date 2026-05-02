package net.sideways_stairs.client.model;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.sideways_stairs.item.HorizontalStairsItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HorizontalStairsBakedModel implements IBakedModel {

    private final IBakedModel geometry;
    private final ItemOverrideList overrides;

    public HorizontalStairsBakedModel(IBakedModel geometry) {
        this.geometry = geometry;
        this.overrides = new HorizontalStairsItemOverrides(this);
    }

    @Nullable
    private static TextureAtlasSprite spriteForState(@Nullable BlockState sourceState) {
        if(sourceState == null) {
            return null;
        }
        try {
            IBakedModel sourceModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(sourceState);
            if(sourceModel != null) {
                return sourceModel.getParticleIcon();
            }
        } catch(Exception ignored) {
        }
        return null;
    }

    @Nullable
    private static TextureAtlasSprite spriteForId(@Nullable ResourceLocation id) {
        if(id == null) {
            return null;
        }
        Block src = ForgeRegistries.BLOCKS.getValue(id);
        if(src == null) {
            return null;
        }
        return spriteForState(src.defaultBlockState());
    }

    @Nullable
    private TextureAtlasSprite resolveSourceSprite(@Nullable IModelData data) {
        if(data == null) {
            return null;
        }
        return spriteForState(data.getData(HorizontalStairsModelData.SOURCE_STATE));
    }

    private static BakedQuad reSprite(BakedQuad original, TextureAtlasSprite newSprite) {
        if(newSprite == null || newSprite == original.getSprite()) {
            return original;
        }

        int[] vertices = original.getVertices().clone();
        TextureAtlasSprite old = original.getSprite();

        for(int i = 0; i < 4; i++) {
            int base = i * 8;
            float u = Float.intBitsToFloat(vertices[base + 4]);
            float v = Float.intBitsToFloat(vertices[base + 5]);

            float uNorm = old.getU0() == old.getU1() ? 0f : (u - old.getU0()) / (old.getU1() - old.getU0());
            float vNorm = old.getV0() == old.getV1() ? 0f : (v - old.getV0()) / (old.getV1() - old.getV0());

            vertices[base + 4] = Float.floatToRawIntBits(newSprite.getU0() + uNorm * (newSprite.getU1() - newSprite.getU0()));
            vertices[base + 5] = Float.floatToRawIntBits(newSprite.getV0() + vNorm * (newSprite.getV1() - newSprite.getV0()));
        }

        return new BakedQuad(vertices, original.getTintIndex(), original.getDirection(), newSprite, original.isShade());
    }

    List<BakedQuad> reSprite(List<BakedQuad> quads, TextureAtlasSprite sprite) {
        if(sprite == null) {
            return quads;
        }
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for(BakedQuad q : quads) result.add(reSprite(q, sprite));
        return result;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = geometry.getQuads(state, side, rand, EmptyModelData.INSTANCE);
        return reSprite(quads, resolveSourceSprite(extraData));
    }

    @Override
    public boolean useAmbientOcclusion() {
        return geometry.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return geometry.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return geometry.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public TextureAtlasSprite getParticleIcon() {
        return geometry.getParticleIcon();
    }

    @Override
    @MethodsReturnNonnullByDefault
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        TextureAtlasSprite sprite = resolveSourceSprite(data);
        return sprite != null ? sprite : geometry.getParticleTexture(data);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemCameraTransforms getTransforms() {
        return geometry.getTransforms();
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemOverrideList getOverrides() {
        return overrides;
    }

    private static final class HorizontalStairsItemOverrides extends ItemOverrideList {

        private final HorizontalStairsBakedModel owner;

        HorizontalStairsItemOverrides(HorizontalStairsBakedModel owner) {
            this.owner = owner;
        }

        @Nullable
        @Override
        public IBakedModel resolve(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            ResourceLocation sourceId = null;
            if(stack.getItem() instanceof HorizontalStairsItem) {
                sourceId = ((HorizontalStairsItem) stack.getItem()).getSourceBlockId();
            }
            // fallback
            if(sourceId == null) {
                CompoundNBT tag = stack.getTagElement("BlockEntityTag");
                if(tag != null && tag.contains("Source")) {
                    try {
                        sourceId = new ResourceLocation(tag.getString("Source"));
                    } catch(Exception ignored) {
                    }
                }
            }

            TextureAtlasSprite sprite = spriteForId(sourceId);
            if(sprite == null) {
                return owner;
            }

            final TextureAtlasSprite finalSprite = sprite;
            final HorizontalStairsBakedModel base = owner;

            return new IBakedModel() {
                @Override
                @MethodsReturnNonnullByDefault
                public List<BakedQuad> getQuads(@Nullable BlockState s, @Nullable Direction side, @Nonnull Random rand) {
                    return base.reSprite(base.geometry.getQuads(s, side, rand, EmptyModelData.INSTANCE), finalSprite);
                }

                @Override
                public boolean useAmbientOcclusion() {
                    return base.useAmbientOcclusion();
                }

                @Override
                public boolean isGui3d() {
                    return base.isGui3d();
                }

                @Override
                public boolean usesBlockLight() {
                    return base.usesBlockLight();
                }

                @Override
                public boolean isCustomRenderer() {
                    return false;
                }

                @Override
                @MethodsReturnNonnullByDefault
                public TextureAtlasSprite getParticleIcon() {
                    return finalSprite;
                }

                @Override
                @MethodsReturnNonnullByDefault
                public ItemCameraTransforms getTransforms() {
                    return base.getTransforms();
                }

                @Override
                @MethodsReturnNonnullByDefault
                public ItemOverrideList getOverrides() {
                    return ItemOverrideList.EMPTY;
                }
            };
        }
    }
}
