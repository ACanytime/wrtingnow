package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<Item> itemList;

    public Server() {
        // 初始化商品列表
        itemList = new ArrayList<>();
        itemList.add(new Item(1, "iPhone", 999));
        itemList.add(new Item(2, "iPad", 799));
        itemList.add(new Item(3, "MacBook", 1299));

        // 启动服务器
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server started.");
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean buyItem(int itemId) {
        for (Item item : itemList) {
            if (item.getId() == itemId) {
                if (item.getStock() > 0) {
                    item.setStock(item.getStock() - 1);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private synchronized String listItems() {
        StringBuilder sb = new StringBuilder();
        for (Item item : itemList) {
            sb.append(item.getId()).append(" ");
            sb.append(item.getName()).append(" ");
            sb.append(item.getPrice()).append(" ");
            sb.append(item.getStock()).append("\n");
        }
        return sb.toString();
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String request;
                while ((request = in.readLine()) != null) {
                    String[] tokens = request.split(" ");
                    if (tokens[0].equals("list_items")) {
                        out.println(listItems() + "list_items_end");
                    } else if (tokens[0].equals("buy_item")) {
                        int itemId = Integer.parseInt(tokens[1]);
                        if (buyItem(itemId)) {
                            out.println("buy_success");
                        } else {
                            out.println("buy_fail");
                        }
                    } else {
                        out.println("unknown_command");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}

class Item {
    private int id;
    private String name;
    private int price;
    private int stock;

    public Item(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = 10;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
