package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatPreviewWarningScreen extends WarningScreen {
   private static final Component TITLE = Component.translatable("chatPreview.warning.title").withStyle(ChatFormatting.BOLD);
   private static final Component CONTENT = Component.translatable("chatPreview.warning.content");
   private static final Component CHECK = Component.translatable("chatPreview.warning.check");
   private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
   private final ServerData serverData;
   @Nullable
   private final Screen lastScreen;

   public ChatPreviewWarningScreen(@Nullable Screen p_232837_, ServerData p_232838_) {
      super(TITLE, CONTENT, CHECK, NARRATION);
      this.serverData = p_232838_;
      this.lastScreen = p_232837_;
   }

   protected void initButtons(int p_232840_) {
      this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + p_232840_, 150, 20, Component.translatable("menu.disconnect"), (p_232846_) -> {
         this.minecraft.level.disconnect();
         this.minecraft.clearLevel();
         this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, 100 + p_232840_, 150, 20, CommonComponents.GUI_PROCEED, (p_232842_) -> {
         this.updateOptions();
         this.onClose();
      }));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void updateOptions() {
      if (this.stopShowing != null && this.stopShowing.selected()) {
         ServerData.ChatPreview serverdata$chatpreview = this.serverData.getChatPreview();
         if (serverdata$chatpreview != null) {
            serverdata$chatpreview.acknowledge();
            ServerList.saveSingleServer(this.serverData);
         }
      }

   }

   protected int getLineHeight() {
      return 9 * 3 / 2;
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}