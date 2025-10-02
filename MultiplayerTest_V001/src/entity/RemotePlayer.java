package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import main.GamePanel;

public class RemotePlayer extends Entity {
    static class Snapshot {
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

    private final long bufferDelay = 200;

    public RemotePlayer(GamePanel gp) {
        super(gp);
        this.gp = gp;
    }

    int worldX,worldY;
    
    public void setPos(GamePanel gp,int x,int y) {
    	worldX = x;
    	worldY = y;
    }
    
    public void addSnapshot(int x, int y, String dir) {
        history.add(new Snapshot(System.currentTimeMillis(), x, y, dir));
        if (history.size() > 20) history.removeFirst();
    }

    public void predictPos() {
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

    Color realPosColor = new Color(0,0,255,128);

    public void draw(Graphics2D g2) {
        g2.setColor(realPosColor);
        g2.fillRect(worldX,worldY, gp.tileSize, gp.tileSize);
//        g2.setColor(Color.red);
//        g2.drawString(renderDir, (int) renderX, (int) renderY - 5);
    }
}
