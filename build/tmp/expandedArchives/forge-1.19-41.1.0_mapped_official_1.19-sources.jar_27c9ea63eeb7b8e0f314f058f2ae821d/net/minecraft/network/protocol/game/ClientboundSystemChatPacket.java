package net.minecraft.network.protocol.game;

import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(Component content, int typeId) implements Packet<ClientGamePacketListener> {
   public ClientboundSystemChatPacket(FriendlyByteBuf p_237852_) {
      this(p_237852_.readComponent(), p_237852_.readVarInt());
   }

   public void write(FriendlyByteBuf p_237860_) {
      p_237860_.writeComponent(this.content);
      p_237860_.writeVarInt(this.typeId);
   }

   public void handle(ClientGamePacketListener p_237864_) {
      p_237864_.handleSystemChat(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public ChatType resolveType(Registry<ChatType> p_237858_) {
      return Objects.requireNonNull(p_237858_.byId(this.typeId), "Invalid chat type");
   }
}