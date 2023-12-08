package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.StringUtil;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
   public static final Duration MESSAGE_EXPIRES_AFTER = Duration.ofMinutes(5L);
   private final String message;
   private final Instant timeStamp;
   private final Crypt.SaltSignaturePair saltSignature;
   private final boolean signedPreview;

   public ServerboundChatPacket(String p_237955_, MessageSignature p_237956_, boolean p_237957_) {
      this.message = StringUtil.trimChatMessage(p_237955_);
      this.timeStamp = p_237956_.timeStamp();
      this.saltSignature = p_237956_.saltSignature();
      this.signedPreview = p_237957_;
   }

   public ServerboundChatPacket(FriendlyByteBuf p_179545_) {
      this.message = p_179545_.readUtf(256);
      this.timeStamp = p_179545_.readInstant();
      this.saltSignature = new Crypt.SaltSignaturePair(p_179545_);
      this.signedPreview = p_179545_.readBoolean();
   }

   public void write(FriendlyByteBuf p_133839_) {
      p_133839_.writeUtf(this.message, 256);
      p_133839_.writeInstant(this.timeStamp);
      Crypt.SaltSignaturePair.write(p_133839_, this.saltSignature);
      p_133839_.writeBoolean(this.signedPreview);
   }

   public void handle(ServerGamePacketListener p_133836_) {
      p_133836_.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }

   public MessageSignature getSignature(UUID p_237959_) {
      return new MessageSignature(p_237959_, this.timeStamp, this.saltSignature);
   }

   public Instant getTimeStamp() {
      return this.timeStamp;
   }

   public boolean signedPreview() {
      return this.signedPreview;
   }
}