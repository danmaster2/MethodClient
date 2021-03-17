package Method.Client.utils;

import Method.Client.module.render.MobOwner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;

public class PlayerIdentity implements Serializable {
    private String displayName;
    private final String stringUuid;

    public PlayerIdentity(String stringUuid) {
        String formattedUuid = stringUuid.replace("-", "");
        this.stringUuid = stringUuid;
        this.displayName = "Loading...";
        new Thread(() -> {
            this.displayName = getName(formattedUuid);
            MobOwner.identityCacheMap.put(this.getStringUuid(), this);
        }).start();
    }

    private static String getName(String UUID) {
        try {
            URL e = new URL("https://api.mojang.com/user/profiles/" + UUID.replace("-", "") + "/names");
            BufferedReader reader = new BufferedReader(new InputStreamReader(e.openConnection().getInputStream()));
            StringBuilder jsonb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonb.append(line).append("\n");
            }
            String formattedjson = jsonb.toString();
            reader.close();

            JsonArray array = new JsonParser().parse(formattedjson).getAsJsonArray();
            JsonObject obj = array.get(array.size() - 1).getAsJsonObject();
            String nameform = obj.get("name").getAsString();
            try {
                obj.get("changedToAt");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(obj.get("changedToAt").getAsLong());
                return nameform;
            } catch (Exception ee) {
                return nameform;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UUID;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getStringUuid() {
        return this.stringUuid;
    }

}

