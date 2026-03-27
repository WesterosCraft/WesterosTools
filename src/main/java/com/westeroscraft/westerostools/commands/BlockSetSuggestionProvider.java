package com.westeroscraft.westerostools.commands;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;

import com.westeroscraft.westerostools.WesterosTools;

public class BlockSetSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private WesterosTools wt;

    public BlockSetSuggestionProvider(WesterosTools westerostools) {
        wt = westerostools;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        boolean fullName = remaining.contains(":");

        for (String candidate : wt.blockMap.keySet()) {
            if ((fullName && candidate.contains(":") && candidate.startsWith(remaining)) ||
                (!fullName && !candidate.contains(":") && candidate.startsWith(remaining))) {
                builder.suggest(candidate);
            }
        }

        return builder.buildFuture();
    }
}