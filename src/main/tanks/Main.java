package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.UI.GameSceneWidget;
import tanks.UI.WidgetFactory;
import tanks.event.GameActionEvent;
import tanks.event.GameActionListener;
import tanks.levels.FirstLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GamePanel::new);

    }

    static class GamePanel extends JFrame {

        private Game game;
        private WidgetFactory widgetFactory;

        public GamePanel() throws HeadlessException {
            setVisible(true);

            widgetFactory = new WidgetFactory();
            game = new Game(new FirstLevel());

            game.addGameActionListener(new GameController());

            JPanel content = (JPanel) this.getContentPane();
            content.add(new GameSceneWidget(game, widgetFactory));

            widgetFactory.getWidget(game.activeTank()).requestFocus();

            pack();
            setLocationRelativeTo(null);
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }

        private final class GameController implements GameActionListener {

            @Override
            public void tankMoved(@NotNull GameActionEvent event) {

            }

            @Override
            public void tankSkippedMove(@NotNull GameActionEvent event) {

            }

            @Override
            public void tankShot(@NotNull GameActionEvent event) {

            }

            @Override
            public void gameOver(@NotNull GameActionEvent event) {
                String color;
                if(widgetFactory.getColorTeam(game.winner()) == Color.YELLOW) {
                    color = "yellow";
                }
                else {
                    color = "green";
                }
                JDialog dialog = new JDialog(GamePanel.this);

                dialog.setModal(true);

                dialog.setTitle ("Game Over");
                dialog.setSize(322, 100);
                dialog.setResizable(false);


                dialog.setLocationRelativeTo(GamePanel.this);

                dialog.getContentPane().setBackground(Color.BLACK);

                dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
                dialog.setLayout(new FlowLayout(FlowLayout.CENTER));

                File font_file = new File("fonts/Pixeboy-z8XGD.ttf");
                Font font = null;
                try {
                    font = Font.createFont(Font.TRUETYPE_FONT, font_file);
                } catch (FontFormatException | IOException e) {
                    e.printStackTrace();
                }
                Font sizedFont = font.deriveFont(30f);

                JLabel label = new JLabel();
                label.setText("Winner is " + color + " team");
                label.setFont(sizedFont);
                label.setForeground(Color.WHITE);

                dialog.add(label);

                JButton b2 = new JButton ("Exit");
                dialog.add(b2);

                b2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GamePanel.this.dispatchEvent(new WindowEvent(GamePanel.this, WindowEvent.WINDOW_CLOSING));
                    }
                });
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                dialog.setVisible(true);
            }

            @Override
            public void bulletChangedCell(@NotNull GameActionEvent event) {

            }

            @Override
            public void tankChangedDirection(@NotNull GameActionEvent event) {

            }

            @Override
            public void tankActivityChanged(@NotNull GameActionEvent event) {

            }

            @Override
            public void damageCaused(@NotNull GameActionEvent event) {

            }

            @Override
            public void objectDestroyed(@NotNull GameActionEvent event) {

            }
        }
    }
}
