package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public interface PreviewedArgument<T> extends ArgumentType<T> {
   @Nullable
   static CompletableFuture<Component> resolvePreviewed(ArgumentCommandNode<?, ?> p_232860_, CommandContextBuilder<CommandSourceStack> p_232861_) throws CommandSyntaxException {
      ArgumentType argumenttype = p_232860_.getType();
      if (argumenttype instanceof PreviewedArgument<?> previewedargument) {
         return previewedargument.resolvePreview(p_232861_, p_232860_.getName());
      } else {
         return null;
      }
   }

   static boolean isPreviewed(CommandNode<?> p_232863_) {
      if (p_232863_ instanceof ArgumentCommandNode<?, ?> argumentcommandnode) {
         if (argumentcommandnode.getType() instanceof PreviewedArgument) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   default CompletableFuture<Component> resolvePreview(CommandContextBuilder<CommandSourceStack> p_232857_, String p_232858_) throws CommandSyntaxException {
      ParsedArgument<CommandSourceStack, ?> parsedargument = p_232857_.getArguments().get(p_232858_);
      return parsedargument != null && this.getValueType().isInstance(parsedargument.getResult()) ? this.resolvePreview(p_232857_.getSource(), this.getValueType().cast(parsedargument.getResult())) : null;
   }

   CompletableFuture<Component> resolvePreview(CommandSourceStack p_232864_, T p_232865_) throws CommandSyntaxException;

   Class<T> getValueType();
}