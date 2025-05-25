package jt.olmos.alertmanager.command.parser;


import io.github.natanimn.BotContext;
import io.github.natanimn.enums.ChatAction;
import io.github.natanimn.enums.EntityType;
import io.github.natanimn.types.Chat;
import io.github.natanimn.types.Message;
import io.github.natanimn.types.MessageEntity;
import jt.olmos.alertmanager.core.BotUsernameProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandParser {
    private final BotUsernameProvider botUsernameProvider;
    private Pattern mentionCommandPattern;

    public void initializePatterns() {
        String botUsername = botUsernameProvider.getBotUsername();
        if (botUsername!= null &&!botUsername.isEmpty()) {
            // Regex: ^@<botUsername>\s+(\w+)(?:\s+(.*))?$
            // \w+ for command, (.*) for arguments. Case-insensitive for command.
            this.mentionCommandPattern = Pattern.compile(
                    "^@" + Pattern.quote(botUsername) + "\\s+([a-zA-Z0-9_]+)(?:\\s+(.*))?$",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            );
            log.info("Mention command pattern initialized for @{}", botUsername);
        } else {
            log.warn("Bot username not available, mention command pattern not initialized.");
        }
    }

    public ParsedCommand parse(String text, BotContext botCtx, Message message) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // Initialize pattern if not done yet (e.g. if BotUsernameProvider initializes lazily)
        if (this.mentionCommandPattern == null && botUsernameProvider.getBotUsername()!= null) {
            initializePatterns();
        }

        String trimmedText = text.trim();

        // 1. Check for standard commands: /command@botname or /command
        if (trimmedText.startsWith("/")) {
            String[] parts = trimmedText.substring(1).split("\\s+", 2);
            String commandWithBotName = parts[0];
            String args = parts.length > 1 ? parts[1].trim() : "";

            String commandPart;
            String targetBotName = null;

            if (commandWithBotName.contains("@")) {
                String[] cmdBot = commandWithBotName.split("@", 2);
                commandPart = cmdBot[0];
                if (cmdBot.length > 1) {
                    targetBotName = cmdBot[1];
                }
            } else {
                commandPart = commandWithBotName;
            }

            boolean isPrivate = message.chat.type.equals("private");
            boolean forThisBot = isPrivate ||
                    (targetBotName != null && targetBotName.equalsIgnoreCase(botUsernameProvider.getBotUsername())) ||
                    (targetBotName == null && !isPrivate);

            if (forThisBot) {
                if (message.entities != null) {
                    for (MessageEntity entity : message.entities) {
                        if (entity.type.equals(EntityType.BOT_COMMAND) && entity.offset == 0) {
                            String entityText = trimmedText.substring(entity.offset, entity.offset + entity.length);
                            String[] entityParts = entityText.substring(1).split("@", 2);
                            String entityCommand = entityParts[0];
                            String entityBot = entityParts.length > 1 ? entityParts[1] : null;

                            if (entityBot == null || entityBot.equalsIgnoreCase(botUsernameProvider.getBotUsername())) {
                                String potentialArgs = trimmedText.substring(entity.offset + entity.length).trim();
                                return new ParsedCommand(entityCommand.toLowerCase(), potentialArgs, true);
                            } else {
                                return null;
                            }
                        }
                    }
                }
                // Fallback if no specific bot_command entity matched our criteria
                // We've already checked forThisBot, so we can return
                return new ParsedCommand(commandPart.toLowerCase(), args, true);
            }
        }

        // 2. Check for mention commands: @botname command args
        if (this.mentionCommandPattern != null) {
            Matcher matcher = mentionCommandPattern.matcher(trimmedText);
            if (matcher.matches()) {
                String command = matcher.group(1).toLowerCase();
                String args = matcher.group(2) != null ? matcher.group(2).trim() : "";
                return new ParsedCommand(command, args, true);
            }
        }

        return null; // Not a recognized command format for this bot
    }

    /**
     * @param isCommand Indicates if it was successfully parsed as a command
     */
    public record ParsedCommand(
            String command,
            String arguments,
            boolean isCommand
    ) {
    }
}
