package znemux.discordbot;

import znemux.discordbot.commands.GPT3;
import znemux.discordbot.commands.Moderation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

/**
 *
 * @author znemux
 */
public class CommandAdapter extends ListenerAdapter {

    public static SlashCommandInteractionEvent event;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandAdapter.event = event;
        switch (event.getName()) {
            case "ban" -> Moderation.ban();
            case "unban" -> Moderation.unban();
            case "banlist" -> Moderation.banlist();
            case "timeout" -> Moderation.timeout();
            case "kick" -> Moderation.kick();
            //case "purge" -> Moderation.purge();
            case "gpt3" -> GPT3.gpt();
            default -> event.reply("Not implemented yet").setEphemeral(true).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        var stream = Stream.of(new String[0]);
        if (event.getName().equals("unban") && event.getFocusedOption().getName().equals("user")) {
            var bans = event.getGuild().retrieveBanList();
            var banList = new ArrayList<String>();
            for (var ban : bans) {
            banList.add(ban.getUser().getId() + "   -   " + ban.getUser().getGlobalName() + " (" + ban.getUser().getName() + ")");
            }
            stream = banList.stream();
        }
        if (event.getName().equals("timeout") && event.getFocusedOption().getName().equals("timeunit")) {
            String[] timeUnits = {"seconds", "minute(s)", "hour(s)", "day(s)"};
            stream = Stream.of(timeUnits);
        }
        List<Command.Choice> options = stream
            .filter(item -> item.startsWith(event.getFocusedOption().getValue()))
            .map(item -> new Command.Choice(item, item))
            .collect(Collectors.toList());
        event.replyChoices(options).queue();
    }
    
}
