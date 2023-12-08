package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayChatListener implements ChatListener {
   private final Minecraft minecraft;

   public OverlayChatListener(Minecraft p_93352_) {
      this.minecraft = p_93352_;
   }

   public void handle(ChatType p_232454_, Component p_232455_, @Nullable ChatSender p_232456_) {
      p_232454_.overlay().ifPresent((p_232460_) -> {
         Component component = p_232460_.decorate(p_232455_, p_232456_);
         this.minecraft.gui.setOverlayMessage(component, false);
      });
   }
}