package xyz.olivermartin.multichat.spongebridge.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.olivermartin.multichat.spongebridge.SpongeNameManager;
import xyz.olivermartin.multichat.spongebridge.SpongeSQLNameManager;

public class MultiChatSpongeCommand implements CommandExecutor {

	@SuppressWarnings("serial")
	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		if (sender instanceof Player) {
			sender.sendMessage(Text.of("This command can only be executed from the console!"));
			return CommandResult.success();
		}

		String commandString = args.<String>getOne("command").get();

		if (commandString.equalsIgnoreCase("migratetosql")) {

			if (! (SpongeNameManager.getInstance() instanceof SpongeSQLNameManager)) {
				sender.sendMessage(Text.of("This command can only be used in SQL mode!"));
				return CommandResult.success();
			}

			// Load nickname data from file

			File f = new File("multichat_namedata");

			if ((f.exists()) && (!f.isDirectory())) {

				ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(f).build();
				ConfigurationNode rootNode;

				Map<UUID, String> mapUUIDNick;
				Map<String, String> mapNickFormatted;

				try {

					rootNode = configLoader.load();

					try {

						mapUUIDNick = (Map<UUID, String>) rootNode.getNode("mapUUIDNick").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });
						mapNickFormatted = (Map<String, String>) rootNode.getNode("mapNickFormatted").getValue(new TypeToken<Map<String,String>>() { /* EMPTY */ });

						if (mapUUIDNick == null) {
							mapUUIDNick = new HashMap<UUID,String>();
							mapNickFormatted = new HashMap<String,String>();
							System.out.println("[MultiChatSponge] Created new nicknames maps for file storage!");
						}

						System.out.println("[MultiChatSponge] Nicknames appeared to load correctly!");

					} catch (ClassCastException e) {

						mapUUIDNick = new HashMap<UUID,String>();
						mapNickFormatted = new HashMap<String,String>();

					} catch (ObjectMappingException e) {

						mapUUIDNick = new HashMap<UUID,String>();
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
					mapNickFormatted = new HashMap<String,String>();

				}

				// PERFORM MIGRATION

				UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
				Collection<GameProfile> profiles = uss.getAll();

				int count = 0;
				int max = profiles.size();
				int checkcount = 25;
				int checkpoint = max/100*checkcount;

				for (GameProfile gp : profiles) {

					count++;
					if (count > checkpoint) {
						sender.sendMessage(Text.of("Completed " + checkcount + "% of migration..."));
						checkcount += 25;
						checkpoint = max/100*checkcount;
					}

					if (!gp.getName().isPresent()) continue;

					UUID uuid = gp.getUniqueId();
					String formattedName = gp.getName().get();
					String name = formattedName.toLowerCase();
					String nick;
					String formattedNick;

					if (mapUUIDNick.containsKey(uuid)) {
						nick = mapUUIDNick.get(uuid);
						formattedNick = mapNickFormatted.get(nick);
						if (formattedNick.equals(formattedName)) {
							nick = null;
							formattedNick = null;
						}
					} else {
						nick = null;
						formattedNick = null;
					}

					((SpongeSQLNameManager)SpongeNameManager.getInstance()).registerMigratedPlayer(uuid, name, formattedName, nick, formattedNick);

				}

				// /PERFORM MIGRATION

				sender.sendMessage(Text.of("Successfully migrated: " + max + " records"));	

				return CommandResult.success();

			} else {

				sender.sendMessage(Text.of("Could not find nickname file to process!"));
				return CommandResult.success();

			}

		} else {

			sender.sendMessage(Text.of("Incorrect usage... Try /multichatsponge migratetosql"));
			return CommandResult.success();

		}

	}

}
