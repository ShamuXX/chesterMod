package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.ClientChatPreview;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.PreviewedArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ChatScreen extends Screen {
   public static final double MOUSE_SCROLL_SPEED = 7.0D;
   private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");
   private static final int PREVIEW_MARGIN_SIDES = 2;
   private static final int PREVIEW_PADDING = 2;
   private static final int PREVIEW_MARGIN_BOTTOM = 15;
   private static final Component PREVIEW_WARNING_TITLE = Component.translatable("chatPreview.warning.toast.title");
   private static final Component PREVIEW_WARNING_TOAST = Component.translatable("chatPreview.warning.toast");
   private static final Component PREVIEW_HINT = Component.translatable("chat.preview").withStyle(ChatFormatting.DARK_GRAY);
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private String initial;
   CommandSuggestions commandSuggestions;
   private ClientChatPreview chatPreview;

   public ChatScreen(String p_95579_) {
      super(Component.translatable("chat_screen.title"));
      this.initial = p_95579_;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, Component.translatable("chat.editBox")) {
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.addWidget(this.input);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestions.updateCommandInfo();
      this.setInitialFocus(this.input);
      this.chatPreview = new ClientChatPreview(this.minecraft);
      this.updateChatPreview(this.input.getValue());
      ServerData serverdata = this.minecraft.getCurrentServer();
      if (serverdata != null && this.minecraft.options.chatPreview().get()) {
         ServerData.ChatPreview serverdata$chatpreview = serverdata.getChatPreview();
         if (serverdata$chatpreview != null && serverdata.previewsChat() && serverdata$chatpreview.showToast()) {
            ServerList.saveSingleServer(serverdata);
            SystemToast systemtoast = SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.CHAT_PREVIEW_WARNING, PREVIEW_WARNING_TITLE, PREVIEW_WARNING_TOAST);
            this.minecraft.getToasts().addToast(systemtoast);
         }
      }

   }

   public void resize(Minecraft p_95600_, int p_95601_, int p_95602_) {
      String s = this.input.getValue();
      this.init(p_95600_, p_95601_, p_95602_);
      this.setChatLine(s);
      this.commandSuggestions.updateCommandInfo();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   public void tick() {
      this.input.tick();
      this.chatPreview.tick();
   }

   private void onEdited(String p_95611_) {
      String s = this.input.getValue();
      this.commandSuggestions.setAllowSuggestions(!s.equals(this.initial));
      this.commandSuggestions.updateCommandInfo();
      this.updateChatPreview(s);
   }

   private void updateChatPreview(String p_232719_) {
      String s = this.normalizeChatMessage(p_232719_);
      if (this.sendsChatPreviewRequests()) {
         this.requestPreview(s);
      } else {
         this.chatPreview.disable();
      }

   }

   private void requestPreview(String p_232721_) {
      if (p_232721_.startsWith("/")) {
         this.requestCommandArgumentPreview(p_232721_);
      } else {
         this.requestChatMessagePreview(p_232721_);
      }

   }

   private void requestChatMessagePreview(String p_232723_) {
      this.chatPreview.update(p_232723_);
   }

   private void requestCommandArgumentPreview(String p_232725_) {
      CommandNode<SharedSuggestionProvider> commandnode = this.commandSuggestions.getNodeAt(this.input.getCursorPosition());
      if (commandnode != null && PreviewedArgument.isPreviewed(commandnode)) {
         this.chatPreview.update(p_232725_);
      } else {
         this.chatPreview.disable();
      }

   }

   private boolean sendsChatPreviewRequests() {
      if (this.minecraft.player == null) {
         return false;
      } else if (!this.minecraft.options.chatPreview().get()) {
         return false;
      } else {
         ServerData serverdata = this.minecraft.getCurrentServer();
         return serverdata != null && serverdata.previewsChat();
      }
   }

   public boolean keyPressed(int p_95591_, int p_95592_, int p_95593_) {
      if (this.commandSuggestions.keyPressed(p_95591_, p_95592_, p_95593_)) {
         return true;
      } else if (super.keyPressed(p_95591_, p_95592_, p_95593_)) {
         return true;
      } else if (p_95591_ == 256) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else if (p_95591_ != 257 && p_95591_ != 335) {
         if (p_95591_ == 265) {
            this.moveInHistory(-1);
            return true;
         } else if (p_95591_ == 264) {
            this.moveInHistory(1);
            return true;
         } else if (p_95591_ == 266) {
            this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            return true;
         } else if (p_95591_ == 267) {
            this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            return true;
         } else {
            return false;
         }
      } else {
         this.handleChatInput(this.input.getValue(), true);
         this.minecraft.setScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double p_95581_, double p_95582_, double p_95583_) {
      p_95583_ = Mth.clamp(p_95583_, -1.0D, 1.0D);
      if (this.commandSuggestions.mouseScrolled(p_95583_)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            p_95583_ *= 7.0D;
         }

         this.minecraft.gui.getChat().scrollChat((int)p_95583_);
         return true;
      }
   }

   public boolean mouseClicked(double p_95585_, double p_95586_, int p_95587_) {
      if (this.commandSuggestions.mouseClicked((double)((int)p_95585_), (double)((int)p_95586_), p_95587_)) {
         return true;
      } else {
         if (p_95587_ == 0) {
            ChatComponent chatcomponent = this.minecraft.gui.getChat();
            if (chatcomponent.handleChatQueueClicked(p_95585_, p_95586_)) {
               return true;
            }

            Style style = this.getComponentStyleAt(p_95585_, p_95586_);
            if (style != null && this.handleComponentClicked(style)) {
               this.initial = this.input.getValue();
               return true;
            }
         }

         return this.input.mouseClicked(p_95585_, p_95586_, p_95587_) ? true : super.mouseClicked(p_95585_, p_95586_, p_95587_);
      }
   }

   protected void insertText(String p_95606_, boolean p_95607_) {
      if (p_95607_) {
         this.input.setValue(p_95606_);
      } else {
         this.input.insertText(p_95606_);
      }

   }

   public void moveInHistory(int p_95589_) {
      int i = this.historyPos + p_95589_;
      int j = this.minecraft.gui.getChat().getRecentChat().size();
      i = Mth.clamp(i, 0, j);
      if (i != this.historyPos) {
         if (i == j) {
            this.historyPos = j;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == j) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get(i));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = i;
         }
      }
   }

   public void render(PoseStack p_95595_, int p_95596_, int p_95597_, float p_95598_) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(p_95595_, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
      this.input.render(p_95595_, p_95596_, p_95597_, p_95598_);
      if (this.chatPreview.isEnabled()) {
         this.renderChatPreview(p_95595_);
      } else {
         this.commandSuggestions.render(p_95595_, p_95596_, p_95597_);
      }

      Style style = this.getComponentStyleAt((double)p_95596_, (double)p_95597_);
      if (style != null && style.getHoverEvent() != null) {
         this.renderComponentHoverEffect(p_95595_, style, p_95596_, p_95597_);
      }

      super.render(p_95595_, p_95596_, p_95597_, p_95598_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String p_95613_) {
      this.input.setValue(p_95613_);
   }

   protected void updateNarrationState(NarrationElementOutput p_169238_) {
      p_169238_.add(NarratedElementType.TITLE, this.getTitle());
      p_169238_.add(NarratedElementType.USAGE, USAGE_TEXT);
      String s = this.input.getValue();
      if (!s.isEmpty()) {
         p_169238_.nest().add(NarratedElementType.TITLE, Component.translatable("chat_screen.message", s));
      }

   }

   public void renderChatPreview(PoseStack p_232705_) {
      int i = (int)(255.0D * (this.minecraft.options.chatOpacity().get() * (double)0.9F + (double)0.1F));
      int j = (int)(255.0D * this.minecraft.options.textBackgroundOpacity().get());
      int k = this.chatPreviewWidth();
      List<FormattedCharSequence> list = this.peekChatPreview();
      int l = this.chatPreviewHeight(list);
      RenderSystem.enableBlend();
      p_232705_.pushPose();
      p_232705_.translate((double)this.chatPreviewLeft(), (double)this.chatPreviewTop(l), 0.0D);
      fill(p_232705_, 0, 0, k, l, j << 24);
      p_232705_.translate(2.0D, 2.0D, 0.0D);

      for(int i1 = 0; i1 < list.size(); ++i1) {
         FormattedCharSequence formattedcharsequence = list.get(i1);
         this.minecraft.font.drawShadow(p_232705_, formattedcharsequence, 0.0F, (float)(i1 * 9), i << 24 | 16777215);
      }

      p_232705_.popPose();
      RenderSystem.disableBlend();
   }

   @Nullable
   private Style getComponentStyleAt(double p_232702_, double p_232703_) {
      Style style = this.minecraft.gui.getChat().getClickedComponentStyleAt(p_232702_, p_232703_);
      if (style == null) {
         style = this.getChatPreviewStyleAt(p_232702_, p_232703_);
      }

      return style;
   }

   @Nullable
   private Style getChatPreviewStyleAt(double p_232716_, double p_232717_) {
      if (this.minecraft.options.hideGui) {
         return null;
      } else {
         List<FormattedCharSequence> list = this.peekChatPreview();
         int i = this.chatPreviewHeight(list);
         if (!(p_232716_ < (double)this.chatPreviewLeft()) && !(p_232716_ > (double)this.chatPreviewRight()) && !(p_232717_ < (double)this.chatPreviewTop(i)) && !(p_232717_ > (double)this.chatPreviewBottom())) {
            int j = this.chatPreviewLeft() + 2;
            int k = this.chatPreviewTop(i) + 2;
            int l = (Mth.floor(p_232717_) - k) / 9;
            if (l >= 0 && l < list.size()) {
               FormattedCharSequence formattedcharsequence = list.get(l);
               return this.minecraft.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int)(p_232716_ - (double)j));
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private List<FormattedCharSequence> peekChatPreview() {
      Component component = this.chatPreview.peek();
      return component != null ? this.font.split(component, this.chatPreviewWidth()) : List.of(PREVIEW_HINT.getVisualOrderText());
   }

   private int chatPreviewWidth() {
      return this.minecraft.screen.width - 4;
   }

   private int chatPreviewHeight(List<FormattedCharSequence> p_232714_) {
      return Math.max(p_232714_.size(), 1) * 9 + 4;
   }

   private int chatPreviewBottom() {
      return this.minecraft.screen.height - 15;
   }

   private int chatPreviewTop(int p_232709_) {
      return this.chatPreviewBottom() - p_232709_;
   }

   private int chatPreviewLeft() {
      return 2;
   }

   private int chatPreviewRight() {
      return this.minecraft.screen.width - 2;
   }

   public void handleChatInput(String p_232711_, boolean p_232712_) {
      p_232711_ = this.normalizeChatMessage(p_232711_);
      if (!p_232711_.isEmpty()) {
         if (p_232712_) {
            this.minecraft.gui.getChat().addRecentChat(p_232711_);
         }

         Component component = this.chatPreview.pull(p_232711_);
         if (p_232711_.startsWith("/")) {
            this.minecraft.player.command(p_232711_.substring(1), component);
         } else {
            this.minecraft.player.chat(p_232711_, component);
         }

      }
   }

   public String normalizeChatMessage(String p_232707_) {
      return StringUtils.normalizeSpace(p_232707_.trim());
   }

   public ClientChatPreview getChatPreview() {
      return this.chatPreview;
   }
}