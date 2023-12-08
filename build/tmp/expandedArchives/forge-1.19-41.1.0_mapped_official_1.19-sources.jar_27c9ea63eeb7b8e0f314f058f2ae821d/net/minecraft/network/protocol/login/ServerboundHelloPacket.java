package net.minecraft.network.protocol.login;

import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record ServerboundHelloPacket(String name, Optional<ProfilePublicKey.Data> publicKey) implements Packet<ServerLoginPacketListener> {
   public ServerboundHelloPacket(FriendlyByteBuf p_179827_) {
      this(p_179827_.readUtf(16), p_179827_.readOptional(ProfilePublicKey.Data::new));
   }

   public void write(FriendlyByteBuf p_134851_) {
      p_134851_.writeUtf(this.name, 16);
      p_134851_.writeOptional(this.publicKey, (p_238047_, p_238048_) -> {
         p_238048_.write(p_134851_);
      });
   }

   public void handle(ServerLoginPacketListener p_134848_) {
      p_134848_.handleHello(this);
   }
}