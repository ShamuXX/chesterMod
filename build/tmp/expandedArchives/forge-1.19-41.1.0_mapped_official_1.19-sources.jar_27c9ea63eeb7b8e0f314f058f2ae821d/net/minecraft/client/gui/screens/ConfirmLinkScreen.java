package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmLinkScreen extends ConfirmScreen {
   private static final Component COPY_BUTTON_TEXT = Component.translatable("chat.copy");
   private static final Component WARNING_TEXT = Component.translatable("chat.link.warning");
   private final String url;
   private final boolean showWarning;

   public ConfirmLinkScreen(BooleanConsumer p_238329_, Component p_238330_, String p_238331_, boolean p_238332_) {
      super(p_238329_, p_238330_, Component.translatable(p_238332_ ? "chat.link.confirmTrusted" : "chat.link.confirm").append(" ").append(Component.literal(p_238331_)));
      this.yesButton = (Component)(p_238332_ ? Component.translatable("chat.link.open") : CommonComponents.GUI_YES);
      this.noButton = p_238332_ ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO;
      this.showWarning = !p_238332_;
      this.url = p_238331_;
   }

   public ConfirmLinkScreen(BooleanConsumer p_95631_, String p_95632_, boolean p_95633_) {
      super(p_95631_, Component.translatable(p_95633_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), Component.literal(p_95632_));
      this.yesButton = (Component)(p_95633_ ? Component.translatable("chat.link.open") : CommonComponents.GUI_YES);
      this.noButton = p_95633_ ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO;
      this.showWarning = !p_95633_;
      this.url = p_95632_;
   }

   protected void addButtons(int p_169243_) {
      this.addRenderableWidget(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, (p_169249_) -> {
         this.callback.accept(true);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, COPY_BUTTON_TEXT, (p_169247_) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, (p_169245_) -> {
         this.callback.accept(false);
      }));
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public void render(PoseStack p_95635_, int p_95636_, int p_95637_, float p_95638_) {
      super.render(p_95635_, p_95636_, p_95637_, p_95638_);
      if (this.showWarning) {
         drawCenteredString(p_95635_, this.font, WARNING_TEXT, this.width / 2, 110, 16764108);
      }

   }
}