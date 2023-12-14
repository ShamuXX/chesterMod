package com.docs.chestermod.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.docs.chestermod.Chestermod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class InitItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
                        ForgeRegistries.ITEMS, Chestermod.MODID);

        public static final RegistryObject<Item> EYEBONE = ITEMS.register(
                        "eye_bone", () -> new Item(new Item.Properties()
                                        .tab(CreativeModeTab.TAB_MISC)
                                        .rarity(Rarity.RARE)
                                        .fireResistant()
                                        .stacksTo(0)));

}
