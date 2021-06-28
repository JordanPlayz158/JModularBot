package xyz.jordanplayz158.jmodularbot.json;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;

public class Config extends Template {
    public Config(File file) {
        super(file);
    }

    public String getLogLevel() {
        return json.get("logLevel").getAsString();
    }

    public String getPrefix() {
        return json.get("prefix").getAsString();
    }

    public JsonObject getActivity() {
        return json.getAsJsonObject("activity");
    }

    public String getActivityName() {
        return getActivity().get("name").getAsString();
    }

    public Activity.ActivityType getActivityType() {
        return Activity.ActivityType.valueOf(getActivity().get("type").getAsString().toUpperCase());
    }
}
