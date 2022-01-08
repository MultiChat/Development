package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.Getter;
import xyz.olivermartin.multichat.velocity.MultiChat;

public abstract class Command implements SimpleCommand {
    @Getter
    private final CommandMeta meta;

    public Command(String name, String... names) {
        meta = MultiChat.getInstance().getServer().getCommandManager().metaBuilder(name).aliases(names).build();
    }
}
