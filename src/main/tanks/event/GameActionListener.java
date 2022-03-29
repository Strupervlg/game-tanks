package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface GameActionListener extends EventListener {

    void tankMoved(@NotNull GameActionEvent event);

    void tankSkippedMove(@NotNull GameActionEvent event);

    void tankShot(@NotNull GameActionEvent event);

    void gameOver(@NotNull GameActionEvent event);

    void bulletChangedCell(@NotNull GameActionEvent event);
}
