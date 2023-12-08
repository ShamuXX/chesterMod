package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (p_236950_, p_236951_) -> {
      return CompletableFuture.completedFuture(p_236951_);
   };

   CompletableFuture<Component> decorate(@Nullable ServerPlayer p_236962_, Component p_236963_);

   default CompletableFuture<FilteredText<Component>> decorateFiltered(@Nullable ServerPlayer p_236970_, FilteredText<Component> p_236971_) {
      CompletableFuture<Component> completablefuture = this.decorate(p_236970_, p_236971_.raw());
      if (!p_236971_.isFiltered()) {
         return completablefuture.thenApply(FilteredText::passThrough);
      } else if (p_236971_.filtered() == null) {
         return completablefuture.thenApply(FilteredText::fullyFiltered);
      } else {
         CompletableFuture<Component> completablefuture1 = this.decorate(p_236970_, p_236971_.filtered());
         return CompletableFuture.allOf(completablefuture, completablefuture1).thenApply((p_236960_) -> {
            return new FilteredText<>(completablefuture.join(), completablefuture1.join());
         });
      }
   }

   default CompletableFuture<FilteredText<PlayerChatMessage>> decorateChat(@Nullable ServerPlayer p_236965_, FilteredText<Component> p_236966_, MessageSignature p_236967_, boolean p_236968_) {
      return this.decorateFiltered(p_236965_, p_236966_).thenApply((p_236956_) -> {
         return PlayerChatMessage.filteredSigned(p_236966_, p_236956_, p_236967_, p_236968_, p_236965_);
      });
   }
}
