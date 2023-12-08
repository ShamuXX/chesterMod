package net.minecraft.server.network;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public record FilteredText<T>(T raw, @Nullable T filtered) {
   public static final FilteredText<String> EMPTY_STRING = passThrough("");

   public static <T> FilteredText<T> passThrough(T p_215182_) {
      return new FilteredText<>(p_215182_, p_215182_);
   }

   public static <T> FilteredText<T> fullyFiltered(T p_215187_) {
      return new FilteredText<>(p_215187_, (T)null);
   }

   public <U> FilteredText<U> map(Function<T, U> p_215184_) {
      return new FilteredText<>(p_215184_.apply(this.raw), Util.mapNullable(this.filtered, p_215184_));
   }

   public boolean isFiltered() {
      return !this.raw.equals(this.filtered);
   }

   public boolean isFullyFiltered() {
      return this.filtered == null;
   }

   public T filteredOrElse(T p_215190_) {
      return (T)(this.filtered != null ? this.filtered : p_215190_);
   }

   @Nullable
   public T filter(ServerPlayer p_215176_, ServerPlayer p_215177_) {
      return (T)(p_215176_.shouldFilterMessageTo(p_215177_) ? this.filtered : this.raw);
   }

   @Nullable
   public T filter(CommandSourceStack p_215179_, ServerPlayer p_215180_) {
      ServerPlayer serverplayer = p_215179_.getPlayer();
      return (T)(serverplayer != null ? this.filter(serverplayer, p_215180_) : this.raw);
   }
}