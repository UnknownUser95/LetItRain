package net.unknownuser.letitrain;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public class ConfigDeserializer extends TypeAdapter<Config> {
    @Override
    public void write(final JsonWriter writer, final Config config) throws IOException {
        if (config == null) {
            writer.nullValue();
            return;
        }
        
        writer.setFormattingStyle(FormattingStyle.PRETTY);
        
        writer.beginObject();
        
        writer
            .name("keepRainChance")
            .value(config.keepRainChance)
            .name("keepThunderChance")
            .value(config.keepThunderChance)
            .name("preserveWeatherTime")
            .value(config.preserveWeatherTime)
            .name("logRolls")
            .value(config.logRolls)
            .name("resetThunderOnSleep")
            .value(config.resetThunderOnSleep);
        
        writer.endObject();
    }
    
    @Override
    public Config read(final JsonReader reader) throws IOException {
        int     keepRainChance      = Config.Defaults.KEEP_RAIN_CHANCE;
        int     keepThunderChance   = Config.Defaults.KEEP_THUNDER_CHANCE;
        boolean preserveWeatherTime = Config.Defaults.PRESERVE_WEATHER_TIME;
        boolean logRolls            = Config.Defaults.LOG_ROLLS;
        boolean resetThunderOnSleep = Config.Defaults.RESET_THUNDER_ON_SLEEP;
        
        reader.beginObject();
        
        while (reader.hasNext()) {
            final String fieldName = reader.nextName();
            
            switch (fieldName) {
                case "keepRainChance" -> keepRainChance = reader.nextInt();
                case "keepThunderChance" -> keepThunderChance = reader.nextInt();
                case "preserveWeatherTime" -> preserveWeatherTime = reader.nextBoolean();
                case "logRolls" -> logRolls = reader.nextBoolean();
                case "resetThunderOnSleep" -> resetThunderOnSleep = reader.nextBoolean();
                default -> LetItRain.LOGGER.warn("Unknown config key: {}", fieldName);
            }
        }
        
        reader.endObject();
        
        return new Config(
            keepRainChance,
            keepThunderChance,
            preserveWeatherTime,
            logRolls,
            resetThunderOnSleep
        );
    }
}
