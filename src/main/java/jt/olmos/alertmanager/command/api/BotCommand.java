package jt.olmos.alertmanager.command.api;

public interface BotCommand {
    /**
     * Gets the primary identifier for this command (e.g., "start", "help").
     * This is typically the string after the '/' or the bot mention.
     * @return The command identifier.
     */
    String getCommandIdentifier();

    /**
     * Executes the command logic.
     * @param context The context of the current command execution,
     *                containing message details, chat info, and bot context.
     */
    void execute(CommandContext context);

    /**
     * (Optional) Provides a description of the command for help messages.
     * @return The command description, or null if not applicable.
     */
    default String getDescription() {
        return null;
    }
}
