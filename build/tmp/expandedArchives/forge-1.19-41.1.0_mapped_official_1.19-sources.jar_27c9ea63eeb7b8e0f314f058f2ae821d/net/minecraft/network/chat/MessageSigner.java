package net.minecraft.network.chat;

import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.util.Crypt;
import net.minecraft.util.Signer;

public record MessageSigner(UUID sender, Instant timeStamp, long salt) {
   public static MessageSigner create(UUID p_237184_) {
      return new MessageSigner(p_237184_, Instant.now(), Crypt.SaltSupplier.getLong());
   }

   public MessageSignature sign(Signer p_237181_, Component p_237182_) {
      byte[] abyte = p_237181_.sign((p_237187_) -> {
         MessageSignature.updateSignature(p_237187_, p_237182_, this.sender, this.timeStamp, this.salt);
      });
      return new MessageSignature(this.sender, this.timeStamp, new Crypt.SaltSignaturePair(this.salt, abyte));
   }

   public MessageSignature sign(Signer p_237178_, String p_237179_) throws SignatureException {
      return this.sign(p_237178_, Component.literal(p_237179_));
   }
}