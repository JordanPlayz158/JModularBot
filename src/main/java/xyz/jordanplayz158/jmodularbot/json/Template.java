package xyz.jordanplayz158.jmodularbot.json;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.jordanplayz158.utils.LoadJson;

import java.io.File;

@Getter
public abstract class Template {
    public final File file;
    public JsonObject json;

    public Template(File file) {
        this.file = file;
    }

    public void load() {
        json = LoadJson.linkedTreeMap(file);
    }
}
