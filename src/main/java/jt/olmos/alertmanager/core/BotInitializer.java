package jt.olmos.alertmanager.core;

import io.github.natanimn.BotClient;
import io.github.natanimn.BotContext;
import io.github.natanimn.requests.GetUpdates;
import io.github.natanimn.types.Message;
import io.github.natanimn.types.Update;
import jt.olmos.alertmanager.command.dispatcher.CommandDispatcher;
import jt.olmos.alertmanager.command.parser.CommandParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer implements ApplicationRunner {
    private final BotClient botClient;
    private final BotUsernameProvider botUsernameProvider;
    private final CommandParser commandParser;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing telegram bot...");

        botUsernameProvider.initializeBotUsername();
        commandParser.initializePatterns();
        log.info("Starting Telegram Bot (long polling)...");
        try {
            botClient.run();
        } catch (Exception e) {
            log.error("Error running Telegram Bot", e);
            // Consider application shutdown or retry logic here
            System.exit(1); // Or handle more gracefully
        }
        log.info("Telegram Bot has stopped.");
    }
}
