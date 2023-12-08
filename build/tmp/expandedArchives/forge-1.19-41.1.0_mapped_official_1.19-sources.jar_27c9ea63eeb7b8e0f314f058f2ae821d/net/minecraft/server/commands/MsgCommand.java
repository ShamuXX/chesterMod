package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public class MsgCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138061_) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = p_138061_.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_138063_) -> {
         return sendMessage(p_138063_.getSource(), EntityArgument.getPlayers(p_138063_, "targets"), MessageArgument.getChatMessage(p_138063_, "message"));
      }))));
      p_138061_.register(Commands.literal("tell").redirect(literalcommandnode));
      p_138061_.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendMessage(CommandSourceStack p_214523_, Collection<ServerPlayer> p_214524_, MessageArgument.ChatMessage p_214525_) {
      if (p_214524_.isEmpty()) {
         return 0;
      } else {
         p_214525_.resolve(p_214523_).thenAcceptAsync((p_214529_) -> {
            Component component = p_214529_.raw().serverContent();

            for(ServerPlayer serverplayer : p_214524_) {
               p_214523_.sendSuccess(Component.translatable("commands.message.display.outgoing", serverplayer.getDisplayName(), component).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
               PlayerChatMessage playerchatmessage = p_214529_.filter(p_214523_, serverplayer);
               if (playerchatmessage != null) {
                  serverplayer.sendChatMessage(playerchatmessage, p_214523_.asChatSender(), ChatType.MSG_COMMAND);
               }
            }

         }, p_214523_.getServer());
         return p_214524_.size();
      }
   }
}