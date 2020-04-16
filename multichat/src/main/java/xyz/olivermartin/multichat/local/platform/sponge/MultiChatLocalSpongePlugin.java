package xyz.olivermartin.multichat.local.platform.sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import me.rojo8399.placeholderapi.PlaceholderService;
import xyz.olivermartin.multichat.local.LocalChatManager;
import xyz.olivermartin.multichat.local.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.LocalMetaManager;
import xyz.olivermartin.multichat.local.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.LocalProxyCommunicationManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.config.LocalConfigManager;
import xyz.olivermartin.multichat.local.platform.sponge.commands.MultiChatLocalSpongeCommand;
import xyz.olivermartin.multichat.local.platform.sponge.commands.SpongeNickCommand;
import xyz.olivermartin.multichat.local.platform.sponge.commands.SpongeProxyExecuteCommand;
import xyz.olivermartin.multichat.local.platform.sponge.commands.SpongeRealnameCommand;
import xyz.olivermartin.multichat.local.platform.sponge.commands.SpongeUsernameCommand;
import xyz.olivermartin.multichat.local.platform.sponge.hooks.LocalSpongePAPIHook;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;
import xyz.olivermartin.multichat.local.storage.LocalDatabaseSetupManager;
import xyz.olivermartin.multichat.local.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.storage.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.storage.LocalNameManager;
import xyz.olivermartin.multichat.local.storage.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.storage.LocalSQLNameManager;

@Plugin(id = "multichat", name = "MultiChatSponge", version = "1.8.1", dependencies = { @Dependency(id = "placeholderapi", optional = true) })
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
		String pluginName = "multichat"; // NOT SURE IF THIS SHOULD BE MultiChatSponge??
		api.registerPluginName(pluginName);

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
		LocalConfigManager configMan = new LocalConfigManager();
		api.registerConfigManager(configMan);

		// Register config files
		configMan.registerLocalConfig(platform, "multichatsponge.yml", configDir);

		// Register data store
		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		// Register name manager...
		LocalNameManager nameManager;

		if (configMan.getLocalConfig().isNicknameSQL()) {

			LocalDatabaseSetupManager ldsm = new LocalDatabaseSetupManager(platform, configMan.getLocalConfig().isMySQL());

			if (ldsm.isConnected()) {
				nameManager = new LocalSQLNameManager("multichatsponge.db");
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
		if (nameManager.getMode() == LocalNameManagerMode.FILE) {
			fileSystemManager.registerNicknameFile(platform, "multichat_namedata", configDir, (LocalFileNameManager)nameManager);
		}

		// Copy translations files...
		fileSystemManager.createResource("multichatsponge_fr.yml", translationsDir);

		// Register meta manager
		LocalMetaManager metaManager = new LocalSpongeMetaManager();
		api.registerMetaManager(metaManager);

		// Register plugin communication channels
		// TODO registerCommunicationChannels(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		// Register communication manager
		LocalProxyCommunicationManager proxyCommunicationManager = new SpongeBungeeCommunicationManager();
		api.registerProxyCommunicationManager(proxyCommunicationManager);

		// Register placeholder manager
		LocalPlaceholderManager placeholderManager = new LocalSpongePlaceholderManager();
		api.registerPlaceholderManager(placeholderManager);

		// Register chat manager
		LocalChatManager chatManager = new LocalSpongeChatManager();
		api.registerChatManager(chatManager); // TODO Issues here...

		// Register Listeners
		// TODO!

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

}
