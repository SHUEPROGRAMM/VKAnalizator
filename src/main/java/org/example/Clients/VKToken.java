package org.example.Clients;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.General;
import org.example.VKRequest.RequestsGet;
import org.example.VKRequest.RequestsGetID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class VKToken {
    public static class VkError extends RuntimeException {
        private Integer errorCode;

        public VkError(String message) { super(message); }

        public VkError(Throwable cause) { super(cause); }

        public VkError(String message, Throwable throwable) { super(message, throwable); }

        public VkError(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public Integer getErrorCode() { return errorCode; }
    }

    private static class DB {
        public final long date;
        public final JsonObject data;

        public DB(long date, JsonObject data) {
            this.date = date;
            this.data = data;
        }
    }

    public final int id;
    public final String accessToken;
    private final HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
    private long back = 0;

    public VKToken(int id, String string) {
        this.id = id;
        this.accessToken = string;
    }

    @Override
    public String toString() {
        return Integer.toString(id) + ":" + accessToken;
    }

    private String requestsBase(String url) throws InterruptedException {
        long time = General.runtimeMXBean.getStartTime() - back;
        back = General.runtimeMXBean.getStartTime();
        if (time < 200) { Thread.sleep(200 - time); }

        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DB requests(String db) throws InterruptedException {
        JsonObject buffer = JsonParser.parseString(requestsBase("https://api.vk.com/method/" + db + "&access_token=" + accessToken + "&v=5.131")).getAsJsonObject();
        long date = new Date().getTime();

        if (buffer.has("error")) {
            JsonObject error = buffer.getAsJsonObject("error");
            throw new VkError(error.get("error_msg").getAsString(), error.get("error_code").getAsInt());
        } return new DB(date, buffer);
    }

    private DB friendsGet(int id, int offset) throws InterruptedException {
        return requests("friends.get?user_id=" + Integer.toString(id) + "&offset=" + Integer.toString(offset) + "&count=5000" + "&fields=bdate,city,contacts,country,domain,education,timezone,last_seen,nickname,online,relation,sex,status,universities");
    }

    public RequestsGetID friendsGet(int id) throws InterruptedException {
        ArrayList<RequestsGet.Node> buffer = new ArrayList<>();
        int count = 0, offset = 0;
        long date = new Date().getTime();

        while (true) {
            DB db = friendsGet(id, offset);
            try {
                buffer.add(new RequestsGet.Node(db.date, db.data.get("response").getAsJsonObject().get("items").getAsJsonArray()));
            } catch (VkError e) {
                if (e.getErrorCode() == 6 || e.getErrorCode() == 9) {
                    return new RequestsGetID(date, buffer, id, false);
                } else throw e;
            }

            count = db.data.get("response").getAsJsonObject().get("count").getAsInt();
            offset += 5000;
            if (offset >= count) break;
        } return new RequestsGetID(date, buffer, id, true);
    }
}
