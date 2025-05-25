package jt.olmos.alertmanager.config;

import io.github.natanimn.BotClient;
import io.github.natanimn.enums.ParseMode;
import io.github.natanimn.enums.Updates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public BotClient botClient() {
        if(botToken == null || botToken.isEmpty()) {
            throw new IllegalArgumentException("Telegram bot token is not configured.");
        }
        return new BotClient.Builder(botToken)
                .log(true)
                .skipOldUpdates(true)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }
}
