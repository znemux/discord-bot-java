package znemux.discordbot.commands;

import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import static znemux.discordbot.CommandAdapter.event;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author znemux
 */
public class Moderation {

    public static void ban() {
        if (!isAllowedToBan()) {
            return;
        }
        var target = event.getOption("user", OptionMapping::getAsUser);
        event.getGuild().ban(target, 0, TimeUnit.HOURS).queue(v -> {
            event.reply("Banned " + target.getAsMention()).queue();
        }, error -> {
            event.reply("Error while banning " + target.getAsMention()).queue();
        });
    }

    public static void unban() {
        if (!isAllowedToBan()) {
            return;
        }
        var target = event.getOption("user", OptionMapping::getAsString).replace("<@", "").replace(">", "").split("   -   ")[0];
        var banList = event.getGuild().retrieveBanList();
        
        User user = null;
        for (var ban : banList) {
            if (target.equals(ban.getUser().getId())) {
                user = ban.getUser(); break;
            }
        }
        if (user == null) {
            event.reply("Cannot find user").setEphemeral(true).queue();
            return;
        }
        var finalTarget = user;
        event.getGuild().unban(finalTarget).queue(v -> {
            event.reply("User " + finalTarget.getAsMention() + " is now unbanned").queue();
        }, error -> {
            event.reply("Error while unbanning " + finalTarget.getAsMention()).queue();
        });
    }

    public static void timeout() {
        if (!isAllowedToModerate()) {
            return;
        }
        var target = event.getOption("user", OptionMapping::getAsUser);
        var quantity = event.getOption("for", OptionMapping::getAsLong);
        var timeUnit = event.getOption("timeunit", OptionMapping::getAsString);
        var trueTimeUnit = switch (timeUnit) {
            case "day(s)" -> TimeUnit.DAYS;
            case "hour(s)" -> TimeUnit.HOURS;
            case "minute(s)" -> TimeUnit.MINUTES;
            case "seconds" -> TimeUnit.SECONDS;
            default -> null;
        };
        if (trueTimeUnit == null) {
            event.reply("Not a valid time unit").setEphemeral(true).queue();
        }
        event.getGuild().timeoutFor(target, quantity, trueTimeUnit).queue(v -> {
            event.reply(target.getAsMention() + " timed out for " + quantity + " " + timeUnit).queue();
        }, error -> {
            event.reply("Error while timing out " + target.getAsMention()).queue();
        });
    }
    
    public static void kick() {
        if (!isAllowedToKick()) {
            return;
        }
        var target = event.getOption("user", OptionMapping::getAsUser);
        event.getGuild().kick(target).queue(v -> {
            event.reply("Kicked " + target.getAsMention()).queue();
        }, error -> {
            event.reply("Error while kicking " + target.getAsMention() +" (Not in guild?)").queue();
        });
    }

    public static void banlist() {
        var banList = event.getGuild().retrieveBanList();
        var message = new String();
        for (var ban : banList) {
            message += ban.getUser().getId() + "   -   " + ban.getUser().getAsMention() + "\n";
        }
        if (message.isBlank()) {
            message = "(empty)";
        }
        event.reply("### Ban List\n"+message).setEphemeral(true).queue();
    }
    
    public static void purge() {
        var user = event.getOption("user", OptionMapping::getAsUser);
        event.getChannel().purgeMessagesById(user.getId());
    }
    
    static boolean isAllowedToBan() {
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You do not have the appropriate permissions to ban").setEphemeral(true).queue();
            return false;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("I do not have the appropriate permissions to ban").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    static boolean isAllowedToModerate() {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You do not have the appropriate permissions to moderate").setEphemeral(true).queue();
            return false;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("I do not have the appropriate permissions to moderate").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    
    static boolean isAllowedToKick() {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You do not have the appropriate permissions to kick").setEphemeral(true).queue();
            return false;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("I do not have the appropriate permissions to kick").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

}
