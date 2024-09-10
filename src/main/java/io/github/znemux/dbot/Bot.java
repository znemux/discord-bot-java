package io.github.znemux.dbot;

import java.util.Collections;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.internal.utils.JDALogger;

public class Bot {
    
    public static void start(Object token) {
        JDALogger.setFallbackLoggerEnabled(false);
        var jda = JDABuilder.createLight((String)token, Collections.emptyList())
                .addEventListeners(new CommandAdapter())
                .build();
        addCommands(jda);
        System.out.println("Bot is ready!");
    }
    
    static void addCommands(JDA jda) {
        jda.updateCommands().addCommands(
            Commands.slash("kick", "Kick a user").setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                .addOption(OptionType.USER, "user", "The user to kick"),
            Commands.slash("timeout", "Time out a user").setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                .addOption(OptionType.USER, "user", "The user to time out", true)
                .addOption(OptionType.INTEGER, "for", "For how many", true)
                .addOption(OptionType.STRING, "timeunit", "Days, hours, etc", true, true),
            Commands.slash("banlist", "Get list of banned users").setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)),
            Commands.slash("ban", "Ban a user from the server").setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
                .addOption(OptionType.USER, "user", "The user to ban", true),
            Commands.slash("unban", "Unban a user from the server").setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
                .addOption(OptionType.STRING, "user", "The user to unban", true, true),
            Commands.slash("info", "System information"),
            Commands.slash("chatgpt", "Chat with me")
                .addOption(OptionType.STRING, "message", "Message to send", true)
        ).queue();
    }
    
}
