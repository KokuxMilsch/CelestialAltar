package com.kokuxmilsch.celestialaltar.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.misc.RitualType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CelestialAltarRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int glowStone;
    private final RitualType ritual;

    public CelestialAltarRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, int glowStone, RitualType ritual) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.glowStone = glowStone;
        this.ritual = ritual;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return recipeItems.get(0).test(pContainer.getItem(2)) && recipeItems.get(1).test(pContainer.getItem(1));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public int getGlowstone() {
        return this.glowStone;
    }

    public RitualType getRitualType() {
        return this.ritual;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CelestialAltarRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "celestial_altar_ritual";
    }


    public static class Serializer implements RecipeSerializer<CelestialAltarRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CelestialAltar.MODID, Type.ID);

        @Override
        public CelestialAltarRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(2, Ingredient.EMPTY);


            int glowStone = GsonHelper.getAsInt(pSerializedRecipe, "glowstoneCharges")*4;
            //Fallback to max if it gets exceeded
            if(glowStone > 16) {
                glowStone = 16;
            }

            RitualType ritual = RitualType.EMPTY;
            String ritualName = GsonHelper.getAsString(pSerializedRecipe, "ritual_type", "empty");
            RitualType[] ritualTypes = RitualType.values();
            for (int i = 0; i < ritualTypes.length; i++) {
                if(ritualTypes[i].id.equals(ritualName)) {
                    ritual = ritualTypes[i];
                }
            }


            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new CelestialAltarRecipe(pRecipeId, output, inputs, glowStone, ritual);
        }

        @Override
        public @Nullable CelestialAltarRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            int glowStone = buf.readByte();

            RitualType ritual = buf.readEnum(RitualType.class);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new CelestialAltarRecipe(id, output, inputs, glowStone, ritual);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CelestialAltarRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            buf.writeByte(recipe.getGlowstone());
            buf.writeEnum(recipe.getRitualType());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(RegistryAccess.EMPTY), false);
        }
    }
}
