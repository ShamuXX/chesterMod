package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

public interface TextFilter {
   TextFilter DUMMY = new TextFilter() {
      public void join() {
      }

      public void leave() {
      }

      public CompletableFuture<FilteredText<String>> processStreamMessage(String p_143708_) {
         return CompletableFuture.completedFuture(FilteredText.passThrough(p_143708_));
      }

      public CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> p_143710_) {
         return CompletableFuture.completedFuture(p_143710_.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
      }
   };

   void join();

   void leave();

   CompletableFuture<FilteredText<String>> processStreamMessage(String p_10096_);

   CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> p_10097_);

   default CompletableFuture<FilteredText<Component>> processStreamComponent(Component p_215266_) {
      return this.processStreamMessage(p_215266_.getString()).thenApply((p_215269_) -> {
         Component component = Util.mapNullable(p_215269_.filtered(), Component::literal);
         return new FilteredText<>(p_215266_, component);
      });
   }
}