package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final Style SUGGEST_STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(Component.translatable("commands.teammsg.failed.noteam"));

   public static void register(CommandDispatcher<CommandSourceStack> p_139000_) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = p_139000_.register(Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((p_139002_) -> {
         return sendMessage(p_139002_.getSource(), MessageArgument.getChatMessage(p_139002_, "message"));
      })));
      p_139000_.register(Commands.literal("tm").redirect(literalcommandnode));
   }

   private static int sendMessage(CommandSourceStack p_214763_, MessageArgument.ChatMessage p_214764_) throws CommandSyntaxException {
      Entity entity = p_214763_.getEntityOrException();
      PlayerTeam playerteam = (PlayerTeam)entity.getTeam();
      if (playerteam == null) {
         throw ERROR_NOT_ON_TEAM.create();
      } else {
         Component component = playerteam.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
         ChatSender chatsender = p_214763_.asChatSender().withTeamName(component);
         List<ServerPlayer> list = p_214763_.getServer().getPlayerList().getPlayers().stream().filter((p_214761_) -> {
            return p_214761_ == entity || p_214761_.getTeam() == playerteam;
         }).toList();
         if (list.isEmpty()) {
            return 0;
         } else {
            p_214764_.resolve(p_214763_).thenAcceptAsync((p_214771_) -> {
               for(ServerPlayer serverplayer : list) {
                  if (serverplayer == entity) {
                     serverplayer.sendSystemMessage(Component.translatable("chat.type.team.sent", component, p_214763_.getDisplayName(), p_214771_.raw().serverContent()));
                  } else {
                     PlayerChatMessage playerchatmessage = p_214771_.filter(p_214763_, serverplayer);
                     if (playerchatmessage != null) {
                        serverplayer.sendChatMessage(playerchatmessage, chatsender, ChatType.TEAM_MSG_COMMAND);
                     }
                  }
               }

            }, p_214763_.getServer());
            return list.size();
         }
      }
   }
}