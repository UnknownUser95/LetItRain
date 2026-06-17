package net.unknownuser.letitrain.mixins;

import net.minecraft.server.level.*;
import net.minecraft.world.level.saveddata.*;
import net.unknownuser.letitrain.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static net.unknownuser.letitrain.LetItRain.*;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
    
    @Shadow
    public abstract WeatherData getWeatherData();
    
    @Unique
    private static final int MAX_ROLL_CHANCE = 100;
    
    // as far as I can tell, resetWeather is only called when sleeping
    @Inject(at = @At("HEAD"), method = "resetWeatherCycle", cancellable = true)
    private void resetWeather(CallbackInfo ci) {
        final WeatherData weatherData = this.getWeatherData();
        
        if (weatherData.isRaining()) {
            int rainContinuationChance = Config.keepRainChance();
            int rainRoll               = RANDOM.nextInt(MAX_ROLL_CHANCE);
            
            logRoll("Rain continuation rolled: {}/{}", rainRoll, rainContinuationChance);
            
            if (rainRoll < rainContinuationChance) {
                weatherData.setRaining(true);
                logRoll("Rain continuation passed");
                
                if (Config.resetThunderOnSleep()) {
                    logRoll("Reset thundering because of configuration");
                    weatherData.setThundering(false);
                    weatherData.setThunderTime(0);
                } else if (weatherData.isThundering()) {
                    int thunderContinuationChance = Config.keepThunderChance();
                    int thunderRoll               = RANDOM.nextInt(MAX_ROLL_CHANCE);
                    logRoll(
                        "Thunder continuation rolled: {}/{}",
                        thunderRoll,
                        thunderContinuationChance
                    );
                    
                    if (thunderRoll < thunderContinuationChance) {
                        weatherData.setThundering(true);
                        logRoll("Thunder continuation passed");
                    } else {
                        LetItRain.LOGGER.info("Thunder continuation not passed");
                    }
                }
                ci.cancel();
            } else {
                LetItRain.LOGGER.info("Rain continuation not passed");
            }
        } else if (Config.preserveWeatherTime()) {
            ci.cancel();
        }
    }
}
