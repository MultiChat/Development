package xyz.olivermartin.multichat.spongebridge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
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
import xyz.olivermartin.multichat.database.DatabaseManager;
import xyz.olivermartin.multichat.database.DatabaseMode;
import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spongebridge.commands.MultiChatSpongeCommand;
import xyz.olivermartin.multichat.spongebridge.commands.SpongeNickCommand;
import xyz.olivermartin.multichat.spongebridge.commands.SpongeRealnameCommand;
import xyz.olivermartin.multichat.spongebridge.commands.SpongeUsernameCommand;
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
@Plugin(id = "multichat", name = "MultiChatSponge", version = "1.8", dependencies = { @Dependency(id = "placeholderapi", optional = true) })
public final class MultiChatSponge {

	public static SimpleMutableMessageChannel multichatChannel;

	ChannelRegistrar channelRegistrar;

	RawDataChannel commChannel;
	static RawDataChannel chatChannel;

	RawDataChannel actionChannel;
	RawDataChannel playerActionChannel;

	static RawDataChannel prefixChannel;
	static RawDataChannel suffixChannel;
	static RawDataChannel displayNameChannel;
	static RawDataChannel nickChannel;
	static RawDataChannel worldChannel;
	static RawDataChannel channelChannel;
	static RawDataChannel ignoreChannel;

	//public static Map<UUID,String> nicknames;
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
	public static int nicknameMaxLength = 20;
	public static int nicknameMinLength = 3;
	public static boolean nicknameLengthIncludeFormatting = false;
	public static boolean nicknameSQL = false;

	public static Map<Player, String> playerChannels = new HashMap<Player, String>();
	public static Map<String, PseudoChannel> channelObjects = new HashMap<String, PseudoChannel>();
	public static Map<UUID, Set<UUID>> ignoreMap = new HashMap<UUID, Set<UUID>>();
	public static Map<UUID, Boolean> colourMap = new HashMap<UUID, Boolean>();

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;

	@SuppressWarnings("serial")
	@Listener
	public void onServerStart(GameStartedServerEvent event) {

		// DEBUG MODE
		//DebugManager.setDebug(true);//TODO

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
			if (!config.getNode("nickname_length_limit").isVirtual()) {
				nicknameMaxLength = config.getNode("nickname_length_limit").getInt();
				nicknameLengthIncludeFormatting = config.getNode("nickname_length_limit_formatting").getBoolean();
			}

			if (!config.getNode("nickname_length_min").isVirtual()) {

				nicknameMinLength = config.getNode("nickname_length_min").getInt();

			}


			// SQL

			if (!config.getNode("nickname_sql").isVirtual()) {

				nicknameSQL = config.getNode("nickname_sql").getBoolean();

				if (nicknameSQL) {

					if (config.getNode("mysql").getBoolean()) {

						DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);

						DatabaseManager.getInstance().setURLMySQL(config.getNode("mysql_url").getString());
						DatabaseManager.getInstance().setUsernameMySQL(config.getNode("mysql_user").getString());
						DatabaseManager.getInstance().setPasswordMySQL(config.getNode("mysql_pass").getString());

						try {

							if (!config.getNode("mysql_database").isVirtual()) {
								DatabaseManager.getInstance().createDatabase("multichatsponge.db", config.getNode("mysql_database").getString());
							} else {
								DatabaseManager.getInstance().createDatabase("multichatsponge.db", "multichatsponge");
							}

							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().connectToDatabase();
							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().update("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().update("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");
							///DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().disconnectFromDatabase();

							SpongeNameManager.useSQL(nicknameSQL);

						} catch (SQLException e) {
							nicknameSQL = false;
							SpongeNameManager.useSQL(false);
							System.err.println("Could not enable database! Using files...");
							e.printStackTrace();
						}

					} else {

						DatabaseManager.getInstance().setMode(DatabaseMode.SQLite);

						File configDir = privateConfigDir.toFile();

						if (!(configDir.exists() && configDir.isDirectory())) {
							configDir.mkdir();
						}

						DatabaseManager.getInstance().setPathSQLite(configDir);

						try {

							DatabaseManager.getInstance().createDatabase("multichatsponge.db");

							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().connectToDatabase();
							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().update("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().update("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");
							//DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().disconnectFromDatabase();

							SpongeNameManager.useSQL(nicknameSQL);

						} catch (SQLException e) {
							nicknameSQL = false;
							SpongeNameManager.useSQL(false);
							System.err.println("Could not enable database! Using files...");
							e.printStackTrace();
						}

					}

				}

			}

			// /SQL


		}

		if (SpongeNameManager.getInstance() instanceof SpongeFileNameManager) {

			File f = new File("multichat_namedata");
			File fLegacy = new File("nicknames");

			if ((f.exists()) && (!f.isDirectory())) {

				// New file based storage
				configLoader = HoconConfigurationLoader.builder().setFile(f).build();
				ConfigurationNode rootNode;

				Map<UUID, String> mapUUIDNick;
				Map<String, UUID> mapNickUUID;
				Map<String, String> mapNickFormatted;

				try {

					rootNode = configLoader.load();

					try {

						mapUUIDNick = (Map<UUID, String>) rootNode.getNode("mapUUIDNick").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });
						mapNickUUID = (Map<String, UUID>) rootNode.getNode("mapNickUUID").getValue(new TypeToken<Map<String, UUID>>() { /* EMPTY */ });
						mapNickFormatted = (Map<String, String>) rootNode.getNode("mapNickFormatted").getValue(new TypeToken<Map<String,String>>() { /* EMPTY */ });

						if (mapUUIDNick == null) {
							mapUUIDNick = new HashMap<UUID,String>();
							mapNickUUID = new HashMap<String, UUID>();
							mapNickFormatted = new HashMap<String,String>();
							System.out.println("[MultiChatSponge] Created new nicknames maps for file storage!");
						}

						System.out.println("[MultiChatSponge] Nicknames appeared to load correctly!");

					} catch (ClassCastException e) {

						mapUUIDNick = new HashMap<UUID,String>();
						mapNickUUID = new HashMap<String, UUID>();
						mapNickFormatted = new HashMap<String,String>();

					} catch (ObjectMappingException e) {

						mapUUIDNick = new HashMap<UUID,String>();
						mapNickUUID = new HashMap<String, UUID>();
						mapNickFormatted = new HashMap<String,String>();

					}

					try {

						configLoader.save(rootNode);

					} catch (IOException e) {

						e.printStackTrace();

					}

				} catch (IOException e) {

					e.printStackTrace();
					mapUUIDNick = new HashMap<UUID,String>();
					mapNickUUID = new HashMap<String, UUID>();
					mapNickFormatted = new HashMap<String,String>();

				}

				((SpongeFileNameManager)SpongeNameManager.getInstance()).loadFromFile(mapUUIDNick, mapNickUUID, mapNickFormatted);

			} else if (fLegacy.exists()) {

				// Legacy storage
				ConfigurationLoader<CommentedConfigurationNode> legacyConfigLoader = HoconConfigurationLoader.builder().setFile(new File("nicknames")).build();
				ConfigurationNode rootNode;

				Map<UUID, String> nicknames;

				try {

					rootNode = legacyConfigLoader.load();

					try {

						nicknames = (Map<UUID, String>) rootNode.getNode("nicknames").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });

						if (nicknames == null) {
							nicknames = new HashMap<UUID,String>();
							System.out.println("[MultiChatSponge] Created nicknames map");
						}

						System.out.println("[MultiChatSponge] Loaded a legacy (PRE-1.8) nicknames file!");
						System.out.println("[MultiChatSponge] Attempting conversion...");

					} catch (ClassCastException e) {

						nicknames = new HashMap<UUID,String>();

					} catch (ObjectMappingException e) {

						nicknames = new HashMap<UUID,String>();

					}

					try {

						legacyConfigLoader.save(rootNode);

					} catch (IOException e) {

						e.printStackTrace();

					}

				} catch (IOException e) {

					e.printStackTrace();
					nicknames = new HashMap<UUID,String>();

				}

				// Now loaded the legacy nicknames into "nicknames" var

				for (Entry<UUID, String> e : nicknames.entrySet()) {
					SpongeNameManager.getInstance().setNickname(e.getKey(), e.getValue());
				}

				System.out.println("[MultiChatSponge] Conversion completed.");
				System.out.println("[MultiChatSponge] Converted " + nicknames.size() + " records!");

				// Attempting to save the new file...

				configLoader = HoconConfigurationLoader.builder().setFile(f).build();
				ConfigurationNode rootNode2;

				rootNode2 = configLoader.createEmptyNode();

				try {

					rootNode2.getNode("mapUUIDNick").setValue(new TypeToken<Map<UUID,String>>() {}, 
							((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapUUIDNick());
					rootNode2.getNode("mapNickUUID").setValue(new TypeToken<Map<String,UUID>>() {}, 
							((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapNickUUID());
					rootNode2.getNode("mapNickFormatted").setValue(new TypeToken<Map<String,String>>() {}, 
							((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapNickFormatted());

					try {
						configLoader.save(rootNode2);
						System.out.println("[MultiChatSponge] SAVED NEW NICKNAME DATA!.");
					} catch (IOException e) {
						e.printStackTrace();
					}

				} catch (ObjectMappingException e) {
					e.printStackTrace();
					System.err.println("[MultiChatSponge] ERROR: Could not write nicknames :(");
				}

			} else {

				// First startup

				// New file based storage
				configLoader = HoconConfigurationLoader.builder().setFile(f).build();
				ConfigurationNode rootNode;

				Map<UUID, String> mapUUIDNick;
				Map<String, UUID> mapNickUUID;
				Map<String, String> mapNickFormatted;

				try {

					rootNode = configLoader.load();

					try {

						mapUUIDNick = (Map<UUID, String>) rootNode.getNode("mapUUIDNick").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });
						mapNickUUID = (Map<String, UUID>) rootNode.getNode("mapNickUUID").getValue(new TypeToken<Map<String, UUID>>() { /* EMPTY */ });
						mapNickFormatted = (Map<String, String>) rootNode.getNode("mapNickFormatted").getValue(new TypeToken<Map<String,String>>() { /* EMPTY */ });

						if (mapUUIDNick == null) {
							mapUUIDNick = new HashMap<UUID,String>();
							mapNickUUID = new HashMap<String, UUID>();
							mapNickFormatted = new HashMap<String,String>();
							System.out.println("[MultiChatSponge] Created new nicknames maps for file storage!");
						}

						System.out.println("[MultiChatSponge] Nicknames appeared to load correctly!");

					} catch (ClassCastException e) {

						mapUUIDNick = new HashMap<UUID,String>();
						mapNickUUID = new HashMap<String, UUID>();
						mapNickFormatted = new HashMap<String,String>();

					} catch (ObjectMappingException e) {

						mapUUIDNick = new HashMap<UUID,String>();
						mapNickUUID = new HashMap<String, UUID>();
						mapNickFormatted = new HashMap<String,String>();

					}

					try {

						configLoader.save(rootNode);

					} catch (IOException e) {

						e.printStackTrace();

					}

				} catch (IOException e) {

					e.printStackTrace();
					mapUUIDNick = new HashMap<UUID,String>();
					mapNickUUID = new HashMap<String, UUID>();
					mapNickFormatted = new HashMap<String,String>();

				}

				((SpongeFileNameManager)SpongeNameManager.getInstance()).loadFromFile(mapUUIDNick, mapNickUUID, mapNickFormatted);

			}

		}

		// Register channels

		channelRegistrar = Sponge.getGame().getChannelRegistrar();

		ChannelBinding.RawDataChannel commChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:comm");
		ChannelBinding.RawDataChannel chatChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:chat");

		ChannelBinding.RawDataChannel actionChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:act");
		ChannelBinding.RawDataChannel playerActionChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:pact");

		ChannelBinding.RawDataChannel prefixChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:prefix");
		ChannelBinding.RawDataChannel suffixChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:suffix");
		ChannelBinding.RawDataChannel displayNameChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:dn");
		ChannelBinding.RawDataChannel worldChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:world");
		ChannelBinding.RawDataChannel nickChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:nick");
		ChannelBinding.RawDataChannel channelChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "multichat:ch");
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
		MultiChatSponge.displayNameChannel = displayNameChannel;
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
				.executor(new SpongeNickCommand())
				.build();

		CommandSpec multichatspongeCommandSpec = CommandSpec.builder()
				.description(Text.of("MultiChatSponge command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("command"))))
				.permission("multichatsponge.admin")
				.executor(new MultiChatSpongeCommand())
				.build();

		CommandSpec realnameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Realname Command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("nickname"))))
				.permission("multichatsponge.realname")
				.executor(new SpongeRealnameCommand())
				.build();

		CommandSpec usernameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Username Command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("username"))))
				.permission("multichatsponge.username")
				.executor(new SpongeUsernameCommand())
				.build();

		Sponge.getCommandManager().register(this, nicknameCommandSpec, "nick");
		Sponge.getCommandManager().register(this, multichatspongeCommandSpec, "multichatsponge");
		Sponge.getCommandManager().register(this, realnameCommandSpec, "realname");
		Sponge.getCommandManager().register(this, usernameCommandSpec, "username");

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
		Sponge.getChannelRegistrar().unbindChannel(displayNameChannel);
		Sponge.getChannelRegistrar().unbindChannel(nickChannel);
		Sponge.getChannelRegistrar().unbindChannel(worldChannel);
		Sponge.getChannelRegistrar().unbindChannel(channelChannel);
		Sponge.getChannelRegistrar().unbindChannel(ignoreChannel);

		if (nicknameSQL) {
			try {
				DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().disconnectFromDatabase();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else {

			ConfigurationNode rootNode;

			rootNode = configLoader.createEmptyNode();

			try {

				rootNode.getNode("mapUUIDNick").setValue(new TypeToken<Map<UUID,String>>() {}, 
						((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapUUIDNick());
				rootNode.getNode("mapNickUUID").setValue(new TypeToken<Map<String,UUID>>() {}, 
						((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapNickUUID());
				rootNode.getNode("mapNickFormatted").setValue(new TypeToken<Map<String,String>>() {}, 
						((SpongeFileNameManager)SpongeNameManager.getInstance()).getMapNickFormatted());

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

		nickname = SpongeNameManager.getInstance().getCurrentName(player.getUniqueId());

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

			displayNameChannel.sendTo(player, buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(finalDisplayName));

		} else {

			displayNameChannel.sendTo(player, buffer -> buffer.writeUTF(player.getUniqueId().toString()).writeUTF(playername));

		}

	}

	/*public String stripAllFormattingCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");

	}*/

}
