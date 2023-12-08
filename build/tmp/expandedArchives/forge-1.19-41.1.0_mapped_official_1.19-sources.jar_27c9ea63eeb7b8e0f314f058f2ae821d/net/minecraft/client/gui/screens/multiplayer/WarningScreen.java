package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WarningScreen extends Screen {
   private final Component titleComponent;
   private final Component content;
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component p_232852_, Component p_232853_, Component p_232854_, Component p_232855_) {
      super(NarratorChatListener.NO_TITLE);
      this.titleComponent = p_232852_;
      this.content = p_232853_;
      this.check = p_232854_;
      this.narration = p_232855_;
   }

   protected abstract void initButtons(int p_210922_);

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.content, this.width - 100);
      int i = (this.message.getLineCount() + 1) * this.getLineHeight();
      int j = this.font.width(this.check);
      this.stopShowing = new Checkbox(this.width / 2 - j / 2 - 8, 76 + i, j + 24, 20, this.check, false);
      this.addRenderableWidget(this.stopShowing);
      this.initButtons(i);
   }

   public Component getNarrationMessage() {
      return this.narration;
   }

   public void render(PoseStack p_210924_, int p_210925_, int p_210926_, float p_210927_) {
      this.renderBackground(p_210924_);
      drawString(p_210924_, this.font, this.titleComponent, 25, 30, 16777215);
      int i = this.width / 2 - this.message.getWidth() / 2;
      this.message.renderLeftAligned(p_210924_, i, 70, this.getLineHeight(), 16777215);
      super.render(p_210924_, p_210925_, p_210926_, p_210927_);
   }

   protected int getLineHeight() {
      return 9 * 2;
   }
}