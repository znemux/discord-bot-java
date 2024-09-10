package io.github.znemux.dbot.commands;

import java.io.IOException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.SplitUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static io.github.znemux.dbot.CommandAdapter.event;

public class GPT {
    
    private final static int CHAR_LIMIT = 1984;
    private final static OkHttpClient client = new OkHttpClient();
    
    private static String key;
    public static void setKey(Object key) { GPT.key = (String)key; }

    public static void chatgpt() {
        if (event.isAcknowledged()) return;
        if (key != null) askgpt();
        else event.reply("OpenAI key not provided with OPENAI_KEY environment variable").queue();
    }

    private static void askgpt() {
        event.deferReply().queue();
        var request = formatMessage(event.getOption("message", OptionMapping::getAsString));
        var response = getResponse(makeRequest(request));
        if (response.length() <= CHAR_LIMIT) {
            event.getHook().editOriginal(response).queue();
            return;
        }
        var chunks = SplitUtil.split(response, 1984, SplitUtil.Strategy.NEWLINE);
        event.getHook().editOriginal(chunks.get(0)).queue();
        for (int i = 1; i < chunks.size(); i++) {
            event.reply(chunks.get(i)).queue();
        }
    }

    private static String formatMessage(String message) {
        var builder = new StringBuilder();
        builder.append("<message")
                .append(" id=").append(event.getMember().getId())
                .append(" username=").append(event.getMember().getEffectiveName())
                .append(" nickname=").append(event.getMember().getNickname())
                .append(" body=").append(message.trim())
                .append("/>");
        return builder.toString();
    }

    private static Request makeRequest(final String message) {
        var requestBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", message)))
                .put("max_tokens", 1024);
        var body = RequestBody.create(
                requestBody.toString(), MediaType.parse("application/json"));
        var request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + key)
                .post(body)
                .build();
        return request;
    }

    private static String getResponse(final Request request) {
        try {
            var response = client.newCall(request).execute();
            var jsonObject = new JSONObject(response.body().string());
            if (jsonObject.has("choices")) {
                var choices = jsonObject.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    var messageObject = choices.getJSONObject(0).optJSONObject("message");
                    if (messageObject != null && messageObject.has("content")) {
                        return messageObject.getString("content");
                    }
                }
            }
            return "```json\n"+jsonObject.toString(4)+"```";
        } catch (IOException | JSONException ex) {
            System.err.println(ex.getMessage());
            return "Sorry, I couldn't process your request.";
        }
    }
}
