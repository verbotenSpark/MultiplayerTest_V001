package net;

import java.io.*;
import java.net.*;
import java.util.UUID;
import main.GamePanel;
import entity.RemotePlayer;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gp;

    private String playerID = UUID.randomUUID().toString();

    public GameClient(String host, int port, GamePanel gp) {
        this.gp = gp;
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to relay: " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(() -> {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals("POS")) {
                        String id = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        String dir = parts[4];

                        if (id.equals(playerID)) continue;

                        RemotePlayer rp = gp.remotePlayers.get(id);
                        if (rp == null) {
                            rp = new RemotePlayer(gp);
                            gp.remotePlayers.put(id, rp);
                        }
//                        rp.addSnapshot(x, y, dir);
                        rp.setPos(gp,x, y);

                    } else if (parts[0].equals("PING")) {
                        long now = System.currentTimeMillis();
                        long sent = Long.parseLong(parts[1]);
                        System.out.println("Ping: " + (now - sent) + " ms");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    long now = System.currentTimeMillis();
                    sendMessage("PING," + now);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendPlayerUpdate(int x, int y, String dir) {
        sendMessage("POS," + playerID + "," + x + "," + y + "," + dir);
    }

    private void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}
