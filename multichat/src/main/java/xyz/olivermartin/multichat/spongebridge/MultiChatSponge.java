package xyz.olivermartin.multichat.spongebridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.impl.SimpleMutableMessageChannel;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spongebridge.listeners.BungeeChatListener;
import xyz.olivermartin.multichat.spongebridge.listeners.BungeeCommandListener;
import xyz.olivermartin.multichat.spongebridge.listeners.BungeePlayerCommandListener;
import xyz.olivermartin.multichat.spongebridge.listeners.MetaListener;
import xyz.olivermartin.multichat.spongebridge.listeners.PlayerChannelListener;
import xyz.olivermartin.multichat.spongebridge.listeners.SpongeChatListener;
import xyz.olivermartin.multichat.spongebridge.listeners.SpongeIgnoreListener;
import xyz.olivermartin.multichat.spongebridge.listeners.SpongeLoginListener;

/**
 * MultiChatSponge - MAIN CLASS
 * <p>Allows MultiChat to fetch data from Sponge and powers /nick etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
@Plugin(id = "multichat", name = "MultiChatSponge", version = "1.7.3", dependencies = { @Dependency(id = "placeholderapi", optional = true) })
public final class MultiChatSponge implements CommandExecutor {

	public static SimpleMutableMessageChannel multichatChannel;

	ChannelRegistrar channelRegistrar;

	RawDataChannel commChannel;
	static RawDataChannel chatChannel;

	RawDataChannel actionChannel;
	RawDataChannel playerActionChannel;

	static RawDataChannel prefixChannel;
	static RawDataChannel suffixChannel;
	static RawDataChannel nickChannel;
	static RawDataChannel worldChannel;
	static RawDataChannel channelChannel;
	static RawDataChannel ignoreChannel;

	public static Map<UUID,String> nicknames;
	public static Map<UUID,String> displayNames = new HashMap<UUID,String>();

	public static boolean setDisplayNameLastVal = false;
	public static String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";

	public static boolean globalChatServer = false;
	public static String globalChatFormat = "&f%DISPLAYNAME%&f: ";
	public static String localChatFormat = "&7&lLOCAL &f> &f%DISPLAYNAME%&f: ";

	public static boolean overrideGlobalFormat = false;
	public static String overrideGlobalFormatFormat = "&f%DISPLAYNAME%&f: ";

	public static Optional<PlaceholderService> papi;

	public static String serverName = "SPONGE";

	public static boolean showNicknamePrefix = false;
	public static String nicknamePrefix = "~";
	public static List<String> nicknameBlacklist = new ArrayList<String>();

	public static Map<Player, String> playerChannels = new HashMap<Player, String>();
	public static Map<String, PseudoChannel> channelObjects = new HashMap<String, PseudoChannel>();
	public static Map<UUID, Set<UUID>> ignoreMap = new HashMap<UUID, Set<UUID>>();
	public static Map<UUID, Boolean> colourMap = new HashMap<UUID, Boolean>();

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@SuppressWarnings("serial")
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		
		// DEBUG MODE
		//DebugManager.setDebug(true);

		SpongeConfigManager.getInstance().registerHandler("multichatsponge.yml");
		ConfigurationNode config = SpongeConfigManager.getInstance().getHandler("multichatsponge.yml").getConfig();
		overrideGlobalFormat = config.getNode("override_global_format").getBoolean();
		overrideGlobalFormatFormat = config.getNode("override_global_format_format").getString();
		localChatFormat = config.getNode("local_chat_format").getString();
		serverName = config.getNode("server_name").getString();

		if (!config.getNode("show_nickname_prefix").isVirtual()) {
			showNicknamePrefix = config.getNode("show_nickname_prefix").getBoolean();
			nicknamePrefix = config.getNode("nickname_prefix").getString();
			nicknameBlacklist = config.getNode("nickname_blacklist").getList(value -> value.toString());
		}

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
		ChannelBinding.RawDataChannel channelChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:channel");
		ChannelBinding.RawDataChannel ignoreChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:ignore");

		commChannel.addListener(Platform.Type.SERVER, new MetaListener(commChannel));
		chatChannel.addListener(Platform.Type.SERVER, new BungeeChatListener(chatChannel));
		channelChannel.addListener(Platform.Type.SERVER, new PlayerChannelListener());
		ignoreChannel.addListener(Platform.Type.SERVER, new SpongeIgnoreListener());

		actionChannel.addListener(Platform.Type.SERVER, new BungeeCommandListener());
		playerActionChannel.addListener(Platform.Type.SERVER, new BungeePlayerCommandListener());

		this.commChannel = commChannel;
		MultiChatSponge.chatChannel = chatChannel;

		this.actionChannel = actionChannel;
		this.playerActionChannel = playerActionChannel;

		MultiChatSponge.prefixChannel = prefixChannel;
		MultiChatSponge.suffixChannel = suffixChannel;
		MultiChatSponge.nickChannel = nickChannel;
		MultiChatSponge.worldChannel = worldChannel;
		MultiChatSponge.channelChannel = channelChannel;
		MultiChatSponge.ignoreChannel = ignoreChannel;

		// Register listeners

		Sponge.getEventManager().registerListeners(this, new SpongeChatListener());
		Sponge.getEventManager().registerListeners(this, new SpongeLoginListener());

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

		// Register message channel

		multichatChannel = new SimpleMutableMessageChannel();

		// Dependencies

		try {
			papi = Sponge.getServiceManager().provide(PlaceholderService.class);
			System.out.println("Connected to PlaceholderAPI!");
		} catch (NoClassDefFoundError e) {
			papi = Optional.empty();
		}

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
		Sponge.getChannelRegistrar().unbindChannel(channelChannel);
		Sponge.getChannelRegistrar().unbindChannel(ignoreChannel);

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

	public static void sendChatToBungee(Player player, String message, String format) {

		chatChannel.sendTo(player,buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(message).writeUTF(format));

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

		String strippedNickname = stripAllFormattingCodes(nickname);

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> lookedUpName = uss.get(strippedNickname);

		// Check if a player name exists already (but not the name of this player)
		if (lookedUpName.isPresent() && !strippedNickname.equalsIgnoreCase(target.getName()) && !sender.hasPermission("multichatsponge.nick.impersonate")) {
			sender.sendMessage(Text.of("Sorry, a player already exists with this name!"));
			return CommandResult.success();
		}

		String targetNickname;
		if (nicknames.containsKey(targetUUID)) {
			targetNickname = stripAllFormattingCodes(nicknames.get(targetUUID));
		} else {
			targetNickname = target.getName();
		}

		// Check if a nickname exists already
		if (nicknames.values().stream()
				.map(nick -> stripAllFormattingCodes(nick))
				.anyMatch(nick -> nick.equalsIgnoreCase(strippedNickname))
				&& !targetNickname.equalsIgnoreCase(strippedNickname) ) {
			//&& !sender.hasPermission("multichatsponge.nick.duplicate")) {

			sender.sendMessage(Text.of("Sorry, a player already has that nickname!"));
			return CommandResult.success();

		}

		boolean blacklisted = false;
		for (String bl : MultiChatSponge.nicknameBlacklist) {
			if (strippedNickname.matches(bl)) blacklisted = true;
		}

		if (blacklisted) {

			sender.sendMessage(Text.of("Sorry, this name is not allowed!"));
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

	public String stripAllFormattingCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");

	}

}
