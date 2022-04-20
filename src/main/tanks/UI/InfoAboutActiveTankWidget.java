package tanks.UI;

import tanks.team.Tank;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class InfoAboutActiveTankWidget extends JPanel {

    private Tank _tank;
    private WidgetFactory _widgetFactory;
    private final int PANEL_WIDTH = 576;
    private final int PANEL_HEIGHT = 52;

    private JLabel _info;

    public InfoAboutActiveTankWidget(Tank tank, WidgetFactory widgetFactory) {
        super();
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        File font_file = new File("fonts/Pixeboy-z8XGD.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        Font sizedFont = font.deriveFont(30f);
        _info = new JLabel();
        _info.setFont(sizedFont);
        _info.setForeground(Color.WHITE);

        setTank(tank);
        _widgetFactory = widgetFactory;
        _info.setText(getTankInfoText());
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(_info);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        _info.setText(getTankInfoText());
        g.setColor(Color.WHITE);
        g.drawRect(0,0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    private String getTankInfoText() {
        String color;
        if(_widgetFactory.getColorTeam(_tank.getTeam()) == Color.YELLOW) {
            color = "yellow   ";
        }
        else {
            color = "green   ";
        }

        String infoText = "Team: " + color + "Live: " + _tank.getLive() + "   Recharge: ";
        if(_tank.getRecharge() == 1) {
            infoText += "1 turn";
        }
        else if(_tank.getRecharge() > 1) {
            infoText += _tank.getRecharge() + " turns";
        }
        else {
            infoText += "ready to shoot";
        }
        return infoText;
    }

    public void setTank(Tank tank) {
        this._tank = tank;
        repaint();
        revalidate();
    }
}
