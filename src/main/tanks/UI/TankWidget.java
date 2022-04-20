package tanks.UI;

import org.jetbrains.annotations.NotNull;
import tanks.Direction;
import tanks.team.Tank;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TankWidget extends CellItemWidget {
    private final Color _color;
    private final Tank _tank;

    private static final File FILE_IMG_TANK_1 = new File("images/Tank1.png");
    private static final File FILE_IMG_TANK_2 = new File("images/Tank2.png");
    private static final File FILE_IMG_ACTIVE_TANK_1 = new File("images/ActiveTank1.png");
    private static final File FILE_IMG_ACTIVE_TANK_2 = new File("images/ActiveTank2.png");
    private static final File FILE_IMG_EXPLOSION = new File("images/Explosion.png");

    public TankWidget(Tank tank, Color color) {
        super();
        this._tank = tank;
        this._color = color;
        setFocusable(true);
        addKeyListener(new KeyController());
    }

    public void setActive(boolean state) {
        setFocusable(state);
        requestFocus();
        repaint();
    }

    public Color getColor() {
        return _color;
    }

    private File getTankFileByColorAndActive(Color color, boolean active, State state) {
        File file = null;
        if(state == State.DAMAGED) {
            file = FILE_IMG_EXPLOSION;
        }
        else if (color == Color.YELLOW)  {
            file = active ? FILE_IMG_ACTIVE_TANK_1 : FILE_IMG_TANK_1;
        }
        else if (color == Color.GREEN) {
            file = active ? FILE_IMG_ACTIVE_TANK_2 : FILE_IMG_TANK_2;
        }
        return file;
    }

    private BufferedImage rotateImage(BufferedImage image, int angle) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage dstImage = null;
        AffineTransform affineTransform = new AffineTransform();

        if (angle == 180) {
            affineTransform.translate(width, height);
            dstImage = new BufferedImage(width, height, image.getType());
        } else if (angle == 90) {
            affineTransform.translate(height, 0);
            dstImage = new BufferedImage(height, width, image.getType());
        } else if (angle == 270) {
            affineTransform.translate(0, width);
            dstImage = new BufferedImage(height, width, image.getType());
        }

        affineTransform.rotate(java.lang.Math.toRadians(angle));
        AffineTransformOp affineTransformOp = new AffineTransformOp(
                affineTransform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return affineTransformOp.filter(image, dstImage);
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getTankFileByColorAndActive(getColor(), this._tank.isActive(), getState()));
            image = rotateImage(image, this._tank.getCurrentDirection().getAngle());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private class KeyController implements KeyListener {

        @Override
        public void keyTyped(KeyEvent arg0) {
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            int keyCode = ke.getKeyCode();

            changeDirectionAction(keyCode);
            moveAction(keyCode);
            shootAction(keyCode);
            skipMoveAction(keyCode);

            repaint();
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }

        private void changeDirectionAction(@NotNull int keyCode) {
            Direction direction = directionByKeyCode(keyCode);
            if(direction != null) {
                _tank.changeDirection(direction);
            }
        }

        private void shootAction(@NotNull int keyCode) {
            if(keyCode == KeyEvent.VK_SPACE) {
                _tank.shoot();
            }
        }

        private void skipMoveAction(@NotNull int keyCode) {
            if(keyCode == KeyEvent.VK_F) {
                _tank.skip();
            }
        }

        private void moveAction(@NotNull int keyCode){
            if(keyCode == KeyEvent.VK_E) {
                _tank.move();
            }
        }

        private Direction directionByKeyCode(@NotNull int keyCode) {
            Direction direction = null;
            switch (keyCode) {
                case KeyEvent.VK_W:
                    direction = Direction.north();
                    break;
                case KeyEvent.VK_S:
                    direction = Direction.south();
                    break;
                case KeyEvent.VK_A:
                    direction = Direction.west();
                    break;
                case KeyEvent.VK_D:
                    direction = Direction.east();
                    break;
            }
            return direction;
        }
    }
}
