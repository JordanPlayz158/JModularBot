package me.jordanplayz158.jmodularbot.json;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.jordanplayz158.utils.LoadJson;

import java.io.File;

@Getter
public abstract class Template {
    public final File jsonFile;
    public JsonObject json;

    public Template(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    public void loadJson() {
        json = LoadJson.linkedTreeMap(jsonFile);
    }
}
