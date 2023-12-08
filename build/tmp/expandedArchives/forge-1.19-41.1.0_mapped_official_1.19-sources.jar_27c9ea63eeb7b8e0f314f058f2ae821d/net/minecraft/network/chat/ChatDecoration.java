package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public record ChatDecoration(String translationKey, List<ChatDecoration.Parameter> parameters, Style style) {
   public static final Codec<ChatDecoration> CODEC = RecordCodecBuilder.create((p_236895_) -> {
      return p_236895_.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatDecoration::translationKey), ChatDecoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatDecoration::parameters), Style.FORMATTING_CODEC.fieldOf("style").forGetter(ChatDecoration::style)).apply(p_236895_, ChatDecoration::new);
   });

   public static ChatDecoration withSender(String p_236897_) {
      return new ChatDecoration(p_236897_, List.of(ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public static ChatDecoration directMessage(String p_236903_) {
      Style style = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
      return new ChatDecoration(p_236903_, List.of(ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), style);
   }

   public static ChatDecoration teamMessage(String p_236909_) {
      return new ChatDecoration(p_236909_, List.of(ChatDecoration.Parameter.TEAM_NAME, ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public Component decorate(Component p_236899_, @Nullable ChatSender p_236900_) {
      Object[] aobject = this.resolveParameters(p_236899_, p_236900_);
      return Component.translatable(this.translationKey, aobject).withStyle(this.style);
   }

   private Component[] resolveParameters(Component p_236905_, @Nullable ChatSender p_236906_) {
      Component[] acomponent = new Component[this.parameters.size()];

      for(int i = 0; i < acomponent.length; ++i) {
         ChatDecoration.Parameter chatdecoration$parameter = this.parameters.get(i);
         acomponent[i] = chatdecoration$parameter.select(p_236905_, p_236906_);
      }

      return acomponent;
   }

   public static enum Parameter implements StringRepresentable {
      SENDER("sender", (p_236939_, p_236940_) -> {
         return p_236940_ != null ? p_236940_.name() : null;
      }),
      TEAM_NAME("team_name", (p_236936_, p_236937_) -> {
         return p_236937_ != null ? p_236937_.teamName() : null;
      }),
      CONTENT("content", (p_236932_, p_236933_) -> {
         return p_236932_;
      });

      public static final Codec<ChatDecoration.Parameter> CODEC = StringRepresentable.fromEnum(ChatDecoration.Parameter::values);
      private final String name;
      private final ChatDecoration.Parameter.Selector selector;

      private Parameter(String p_236925_, ChatDecoration.Parameter.Selector p_236926_) {
         this.name = p_236925_;
         this.selector = p_236926_;
      }

      public Component select(Component p_236929_, @Nullable ChatSender p_236930_) {
         Component component = this.selector.select(p_236929_, p_236930_);
         return Objects.requireNonNullElse(component, CommonComponents.EMPTY);
      }

      public String getSerializedName() {
         return this.name;
      }

      public interface Selector {
         @Nullable
         Component select(Component p_236945_, @Nullable ChatSender p_236946_);
      }
   }
}