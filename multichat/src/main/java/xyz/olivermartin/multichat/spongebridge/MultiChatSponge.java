package xyz.olivermartin.multichat.spongebridge;

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
import xyz.olivermartin.multichat.spongebridge.listeners.BungeeChatListener;
import xyz.olivermartin.multichat.spongebridge.listeners.BungeeCommandListener;
import xyz.olivermartin.multichat.spongebridge.listeners.BungeePlayerCommandListener;
import xyz.olivermartin.multichat.spongebridge.listeners.MetaListener;

/**
 * MultiChatSponge - MAIN CLASS
 * <p>Allows MultiChat to fetch data from Sponge and powers /nick etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
@Plugin(id = "multichat", name = "MultiChatSponge", version = "1.7")
public final class MultiChatSponge implements CommandExecutor {

	ChannelRegistrar channelRegistrar;

	RawDataChannel commChannel;
	RawDataChannel chatChannel;

	RawDataChannel actionChannel;
	RawDataChannel playerActionChannel;

	static RawDataChannel prefixChannel;
	static RawDataChannel suffixChannel;
	static RawDataChannel nickChannel;
	static RawDataChannel worldChannel;

	public static Map<UUID,String> nicknames;
	public static Map<UUID,String> displayNames = new HashMap<UUID,String>();
	public static boolean setDisplayNameLastVal = false;
	public static String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";

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

				nicknames = (Map<UUID, String>) rootNode.getNode("nicknames").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });

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

				e.printStackTrace();

			}

		} catch (IOException e) {

			e.printStackTrace();
			nicknames = new HashMap<UUID,String>();

		}

		// Register channels

		channelRegistrar = Sponge.getGame().getChannelRegistrar();

		ChannelBinding.RawDataChannel commChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:comm");
		ChannelBinding.RawDataChannel chatChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:chat");

		ChannelBinding.RawDataChannel actionChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:action");
		ChannelBinding.RawDataChannel playerActionChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:paction");

		ChannelBinding.RawDataChannel prefixChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:prefix");
		ChannelBinding.RawDataChannel suffixChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:suffix");
		ChannelBinding.RawDataChannel worldChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:world");
		ChannelBinding.RawDataChannel nickChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:nick");

		commChannel.addListener(Platform.Type.SERVER, new MetaListener(commChannel));
		chatChannel.addListener(Platform.Type.SERVER, new BungeeChatListener(chatChannel));

		actionChannel.addListener(Platform.Type.SERVER, new BungeeCommandListener());
		playerActionChannel.addListener(Platform.Type.SERVER, new BungeePlayerCommandListener());

		this.commChannel = commChannel;
		this.chatChannel = chatChannel;

		this.actionChannel = actionChannel;
		this.playerActionChannel = playerActionChannel;

		MultiChatSponge.prefixChannel = prefixChannel;
		MultiChatSponge.suffixChannel = suffixChannel;
		MultiChatSponge.nickChannel = nickChannel;
		MultiChatSponge.worldChannel = worldChannel;

		// Register commands

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

		Sponge.getChannelRegistrar().unbindChannel(commChannel);
		Sponge.getChannelRegistrar().unbindChannel(chatChannel);
		Sponge.getChannelRegistrar().unbindChannel(actionChannel);
		Sponge.getChannelRegistrar().unbindChannel(prefixChannel);
		Sponge.getChannelRegistrar().unbindChannel(suffixChannel);
		Sponge.getChannelRegistrar().unbindChannel(nickChannel);
		Sponge.getChannelRegistrar().unbindChannel(worldChannel);

		ConfigurationNode rootNode;

		rootNode = configLoader.createEmptyNode();

		try {

			rootNode.getNode("nicknames").setValue(new TypeToken<Map<UUID,String>>() {}, nicknames);

			try {
				configLoader.save(rootNode);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (ObjectMappingException e) {
			e.printStackTrace();
			System.err.println("[MultiChatSponge] ERROR: Could not write nicknames :(");
		}

	}

	public static void updatePlayerMeta(String playername, boolean setDisplayName, String displayNameFormat) {

		if (!Sponge.getServer().getPlayer(playername).isPresent()) return;

		Player player = Sponge.getServer().getPlayer(playername).get();
		String nickname = "";
		String prefix = "";
		String suffix = "";

		if (nicknames.containsKey(player.getUniqueId())) {
			nickname = nicknames.get(player.getUniqueId());
		} else {
			nickname =  player.getName();
		}

		final String fNickname = nickname;
		nickChannel.sendTo(player,buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(fNickname));

		final String world = player.getWorld().getName();
		worldChannel.sendTo(player,buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(world));

		if (player.getOption("prefix").isPresent()) {
			prefix = player.getOption("prefix").get();
		}

		final String fPrefix = prefix;
		prefixChannel.sendTo(player,buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(fPrefix));

		if (player.getOption("suffix").isPresent()) {
			suffix = player.getOption("suffix").get();
		}

		final String fSuffix = suffix;
		suffixChannel.sendTo(player,buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(fSuffix));

		if (setDisplayName) {

			displayNameFormat = displayNameFormat.replaceAll("%NICK%", nickname);
			displayNameFormat = displayNameFormat.replaceAll("%NAME%", playername);
			displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", prefix);
			displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", suffix);
			displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");

			final String finalDisplayName = displayNameFormat;

			Sponge.getServer().getPlayer(playername).ifPresent(x -> x.offer(Keys.DISPLAY_NAME, Text.of(finalDisplayName)));

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
			updatePlayerMeta(target.getName(), setDisplayNameLastVal, displayNameFormatLastVal);
			sender.sendMessage(Text.of(target.getName() + " has had their nickname removed!"));
			return CommandResult.success();
		}

		addNickname(targetUUID,nickname);
		updatePlayerMeta(target.getName(), setDisplayNameLastVal, displayNameFormatLastVal);

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
