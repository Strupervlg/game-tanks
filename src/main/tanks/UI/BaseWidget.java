package tanks.UI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BaseWidget extends CellItemWidget {
    private Color _color;

    private static final File FILE_IMG_BASE_1 = new File("images/Base1.png");
    private static final File FILE_IMG_BASE_2 = new File("images/Base2.png");
    private static final File FILE_IMG_DESTROYED_BASE_1 = new File("images/DestroyedBase1.png");
    private static final File FILE_IMG_DESTROYED_BASE_2 = new File("images/DestroyedBase2.png");
    private static final File FILE_IMG_EXPLOSION = new File("images/Explosion.png");

    public BaseWidget(Color color) {
        super();
        this._color = color;
    }

    public Color getColor() {
        return _color;
    }

    private File getBaseFileByColorAndState(Color color, State state) {
        File file = null;
        if (state == State.ALIVE)  {
            file = color == Color.YELLOW ? FILE_IMG_BASE_1 : FILE_IMG_BASE_2;
        }
        else if(state == State.DAMAGED) {
            file = FILE_IMG_EXPLOSION;
        }
        else if (state == State.DESTROYED) {
            file = color == Color.YELLOW ? FILE_IMG_DESTROYED_BASE_1 : FILE_IMG_DESTROYED_BASE_2;
        }
        return file;
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getBaseFileByColorAndState(getColor(), getState()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
