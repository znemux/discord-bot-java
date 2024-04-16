package znemux.discordbot.commands;

import static znemux.discordbot.CommandAdapter.event;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.SplitUtil;
import org.json.JSONArray;

/**
 *
 * @author znemux
 */
public class GPT3 {

    final static OkHttpClient client = new OkHttpClient();
    static String key = null;

    public static void setKey(String key) {
        GPT3.key = key;
    }

    public static void gpt() {
        if (event.isAcknowledged()) return;
        
        if (key == null) {
            event.reply("OpenAI key not provided with OPENAI_KEY environment variable").queue();
            return;
        }
        
        int limit = 1984;
        
        event.deferReply().queue();
        var request = event.getOption("message", OptionMapping::getAsString);
        var response = getResponse(request);
        
        if (response.length() <= limit) {
            event.getHook().editOriginal(response).queue();
            return;
        }
        var chunks = SplitUtil.split(response, 1984, SplitUtil.Strategy.NEWLINE);
        event.getHook().editOriginal(chunks.get(0)).queue();
        for (int i = 1; i < chunks.size(); i++) {
            event.reply(chunks.get(i)).queue();
        }
        
    }

    static String getResponse(String message) {
        try {
            var requestBody = new JSONObject()
                    .put("model", "gpt-3.5-turbo")
                    .put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", message)))
                    .put("max_tokens", 1024);
            var body = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());
            var request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + key)
                    .post(body)
                    .build();
            var response = client.newCall(request).execute();
            var jsonResponse = response.body().string();
            System.out.println("JSON Response: " + jsonResponse); // Log JSON response
            var jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has("choices")) {
                var choicesArray = jsonObject.getJSONArray("choices");
                if (!choicesArray.isEmpty()) {
                    var messageObject = choicesArray.getJSONObject(0).optJSONObject("message");
                    if (messageObject != null && messageObject.has("content")) {
                        System.out.println(messageObject.getString("content"));
                        return messageObject.getString("content");
                    }
                }
            }
            return "API response does not contain 'text' field.";
        } catch (IOException | JSONException ex) {
            System.err.println(ex.getMessage());
            return "Sorry, I couldn't process your request.";
        }
    }

}
