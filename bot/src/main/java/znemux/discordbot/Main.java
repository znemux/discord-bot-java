package znemux.discordbot;

/**
 *
 * @author znemux
 */
public class Main {
    
    public static void main(String[] args) {
        var token = getToken(args);
        Bootstrap.initialize(token);
    }
    
    static String getToken(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        var env = System.getenv("DISCORD_TOKEN");
        if (env != null) {
            return env;
        }
        System.err.println("Cannot get bot token");
        return null;
    }
    
    public static String getAIKey() {
        var env = System.getenv("OPENAI_KEY");
        if (env != null) {
            return env;
        }
        System.err.println("Couldn't get OpenAI API key, please set OPENAI_KEY environment variable");
        return null;
    }
    
}
