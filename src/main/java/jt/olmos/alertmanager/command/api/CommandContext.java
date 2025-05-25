package jt.olmos.alertmanager.command.api;

import io.github.natanimn.BotContext;
import io.github.natanimn.types.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @param message   Could be a common supertype or use generics
 * @param arguments Parsed arguments string
 */

public record CommandContext(
        BotContext botContext,
        Message message,
        String command,
        String arguments
) {
}
