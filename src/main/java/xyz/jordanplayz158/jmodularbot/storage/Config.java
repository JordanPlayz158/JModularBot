package xyz.jordanplayz158.jmodularbot.storage;

import lombok.Getter;
import java.util.Map;

@Getter
public class Config {
    private String logLevel;
    private String token;
    private String prefix;
    private Map<String, String> activity;
}
