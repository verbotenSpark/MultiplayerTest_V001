package main;
//testing Fetch in local
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import entity.Player;
import entity.RemotePlayer;
import net.GameClient;

public class GamePanel extends JPanel implements Runnable {
    public final int tileSize = 48;
    final int maxScreenCol = 10;
    final int maxScreenRow = 10;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    int FPS = 60;

    KeyHandler keyH = new KeyHandler(this);
    public Player player = new Player(this, keyH);
    public RemotePlayer remotePlayer = new RemotePlayer(this);

    Thread gameThread;
    GameClient client;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        // Connect to AWS relay server
        client = new GameClient("43.205.217.14", 5000, this);
        client.start();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        player.update();
        remotePlayer.predictPos();

        // Send local player state to server
        if (client != null) {
            client.sendPlayerUpdate(player.worldX, player.worldY,player.direction);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);
        remotePlayer.draw(g2);

        g2.dispose();
    }
}
