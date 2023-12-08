package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record ChatType(Optional<ChatType.TextDisplay> chat, Optional<ChatType.TextDisplay> overlay, Optional<ChatType.Narration> narration) {
   public static final Codec<ChatType> CODEC = RecordCodecBuilder.create((p_237020_) -> {
      return p_237020_.group(ChatType.TextDisplay.CODEC.optionalFieldOf("chat").forGetter(ChatType::chat), ChatType.TextDisplay.CODEC.optionalFieldOf("overlay").forGetter(ChatType::overlay), ChatType.Narration.CODEC.optionalFieldOf("narration").forGetter(ChatType::narration)).apply(p_237020_, ChatType::new);
   });
   public static final ResourceKey<ChatType> CHAT = create("chat");
   public static final ResourceKey<ChatType> SYSTEM = create("system");
   public static final ResourceKey<ChatType> GAME_INFO = create("game_info");
   public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
   public static final ResourceKey<ChatType> MSG_COMMAND = create("msg_command");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND = create("team_msg_command");
   public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");
   public static final ResourceKey<ChatType> TELLRAW_COMMAND = create("tellraw_command");

   private static ResourceKey<ChatType> create(String p_237024_) {
      return ResourceKey.create(Registry.CHAT_TYPE_REGISTRY, new ResourceLocation(p_237024_));
   }

   public static Holder<ChatType> bootstrap(Registry<ChatType> p_237022_) {
      BuiltinRegistries.register(p_237022_, CHAT, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.text"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(p_237022_, SYSTEM, new ChatType(Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.SYSTEM))));
      BuiltinRegistries.register(p_237022_, GAME_INFO, new ChatType(Optional.empty(), Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty()));
      BuiltinRegistries.register(p_237022_, SAY_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.announcement"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(p_237022_, MSG_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.directMessage("commands.message.display.incoming"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(p_237022_, TEAM_MSG_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.teamMessage("chat.type.team.text"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(p_237022_, EMOTE_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.emote"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.emote"), ChatType.Narration.Priority.CHAT))));
      return BuiltinRegistries.register(p_237022_, TELLRAW_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.CHAT))));
   }

   public static record Narration(Optional<ChatDecoration> decoration, ChatType.Narration.Priority priority) {
      public static final Codec<ChatType.Narration> CODEC = RecordCodecBuilder.create((p_237040_) -> {
         return p_237040_.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatType.Narration::decoration), ChatType.Narration.Priority.CODEC.fieldOf("priority").forGetter(ChatType.Narration::priority)).apply(p_237040_, ChatType.Narration::new);
      });

      public static ChatType.Narration undecorated(ChatType.Narration.Priority p_237045_) {
         return new ChatType.Narration(Optional.empty(), p_237045_);
      }

      public static ChatType.Narration decorated(ChatDecoration p_237042_, ChatType.Narration.Priority p_237043_) {
         return new ChatType.Narration(Optional.of(p_237042_), p_237043_);
      }

      public Component decorate(Component p_237047_, @Nullable ChatSender p_237048_) {
         return this.decoration.map((p_237052_) -> {
            return p_237052_.decorate(p_237047_, p_237048_);
         }).orElse(p_237047_);
      }

      public static enum Priority implements StringRepresentable {
         CHAT("chat", false),
         SYSTEM("system", true);

         public static final Codec<ChatType.Narration.Priority> CODEC = StringRepresentable.fromEnum(ChatType.Narration.Priority::values);
         private final String name;
         private final boolean interrupts;

         private Priority(String p_237068_, boolean p_237069_) {
            this.name = p_237068_;
            this.interrupts = p_237069_;
         }

         public boolean interrupts() {
            return this.interrupts;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   public static record TextDisplay(Optional<ChatDecoration> decoration) {
      public static final Codec<ChatType.TextDisplay> CODEC = RecordCodecBuilder.create((p_237083_) -> {
         return p_237083_.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatType.TextDisplay::decoration)).apply(p_237083_, ChatType.TextDisplay::new);
      });

      public static ChatType.TextDisplay undecorated() {
         return new ChatType.TextDisplay(Optional.empty());
      }

      public static ChatType.TextDisplay decorated(ChatDecoration p_237085_) {
         return new ChatType.TextDisplay(Optional.of(p_237085_));
      }

      public Component decorate(Component p_237087_, @Nullable ChatSender p_237088_) {
         return this.decoration.map((p_237092_) -> {
            return p_237092_.decorate(p_237087_, p_237088_);
         }).orElse(p_237087_);
      }
   }
}