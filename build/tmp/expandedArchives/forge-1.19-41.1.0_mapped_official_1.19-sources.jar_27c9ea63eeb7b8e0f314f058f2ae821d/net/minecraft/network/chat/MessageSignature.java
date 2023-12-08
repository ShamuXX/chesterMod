package net.minecraft.network.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.util.Crypt;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(UUID sender, Instant timeStamp, Crypt.SaltSignaturePair saltSignature) {
   public static MessageSignature unsigned() {
      return new MessageSignature(Util.NIL_UUID, Instant.now(), Crypt.SaltSignaturePair.EMPTY);
   }

   public boolean verify(SignatureValidator p_237154_, Component p_237155_) {
      return this.isValid() ? p_237154_.validate((p_237160_) -> {
         updateSignature(p_237160_, p_237155_, this.sender, this.timeStamp, this.saltSignature.salt());
      }, this.saltSignature.signature()) : false;
   }

   public boolean verify(SignatureValidator p_237151_, String p_237152_) throws SignatureException {
      return this.verify(p_237151_, Component.literal(p_237152_));
   }

   public static void updateSignature(SignatureUpdater.Output p_237145_, Component p_237146_, UUID p_237147_, Instant p_237148_, long p_237149_) throws SignatureException {
      byte[] abyte = new byte[32];
      ByteBuffer bytebuffer = ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN);
      bytebuffer.putLong(p_237149_);
      bytebuffer.putLong(p_237147_.getMostSignificantBits()).putLong(p_237147_.getLeastSignificantBits());
      bytebuffer.putLong(p_237148_.getEpochSecond());
      p_237145_.update(abyte);
      p_237145_.update(encodeContent(p_237146_));
   }

   private static byte[] encodeContent(Component p_237157_) {
      String s = Component.Serializer.toStableJson(p_237157_);
      return s.getBytes(StandardCharsets.UTF_8);
   }

   public boolean isValid() {
      return this.sender != Util.NIL_UUID && this.saltSignature.isValid();
   }

   public boolean isValid(UUID p_238434_) {
      return this.isValid() && p_238434_.equals(this.sender);
   }
}