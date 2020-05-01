package xyz.olivermartin.multichat.local.sponge;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import me.rojo8399.placeholderapi.PlaceholderService;
import xyz.olivermartin.multichat.common.DataStoreMode;
import xyz.olivermartin.multichat.common.MultiChatInfo;
import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.LocalMetaManager;
import xyz.olivermartin.multichat.local.common.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfigManager;
import xyz.olivermartin.multichat.local.common.storage.LocalDataStore;
import xyz.olivermartin.multichat.local.common.storage.LocalDatabaseSetupManager;
import xyz.olivermartin.multichat.local.common.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.common.storage.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;
import xyz.olivermartin.multichat.local.common.storage.LocalSQLNameManager;
import xyz.olivermartin.multichat.local.sponge.commands.MultiChatLocalSpongeCommand;
import xyz.olivermartin.multichat.local.sponge.commands.SpongeNickCommand;
import xyz.olivermartin.multichat.local.sponge.commands.SpongeProxyExecuteCommand;
import xyz.olivermartin.multichat.local.sponge.commands.SpongeRealnameCommand;
import xyz.olivermartin.multichat.local.sponge.commands.SpongeUsernameCommand;
import xyz.olivermartin.multichat.local.sponge.hooks.LocalSpongePAPIHook;
import xyz.olivermartin.multichat.local.sponge.listeners.LocalSpongeLoginLogoutListener;
import xyz.olivermartin.multichat.local.sponge.listeners.LocalSpongeWorldChangeListener;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeGameReloadEvent;
import xyz.olivermartin.multichat.local.sponge.listeners.chat.LocalSpongeChatListenerHighest;
import xyz.olivermartin.multichat.local.sponge.listeners.chat.LocalSpongeChatListenerLowest;
import xyz.olivermartin.multichat.local.sponge.listeners.chat.LocalSpongeChatListenerMonitor;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongeActionListener;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongeCastListener;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongeIgnoreListener;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongePlayerActionListener;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongePlayerChannelListener;
import xyz.olivermartin.multichat.local.sponge.listeners.communication.LocalSpongePlayerMetaListener;

@Plugin(id = "multichat", name = "MultiChatSponge", version = MultiChatInfo.LATEST_VERSION, dependencies = { @Dependency(id = "placeholderapi", optional = true) })
public class MultiChatLocalSpongePlugin {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;

	@Listener
	public void onServerStart(GameStartedServerEvent event) {

		// GET API
		MultiChatLocal api = MultiChatLocal.getInstance();

		// Register console logger
		LocalConsoleLogger consoleLogger = new LocalSpongeConsoleLogger();
		api.registerConsoleLogger(consoleLogger);

		// Register platform
		MultiChatLocalPlatform platform = MultiChatLocalPlatform.SPONGE;
		api.registerPlatform(platform);

		// Register name
		String pluginName = "multichat";
		api.registerPluginName(pluginName);

		// Register version
		String pluginVersion = MultiChatInfo.LATEST_VERSION;
		api.registerVersion(pluginVersion);

		// Register config directory
		File configDir = privateConfigDir.toFile();
		if (!configDir.exists()) {
			consoleLogger.log("Creating plugin directory...");
			configDir.mkdirs();
		}
		api.registerConfigDirectory(configDir);

		// Create directory for translations
		String translationsDirString = configDir.toString() + File.separator + "translations";
		File translationsDir = new File(translationsDirString);
		if (!translationsDir.exists()) {
			consoleLogger.log("Creating translations directory...");
			translationsDir.mkdirs();
		}

		// Register config manager
		LocalConfigManager configMan = new LocalConfigManager(platform);
		api.registerConfigManager(configMan);

		// Register config files
		configMan.registerLocalConfig("localconfig.yml", configDir);

		// Register data store
		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		// Register name manager...
		LocalNameManager nameManager;

		if (configMan.getLocalConfig().isNicknameSQL()) {

			String databaseName = "multichatlocal.db";
			LocalDatabaseSetupManager ldsm = new LocalDatabaseSetupManager(databaseName, configMan.getLocalConfig().isMySQL());

			if (ldsm.isConnected()) {
				nameManager = new LocalSQLNameManager(databaseName);
			} else {
				consoleLogger.log("Could not connect to database! Using file based storage instead...");
				nameManager = new LocalSpongeFileNameManager();
			}

		} else {

			nameManager = new LocalSpongeFileNameManager();

		}

		api.registerNameManager(nameManager);

		LocalFileSystemManager fileSystemManager = new LocalFileSystemManager();
		api.registerFileSystemManager(fileSystemManager);

		// If we are using file based storage for name data, then register and load the nickname file into name manager
		if (nameManager.getMode() == DataStoreMode.FILE) {
			fileSystemManager.registerNicknameFile(platform, "namedata.dat", configDir, (LocalFileNameManager)nameManager);
		}

		// Copy translations files...
		fileSystemManager.createResource("localconfig_fr.yml", translationsDir);

		// Register meta manager
		LocalMetaManager metaManager = new LocalSpongeMetaManager();
		api.registerMetaManager(metaManager);

		// Register communication manager
		SpongeBungeeCommunicationManager proxyCommunicationManager = new SpongeBungeeCommunicationManager();
		api.registerProxyCommunicationManager(proxyCommunicationManager);

		// Register plugin communication channels
		registerCommunicationChannels(proxyCommunicationManager);

		// Register placeholder manager
		LocalPlaceholderManager placeholderManager = new LocalSpongePlaceholderManager();
		api.registerPlaceholderManager(placeholderManager);

		// Register chat manager
		LocalChatManager chatManager = new LocalSpongeChatManager();
		api.registerChatManager(chatManager);

		// Register Listeners
		Sponge.getEventManager().registerListeners(this, new LocalSpongeWorldChangeListener());
		Sponge.getEventManager().registerListeners(this, new LocalSpongeLoginLogoutListener());
		Sponge.getEventManager().registerListeners(this, new LocalSpongeChatListenerHighest());
		Sponge.getEventManager().registerListeners(this, new LocalSpongeChatListenerLowest());
		Sponge.getEventManager().registerListeners(this, new LocalSpongeChatListenerMonitor());

		Sponge.getEventManager().registerListeners(this, new SpongeGameReloadEvent());

		// Register Commands

		CommandSpec nicknameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Nickname Command"))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
						GenericArguments.remainingJoinedStrings(Text.of("message")))
				.permission("multichatlocal.nick.self")
				.executor(new SpongeNickCommand())
				.build();

		CommandSpec multichatlocalCommandSpec = CommandSpec.builder()
				.description(Text.of("MultiChatLocal command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("command"))))
				.permission("multichatlocal.admin")
				.executor(new MultiChatLocalSpongeCommand())
				.build();

		CommandSpec realnameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Realname Command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("nickname"))))
				.permission("multichatlocal.realname")
				.executor(new SpongeRealnameCommand())
				.build();

		CommandSpec usernameCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Username Command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("username"))))
				.permission("multichatlocal.username")
				.executor(new SpongeUsernameCommand())
				.build();

		CommandSpec pexecuteCommandSpec = CommandSpec.builder()
				.description(Text.of("Sponge Proxy Execute Command"))
				.arguments(
						GenericArguments.remainingJoinedStrings(Text.of("message")))
				.permission("multichatlocal.pexecute")
				.executor(new SpongeProxyExecuteCommand())
				.build();

		Sponge.getCommandManager().register(this, nicknameCommandSpec, "nick");
		Sponge.getCommandManager().register(this, multichatlocalCommandSpec, "multichatlocal");
		Sponge.getCommandManager().register(this, realnameCommandSpec, "realname");
		Sponge.getCommandManager().register(this, usernameCommandSpec, "username");
		Sponge.getCommandManager().register(this, pexecuteCommandSpec, "pexecute", "pxe");

		// Manage Dependencies
		try {
			Optional<PlaceholderService> papi = Sponge.getServiceManager().provide(PlaceholderService.class);
			if (papi.isPresent()) {
				LocalSpongePAPIHook.getInstance().hook(papi.get());
			}
		} catch (NoClassDefFoundError e) { /* EMPTY */ }

	}

	private void registerCommunicationChannels(SpongeBungeeCommunicationManager commManager) {

		ChannelRegistrar channelRegistrar = Sponge.getGame().getChannelRegistrar();

		ChannelBinding.RawDataChannel commChannel = channelRegistrar.createRawChannel(this, "multichat:comm");
		commManager.registerChannel("multichat:comm", commChannel);
		ChannelBinding.RawDataChannel chatChannel = channelRegistrar.createRawChannel(this, "multichat:chat");
		commManager.registerChannel("multichat:chat", chatChannel);

		ChannelBinding.RawDataChannel actionChannel = channelRegistrar.createRawChannel(this, "multichat:act");
		commManager.registerChannel("multichat:act", actionChannel);
		ChannelBinding.RawDataChannel playerActionChannel = channelRegistrar.createRawChannel(this, "multichat:pact");
		commManager.registerChannel("multichat:pact", playerActionChannel);

		ChannelBinding.RawDataChannel prefixChannel = channelRegistrar.createRawChannel(this, "multichat:prefix");
		commManager.registerChannel("multichat:prefix", prefixChannel);
		ChannelBinding.RawDataChannel suffixChannel = channelRegistrar.createRawChannel(this, "multichat:suffix");
		commManager.registerChannel("multichat:suffix", suffixChannel);
		ChannelBinding.RawDataChannel displayNameChannel = channelRegistrar.createRawChannel(this, "multichat:dn");
		commManager.registerChannel("multichat:dn", displayNameChannel);
		ChannelBinding.RawDataChannel worldChannel = channelRegistrar.createRawChannel(this, "multichat:world");
		commManager.registerChannel("multichat:world", worldChannel);
		ChannelBinding.RawDataChannel nickChannel = channelRegistrar.createRawChannel(this, "multichat:nick");
		commManager.registerChannel("multichat:nick", nickChannel);
		ChannelBinding.RawDataChannel channelChannel = channelRegistrar.createRawChannel(this, "multichat:ch");
		commManager.registerChannel("multichat:ch", channelChannel);
		ChannelBinding.RawDataChannel ignoreChannel = channelRegistrar.createRawChannel(this, "multichat:ignore");
		commManager.registerChannel("multichat:ignore", ignoreChannel);

		ChannelBinding.RawDataChannel pexecuteChannel = channelRegistrar.createRawChannel(this, "multichat:pxe");
		commManager.registerChannel("multichat:pxe", pexecuteChannel);
		ChannelBinding.RawDataChannel ppexecuteChannel = channelRegistrar.createRawChannel(this, "multichat:ppxe");
		commManager.registerChannel("multichat:ppxe", ppexecuteChannel);

		commChannel.addListener(Platform.Type.SERVER, new LocalSpongePlayerMetaListener());
		chatChannel.addListener(Platform.Type.SERVER, new LocalSpongeCastListener());
		channelChannel.addListener(Platform.Type.SERVER, new LocalSpongePlayerChannelListener());
		ignoreChannel.addListener(Platform.Type.SERVER, new LocalSpongeIgnoreListener());

		actionChannel.addListener(Platform.Type.SERVER, new LocalSpongeActionListener());
		playerActionChannel.addListener(Platform.Type.SERVER, new LocalSpongePlayerActionListener());

	}

	@Listener
	public void onServerStop(GameStoppingServerEvent event) {

		SpongeBungeeCommunicationManager commManager = (SpongeBungeeCommunicationManager)MultiChatLocal.getInstance().getProxyCommunicationManager();

		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:comm"));
		commManager.unregisterChannel("multichat:comm");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:chat"));
		commManager.unregisterChannel("multichat:chat");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:act"));
		commManager.unregisterChannel("multichat:act");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:pact"));
		commManager.unregisterChannel("multichat:pact");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:prefix"));
		commManager.unregisterChannel("multichat:prefix");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:suffix"));
		commManager.unregisterChannel("multichat:suffix");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:dn"));
		commManager.unregisterChannel("multichat:dn");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:nick"));
		commManager.unregisterChannel("multichat:nick");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:world"));
		commManager.unregisterChannel("multichat:world");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:ch"));
		commManager.unregisterChannel("multichat:ch");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:ignore"));
		commManager.unregisterChannel("multichat:ignore");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:pxe"));
		commManager.unregisterChannel("multichat:pxe");
		Sponge.getChannelRegistrar().unbindChannel(commManager.getChannel("multichat:ppxe"));
		commManager.unregisterChannel("multichat:ppxe");

		if (MultiChatLocal.getInstance().getNameManager().getMode() == DataStoreMode.SQL) {

			try {
				DatabaseManager.getInstance().getDatabase("multichatsponge.db").get().disconnectFromDatabase();
			} catch (SQLException e) {
				MultiChatLocal.getInstance().getConsoleLogger().log("Error when disconnecting from database!");
			}

		} else {

			MultiChatLocal.getInstance().getFileSystemManager().getNicknameFile().save();

		}

	}

}
