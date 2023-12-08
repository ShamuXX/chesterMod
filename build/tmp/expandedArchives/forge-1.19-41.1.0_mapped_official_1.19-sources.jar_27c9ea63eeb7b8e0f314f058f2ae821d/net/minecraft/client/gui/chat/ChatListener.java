package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ChatListener {
   void handle(ChatType p_232364_, Component p_232365_, @Nullable ChatSender p_232366_);
}