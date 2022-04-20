package tanks.UI;

import tanks.Direction;
import tanks.team.Tank;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BulletWidget extends CellItemWidget {

    Direction _direction;

    private static final File FILE_IMG_BULLET = new File("images/Bullet.png");

    public BulletWidget(Tank.Bullet bullet) {
        super();
        this._direction = bullet.getDirection();
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

    private File getBulletImageFile() {
        return FILE_IMG_BULLET;
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getBulletImageFile());
            image = rotateImage(image, this._direction.getAngle());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
