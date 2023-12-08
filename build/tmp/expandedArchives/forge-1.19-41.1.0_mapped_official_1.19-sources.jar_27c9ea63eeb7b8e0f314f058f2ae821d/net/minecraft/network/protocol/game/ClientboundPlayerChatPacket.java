package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;

public record ClientboundPlayerChatPacket(Component signedContent, Optional<Component> unsignedContent, int typeId, ChatSender sender, Instant timeStamp, Crypt.SaltSignaturePair saltSignature) implements Packet<ClientGamePacketListener> {
   private static final Duration MESSAGE_EXPIRES_AFTER = ServerboundChatPacket.MESSAGE_EXPIRES_AFTER.plus(Duration.ofMinutes(2L));

   public ClientboundPlayerChatPacket(FriendlyByteBuf p_237741_) {
      this(p_237741_.readComponent(), p_237741_.readOptional(FriendlyByteBuf::readComponent), p_237741_.readVarInt(), new ChatSender(p_237741_), p_237741_.readInstant(), new Crypt.SaltSignaturePair(p_237741_));
   }

   public void write(FriendlyByteBuf p_237755_) {
      p_237755_.writeComponent(this.signedContent);
      p_237755_.writeOptional(this.unsignedContent, FriendlyByteBuf::writeComponent);
      p_237755_.writeVarInt(this.typeId);
      this.sender.write(p_237755_);
      p_237755_.writeInstant(this.timeStamp);
      Crypt.SaltSignaturePair.write(p_237755_, this.saltSignature);
   }

   public void handle(ClientGamePacketListener p_237759_) {
      p_237759_.handlePlayerChat(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public PlayerChatMessage getMessage() {
      MessageSignature messagesignature = new MessageSignature(this.sender.uuid(), this.timeStamp, this.saltSignature);
      return new PlayerChatMessage(this.signedContent, messagesignature, this.unsignedContent);
   }

   private Instant getExpiresAt() {
      return this.timeStamp.plus(MESSAGE_EXPIRES_AFTER);
   }

   public boolean hasExpired(Instant p_237753_) {
      return p_237753_.isAfter(this.getExpiresAt());
   }

   public ChatType resolveType(Registry<ChatType> p_237751_) {
      return Objects.requireNonNull(p_237751_.byId(this.typeId), "Invalid chat type");
   }
}