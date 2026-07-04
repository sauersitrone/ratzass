package de.simone.backend.starcraft;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RatAssBot extends DefaultBWListener {
    private BWClient bwClient;

    @Override
    public void onFrame() {
        Game game = bwClient.getGame();
        game.drawTextScreen(100, 100, "Hello World!");
    }

    @Startup
    void init() {
        RatAssBot bot = new RatAssBot();
        bot.bwClient = new BWClient(bot);
        bot.bwClient.startGame();
    }
}
