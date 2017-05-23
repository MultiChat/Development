package com.olivermartin410.plugins;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "multichat", name = "MultiChat Sponge", version = "1.5")
public final class SpongeComm implements CommandExecutor {

	ChannelRegistrar channelRegistrar;
	RawDataChannel channel;
	public static Map<UUID,String> nicknames;
	public static Map<UUID,String> displayNames = new HashMap<UUID,String>();

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@SuppressWarnings("serial")
	@Listener
	public void onServerStart(GameStartedServerEvent event) {

		configLoader = HoconConfigurationLoader.builder().setFile(new File("nicknames")).build();
		ConfigurationNode rootNode;

		try {
			rootNode = configLoader.load();
			try {
				nicknames = (Map<UUID, String>) rootNode.getNode("nicknames").getValue(new TypeToken<Map<UUID,String>>() {});
				if (nicknames == null) {
					nicknames = new HashMap<UUID,String>();
					System.out.println("[MultiChatSponge] Created nicknames map");
				}
				System.out.println("[MultiChatSponge] Nicknames appeared to load correctly!");
			} catch (ClassCastException e) {
				nicknames = new HashMap<UUID,String>();
			} catch (ObjectMappingException e) {
				nicknames = new HashMap<UUID,String>();
			}
			try {
				configLoader.save(rootNode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			nicknames = new HashMap<UUID,String>();
		}

		channelRegistrar = Sponge.getGame().getChannelRegistrar();
		ChannelBinding.RawDataChannel channel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "MultiChat");
		channel.addListener(Platform.Type.SERVER, new MultiChatRawDataListener(channel));
		this.channel = channel;

		CommandSpec nicknameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Nickname Command"))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
						GenericArguments.remainingJoinedStrings(Text.of("message")))
				.permission("multichatsponge.nick.self")
				.executor(this)
				.build();

		Sponge.getCommandManager().register(this, nicknameCommandSpec, "nick");
	}

	@SuppressWarnings("serial")
	@Listener
	public void onServerStop(GameStoppingServerEvent event) {
		Sponge.getChannelRegistrar().unbindChannel(channel);

		ConfigurationNode rootNode;

		//try {
		rootNode = configLoader.createEmptyNode();
		try {
			rootNode.getNode("nicknames").setValue(new TypeToken<Map<UUID,String>>() {}, nicknames);
			try {
				configLoader.save(rootNode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ObjectMappingException e) {
			e.printStackTrace();
			System.err.println("[MultiChatSponge] ERROR: Could not write nicknames :(");
		}
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}

	}

	private void updatePlayerDisplayName(String playername) {

		String nickname;
		if (Sponge.getServer().getPlayer(playername).isPresent()) {
			Player player = Sponge.getServer().getPlayer(playername).get();
			if (nicknames.containsKey(player.getUniqueId())) {
				nickname = nicknames.get(player.getUniqueId());
				System.out.println("Has a nickname: " + nickname);
			} else {
				nickname =  player.getName();
			}

			if (player.getOption("prefix").isPresent()) {
				if (player.getOption("suffix").isPresent()) {
					displayNames.put(player.getUniqueId(), player.getOption("prefix").get() + nickname + player.getOption("suffix").get());
					Sponge.getServer().getPlayer(playername).ifPresent(x -> x.offer(Keys.DISPLAY_NAME, Text.of(player.getOption("prefix").get() + nickname + player.getOption("suffix").get())));
					//player.offer(Keys.DISPLAY_NAME, Text.of(player.getOption("prefix").get() + nickname + player.getOption("suffix").get()));
				} else {
					displayNames.put(player.getUniqueId(), player.getOption("prefix").get() + nickname);
					//player.offer(Keys.DISPLAY_NAME, Text.of(player.getOption("prefix").get() + nickname));
					Sponge.getServer().getPlayer(playername).ifPresent(x -> x.offer(Keys.DISPLAY_NAME, Text.of(player.getOption("prefix").get() + nickname)));
				}
			} else {
				displayNames.put(player.getUniqueId(), nickname);
				//player.offer(Keys.DISPLAY_NAME, Text.of(nickname));
				Sponge.getServer().getPlayer(playername).ifPresent(x -> x.offer(Keys.DISPLAY_NAME, Text.of(nickname)));
			}

		}

	}

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		if (sender instanceof Player) {
			sender = (Player) sender;
		} else {
			sender.sendMessage(Text.of("Only players can use this command!"));
			return CommandResult.success();
		}

		Player target = args.<Player>getOne("player").get();
		String nickname = args.<String>getOne("message").get();

		if (target != sender) {
			if (!sender.hasPermission("multichatsponge.nick.others")) {
				sender.sendMessage(Text.of("You do not have permission to nickname other players!"));
				return CommandResult.success();
			}
		}

		UUID targetUUID = target.getUniqueId();

		if (nickname.equalsIgnoreCase("off")) {
			removeNickname(targetUUID);
			updatePlayerDisplayName(target.getName());
			sender.sendMessage(Text.of(target.getName() + " has had their nickname removed!"));
			return CommandResult.success();
		}

		addNickname(targetUUID,nickname);
		updatePlayerDisplayName(target.getName());

		sender.sendMessage(Text.of(target.getName() + " has been nicknamed!"));
		return CommandResult.success();
	}

	private void addNickname(UUID uuid, String nickname) {
		nicknames.put(uuid,nickname);
	}

	private void removeNickname(UUID uuid) {
		nicknames.remove(uuid);
	}

}