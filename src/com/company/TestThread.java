package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by david on 2016-02-02.
 */
public class TestThread implements Runnable {

    Socket socket;
    Scanner input;
    Scanner send = new Scanner(System.in);
    PrintWriter out;
    List<String> users = new ArrayList<>();
    final DefaultListModel model = new DefaultListModel();


    public TestThread(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            input = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            out.flush();
            CheckStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recive() {

        if (input.hasNext()) {
            String message = input.nextLine();

            if (message.length() >= 6 && message.substring(0, 6).equals("JOINED")) {
                System.out.println(message);
                users.add(message.substring(7, message.length()));

                Main.appendToPane(Main.tpConversation, message.substring(7, message.length()) + " gick med i chatten \n", Color.green);
                for (String value : users) {
                    System.out.println("User = " + value);
                    String[] usersList = new String[users.size()];
                    usersList = users.toArray(usersList);
                    Main.usersOnline.setListData(usersList);
                }

            }
            else if (message.substring(0, 4).equals("QUIT")) {
                Main.appendToPane(Main.tpConversation, message.substring(5, message.length()) + " lämnade chatten \n", Color.red);
                users.remove(message.substring(5, message.length()));
                String[] usersList = new String[users.size()];
                usersList = users.toArray(usersList);
                Main.usersOnline.setListData(usersList);

            }
            else if (message.length() > 6 && message.substring(0, 7).equals("MESSAGE")) {
                Main.appendToPane(Main.tpConversation, message.substring(8, message.length()) + "\n", Color.white);
            }
            else {
                System.out.println("hue");
            }
        }

    }

    public void CheckStream()
    {
        while(true)
        {
            recive();
        }
    }

    public void send(String message)
    {
        out.println(message);
        out.flush();
        Main.messageField.setText("");
    }

    public void disconnect() throws IOException {
        socket.close();
        System.exit(0);
    }
}
