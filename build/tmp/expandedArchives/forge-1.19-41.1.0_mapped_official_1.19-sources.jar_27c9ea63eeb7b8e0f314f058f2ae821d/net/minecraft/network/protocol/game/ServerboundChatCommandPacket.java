package net.minecraft.network.protocol.game;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.StringUtil;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, ArgumentSignatures argumentSignatures, boolean signedPreview) implements Packet<ServerGamePacketListener> {
   public ServerboundChatCommandPacket {
      command = StringUtil.trimChatMessage(command);
   }

   public ServerboundChatCommandPacket(FriendlyByteBuf p_237932_) {
      this(p_237932_.readUtf(256), p_237932_.readInstant(), new ArgumentSignatures(p_237932_), p_237932_.readBoolean());
   }

   public void write(FriendlyByteBuf p_237936_) {
      p_237936_.writeUtf(this.command, 256);
      p_237936_.writeInstant(this.timeStamp);
      this.argumentSignatures.write(p_237936_);
      p_237936_.writeBoolean(this.signedPreview);
   }

   public void handle(ServerGamePacketListener p_237940_) {
      p_237940_.handleChatCommand(this);
   }

   public CommandSigningContext signingContext(UUID p_237934_) {
      return new CommandSigningContext.SignedArguments(p_237934_, this.timeStamp, this.argumentSignatures, this.signedPreview);
   }
}