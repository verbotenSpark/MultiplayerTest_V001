package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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

    // store multiple remote players
    public Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap<>();

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
        client = new GameClient("65.2.187.142", 5000, this);
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

        // update all remote players
//        for (RemotePlayer rp : remotePlayers.values()) {
//            rp.predictPos();
//        }

        if (client != null) {
            client.sendPlayerUpdate(player.worldX, player.worldY, player.direction);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (RemotePlayer rp : remotePlayers.values()) {
            rp.draw(g2);
        }
        player.draw(g2);

        g2.dispose();
    }
}
