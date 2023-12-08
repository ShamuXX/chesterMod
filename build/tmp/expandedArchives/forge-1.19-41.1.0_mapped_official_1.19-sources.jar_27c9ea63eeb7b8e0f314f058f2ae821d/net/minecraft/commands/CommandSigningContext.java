package net.minecraft.commands;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.Crypt;

public interface CommandSigningContext {
   CommandSigningContext NONE = (p_230582_) -> {
      return MessageSignature.unsigned();
   };

   MessageSignature getArgumentSignature(String p_230583_);

   default boolean signedArgumentPreview(String p_230580_) {
      return false;
   }

   public static record SignedArguments(UUID sender, Instant timeStamp, ArgumentSignatures argumentSignatures, boolean signedPreview) implements CommandSigningContext {
      public MessageSignature getArgumentSignature(String p_230602_) {
         Crypt.SaltSignaturePair crypt$saltsignaturepair = this.argumentSignatures.get(p_230602_);
         return crypt$saltsignaturepair != null ? new MessageSignature(this.sender, this.timeStamp, crypt$saltsignaturepair) : MessageSignature.unsigned();
      }

      public boolean signedArgumentPreview(String p_230595_) {
         return this.signedPreview;
      }
   }
}