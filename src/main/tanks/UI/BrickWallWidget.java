package tanks.UI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BrickWallWidget extends CellItemWidget {

    private static final File FILE_IMG_BRICK_WALL = new File("images/BrickWall.png");
    private static final File FILE_IMG_EXPLOSION = new File("images/Explosion.png");

    public BrickWallWidget() {
        super();
    }

    private File getBrickWallImageFile(State state) {
        File file = null;
        if (state == State.ALIVE)  {
            file = FILE_IMG_BRICK_WALL;
        }
        else if (state == State.DAMAGED) {
            file = FILE_IMG_EXPLOSION;
        }
        return file;
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getBrickWallImageFile(this.getState()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
