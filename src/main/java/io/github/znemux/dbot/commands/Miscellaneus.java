package io.github.znemux.dbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import static io.github.znemux.dbot.CommandAdapter.event;

/**
 *
 * @author znemux
 */
public class Miscellaneus {
    
    static EmbedBuilder embedb = new EmbedBuilder();
    
    public static void info() {
        embedb.setTitle("System information");
        embedb.setDescription("");
        embedb.addField("OS", System.getProperty("os.name") + "\n" + System.getProperty("os.arch"), true);
        embedb.addField("JVM", System.getProperty("java.vm.name").split(" ")[0] + " " + System.getProperty("java.version") + "\n" +System.getProperty("java.vendor"), true);
        event.replyEmbeds(embedb.build()).queue();
    }
    
}
