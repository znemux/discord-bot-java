package io.github.znemux.dbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import io.github.znemux.dbot.commands.GPT;
import static java.lang.System.err;
import static java.lang.System.getProperty;

public class Main {
    
    static String[] ARGS;
        
    public static void main(String[] args) {
        ARGS = args;
        var conf = getConfig();
        GPT.setKey(conf.get("OPENAI_KEY"));
        Bot.start(conf.get("DISCORD_TOKEN"));
    }
    
    static Map getConfig() {
        boolean cliArg = ARGS.length > 0;
        String file = cliArg ? ARGS[0] : "keys.json";
        var path = configFolder(cliArg).resolve(file);
        try {
            var content = Files.readString(path);
            var object = new JSONObject(content);
            return object.toMap();
        } catch (IOException e) {
            err.println("I need a JSON that has DISCORD_TOKEN here: "+path.toString()+". Example:");
            err.println("{\n  \"DISCORD_TOKEN\": \"your_discord_bot_token\"\n  \"OPENAI_KEY\": \"your_openai_developer_key\"\n}");
            return new HashMap();
        }
    }
    
    static Path configFolder(boolean cliArg) {
        String dir = cliArg ? "user.dir" : "user.home";
        Path path = Path.of(getProperty(dir));
        if (!cliArg) {
            path = path.resolve(getProperty("os.name").startsWith("Win") ? "AppData/Local" : ".local/share");
            path = path.resolve("Nemux").resolve("Discord");
        }
        return path;
    }
    
    /*Map getEnvs() {
        var map = new HashMap<String, String>(2);
        var token = System.getenv("DISCORD_TOKEN");
        var key = System.getenv("OPENAI_KEY");
        map.put("DISCORD_TOKEN", token);
        map.put("OPENAI_KEY", key);
        return map;
    }*/
    
}
