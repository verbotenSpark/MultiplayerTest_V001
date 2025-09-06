package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import main.GamePanel;

public class RemotePlayer extends Entity {
    private static class Snapshot {
        long time;
        int x, y;
        String dir;
        Snapshot(long time, int x, int y, String dir) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

    private LinkedList<Snapshot> history = new LinkedList<>();
    private float renderX, renderY;
    private String renderDir = "down";

    private final long bufferDelay = 200;  // 100 ms buffer

    public RemotePlayer(GamePanel gp) {
        super(gp);
        this.gp = gp;
    }

    public void addSnapshot(int x, int y, String dir) {
        history.add(new Snapshot(System.currentTimeMillis(), x, y, dir));
        if (history.size() > 20) history.removeFirst();
    }

    public void update() {
        long renderTime = System.currentTimeMillis() - bufferDelay;

        if (history.size() >= 2) {
            Snapshot prev = null, next = null;

            for (int i = 0; i < history.size() - 1; i++) {
                if (history.get(i).time <= renderTime && history.get(i + 1).time >= renderTime) {
                    prev = history.get(i);
                    next = history.get(i + 1);
                    break;
                }
            }

            if (prev != null && next != null) {
                float t = (renderTime - prev.time) / (float) (next.time - prev.time);
                renderX = prev.x + t * (next.x - prev.x);
                renderY = prev.y + t * (next.y - prev.y);
                renderDir = prev.dir;
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.red);
        g2.fillRect((int) renderX, (int) renderY, 48, 48);
    }
}
