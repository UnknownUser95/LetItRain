package net.unknownuser.letitrain.mixins;

import net.minecraft.server.world.*;
import net.minecraft.world.level.*;
import net.unknownuser.letitrain.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static net.unknownuser.letitrain.LetItRain.*;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
	
	@Unique
	private static final int MAX_ROLL_CHANCE = 100;
	
	@Final
	@Shadow
	private ServerWorldProperties worldProperties;
	
	// as far as I can tell, resetWeather is only called when sleeping
	@Inject(at = @At("HEAD"), method = "resetWeather", cancellable = true)
	private void resetWeather(CallbackInfo ci) {
		if (worldProperties.isRaining()) {
			int rainContinuationChance = Config.keepRainChance();
			int rainRoll               = RANDOM.nextInt(MAX_ROLL_CHANCE);
			logRoll("Rain continuation rolled: {}/{}", rainRoll, rainContinuationChance);
			
			if (rainRoll < rainContinuationChance) {
				worldProperties.setRaining(true);
				logRoll("Rain continuation passed");
				
				if (Config.resetThunderOnSleep()) {
					logRoll("Reset thundering because of configuration");
					worldProperties.setThundering(false);
					worldProperties.setThunderTime(0);
				} else if (worldProperties.isThundering()) {
					int thunderContinuationChance = Config.keepThunderChance();
					int thunderRoll               = RANDOM.nextInt(MAX_ROLL_CHANCE);
					logRoll("Thunder continuation rolled: {}/{}", thunderRoll, thunderContinuationChance);
					
					if (thunderRoll < thunderContinuationChance) {
						worldProperties.setThundering(true);
						logRoll("Thunder continuation passed");
					} else {
						LetItRain.LOGGER.info("Thunder continuation not passed");
					}
				}
				ci.cancel();
			} else {
				LetItRain.LOGGER.info("Rain continuation not passed");
			}
			
		}    else if (Config.preserveWeatherTime()) {
			ci.cancel();
		}
	}
}
