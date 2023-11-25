package src;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ClientGUI extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JTextField textField;
    private JList<String> itemList;
    private DefaultListModel<String> itemModel;

    public ClientGUI() {
        super("Online Store");

        // 创建商品列表
        itemModel = new DefaultListModel<>();
        itemList = new JList<>(itemModel);
        JScrollPane scrollPane = new JScrollPane(itemList);

        // 创建文本框和按钮
        textField = new JTextField(20);
        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(this);

        // 创建底部面板，包含文本框和按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(textField);
        bottomPanel.add(buyButton);

        // 添加组件到窗口
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // 连接服务器
        try {
            socket = new Socket("localhost", 8080);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("list_items");
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("list_items_end")) {
                    break;
                }
                String[] tokens = response.split(" ");
                int itemId = Integer.parseInt(tokens[0]);
                String itemName = tokens[1];
                int itemPrice = Integer.parseInt(tokens[2]);
                int itemStock = Integer.parseInt(tokens[3]);
                itemModel.addElement(itemId + " " + itemName + " $" + itemPrice + " (" + itemStock + " in stock)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置窗口大小和可见性
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Buy")) {
            String selectedItem = itemList.getSelectedValue();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(this, "Please select an item to buy.");
            } else {
                int itemId = Integer.parseInt(selectedItem.split(" ")[0]);
                out.println("buy_item " + itemId);
                try {
                    String response = in.readLine();
                    if (response.equals("buy_success")) {
                        JOptionPane.showMessageDialog(this, "Buy success.");
                        updateItemList();
                    } else if (response.equals("buy_fail")) {
                        JOptionPane.showMessageDialog(this, "Buy fail. The item is out of stock.");
                        updateItemList();
                    } else {
                        JOptionPane.showMessageDialog(this, "Unknown response from server: " + response);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updateItemList() {
        itemModel.clear();
        out.println("list_items");
        try {
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("list_items_end")) {
                    break;
                }
                String[] tokens = response.split(" ");
                int itemId = Integer.parseInt(tokens[0]);
                String itemName = tokens[1];
                int itemPrice = Integer.parseInt(tokens[2]);
                int itemStock = Integer.parseInt(tokens[3]);
                itemModel.addElement(itemId + " " + itemName + " $" + itemPrice + " (" + itemStock + " in stock)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}

