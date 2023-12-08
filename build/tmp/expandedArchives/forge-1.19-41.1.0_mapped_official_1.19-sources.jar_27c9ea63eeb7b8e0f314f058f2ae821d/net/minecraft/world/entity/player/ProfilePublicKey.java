package net.minecraft.world.entity.player;

import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(ProfilePublicKey.Data data) {
   public static final Codec<ProfilePublicKey> TRUSTED_CODEC = ProfilePublicKey.Data.CODEC.comapFlatMap((p_219793_) -> {
      try {
         return DataResult.success(createTrusted(p_219793_));
      } catch (CryptException cryptexception) {
         return DataResult.error("Malformed public key");
      }
   }, ProfilePublicKey::data);

   public static ProfilePublicKey createTrusted(ProfilePublicKey.Data p_219790_) throws CryptException {
      return new ProfilePublicKey(p_219790_);
   }

   public static ProfilePublicKey createValidated(SignatureValidator p_219787_, ProfilePublicKey.Data p_219788_) throws InsecurePublicKeyException, CryptException {
      if (p_219788_.hasExpired()) {
         throw new InsecurePublicKeyException.InvalidException("Expired profile public key");
      } else if (!p_219788_.validateSignature(p_219787_)) {
         throw new InsecurePublicKeyException.InvalidException("Invalid profile public key signature");
      } else {
         return createTrusted(p_219788_);
      }
   }

   public SignatureValidator createSignatureValidator() {
      return SignatureValidator.from(this.data.key, "SHA256withRSA");
   }

   public static record Data(Instant expiresAt, PublicKey key, byte[] keySignature) {
      private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
      public static final Codec<ProfilePublicKey.Data> CODEC = RecordCodecBuilder.create((p_219814_) -> {
         return p_219814_.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(ProfilePublicKey.Data::expiresAt), Crypt.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(ProfilePublicKey.Data::key), ExtraCodecs.BASE64_STRING.fieldOf("signature").forGetter(ProfilePublicKey.Data::keySignature)).apply(p_219814_, ProfilePublicKey.Data::new);
      });

      public Data(FriendlyByteBuf p_219809_) {
         this(p_219809_.readInstant(), p_219809_.readPublicKey(), p_219809_.readByteArray(4096));
      }

      public void write(FriendlyByteBuf p_219816_) {
         p_219816_.writeInstant(this.expiresAt);
         p_219816_.writePublicKey(this.key);
         p_219816_.writeByteArray(this.keySignature);
      }

      boolean validateSignature(SignatureValidator p_219812_) {
         return p_219812_.validate(this.signedPayload().getBytes(StandardCharsets.US_ASCII), this.keySignature);
      }

      private String signedPayload() {
         String s = Crypt.rsaPublicKeyToString(this.key);
         return this.expiresAt.toEpochMilli() + s;
      }

      public boolean hasExpired() {
         return this.expiresAt.isBefore(Instant.now());
      }
   }
}