package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ClientChatPreview {
   private static final long PREVIEW_VALID_AFTER_MS = 200L;
   private boolean enabled;
   @Nullable
   private String lastQuery;
   @Nullable
   private String scheduledRequest;
   private final ChatPreviewRequests requests;
   @Nullable
   private ClientChatPreview.Preview preview;

   public ClientChatPreview(Minecraft p_232411_) {
      this.requests = new ChatPreviewRequests(p_232411_);
   }

   public void tick() {
      String s = this.scheduledRequest;
      if (s != null && this.requests.trySendRequest(s, Util.getMillis())) {
         this.scheduledRequest = null;
      }

   }

   public void update(String p_232417_) {
      this.enabled = true;
      p_232417_ = normalizeQuery(p_232417_);
      if (!p_232417_.isEmpty()) {
         if (!p_232417_.equals(this.lastQuery)) {
            this.lastQuery = p_232417_;
            this.sendOrScheduleRequest(p_232417_);
         }
      } else {
         this.clear();
      }

   }

   private void sendOrScheduleRequest(String p_232423_) {
      if (!this.requests.trySendRequest(p_232423_, Util.getMillis())) {
         this.scheduledRequest = p_232423_;
      } else {
         this.scheduledRequest = null;
      }

   }

   public void disable() {
      this.enabled = false;
      this.clear();
   }

   private void clear() {
      this.lastQuery = null;
      this.scheduledRequest = null;
      this.preview = null;
      this.requests.clear();
   }

   public void handleResponse(int p_232414_, @Nullable Component p_232415_) {
      String s = this.requests.handleResponse(p_232414_);
      if (s != null) {
         Component component = (Component)(p_232415_ != null ? p_232415_ : Component.literal(s));
         this.preview = new ClientChatPreview.Preview(Util.getMillis(), s, component);
      }

   }

   @Nullable
   public Component peek() {
      return Util.mapNullable(this.preview, ClientChatPreview.Preview::response);
   }

   @Nullable
   public Component pull(String p_232420_) {
      if (this.preview != null && this.preview.canPull(p_232420_)) {
         Component component = this.preview.response();
         this.preview = null;
         return component;
      } else {
         return null;
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   static String normalizeQuery(String p_232426_) {
      return StringUtils.normalizeSpace(p_232426_.trim());
   }

   @OnlyIn(Dist.CLIENT)
   static record Preview(long receivedTimeStamp, String query, @Nullable Component response) {
      public Preview {
         query = ClientChatPreview.normalizeQuery(query);
      }

      public boolean canPull(String p_232437_) {
         if (this.query.equals(ClientChatPreview.normalizeQuery(p_232437_))) {
            long i = this.receivedTimeStamp + 200L;
            return Util.getMillis() >= i;
         } else {
            return false;
         }
      }
   }
}