package net.minecraft.network.chat;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;

public record ChatSender(UUID uuid, Component name, @Nullable Component teamName) {
   public ChatSender(UUID p_236984_, Component p_236985_) {
      this(p_236984_, p_236985_, (Component)null);
   }

   public ChatSender(FriendlyByteBuf p_236991_) {
      this(p_236991_.readUUID(), p_236991_.readComponent(), p_236991_.readNullable(FriendlyByteBuf::readComponent));
   }

   public static ChatSender system(Component p_236996_) {
      return new ChatSender(Util.NIL_UUID, p_236996_);
   }

   public void write(FriendlyByteBuf p_236994_) {
      p_236994_.writeUUID(this.uuid);
      p_236994_.writeComponent(this.name);
      p_236994_.writeNullable(this.teamName, FriendlyByteBuf::writeComponent);
   }

   public ChatSender withTeamName(Component p_236999_) {
      return new ChatSender(this.uuid, this.name, p_236999_);
   }
}