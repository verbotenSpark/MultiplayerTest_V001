package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;
        solidArea = new Rectangle(0, 0, 48, 48);
        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 3;
        worldY = gp.tileSize * 1;
        speed = 1;
        direction = "down";
    }

    public void update() {
        if (keyH.upPressed) {
            worldY -= speed;
        } else if (keyH.downPressed) {
            worldY += speed;
        } else if (keyH.leftPressed) {
            worldX -= speed;
        } else if (keyH.rightPressed) {
            worldX += speed;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fillRect(worldX, worldY, 48, 48);
    }
}
