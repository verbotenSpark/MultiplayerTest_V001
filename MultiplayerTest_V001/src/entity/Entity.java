package entity;

import java.awt.Rectangle;

import main.GamePanel;

public class Entity {
	GamePanel gp;
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	public int worldX, worldY;
	public int speed;
	public String direction = "down";
	public Rectangle solidArea;
	public int solidAreaDefaultX, solidAreaDefaultY;	
	public boolean collisionOn = false;
}
