package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by david on 2016-02-02.
 */
public class TestThread implements Runnable {
    DateFormat df = new SimpleDateFormat("HH:mm:ss");

    Socket socket;
    Scanner input;
    PrintWriter out;
    List<String> users = new ArrayList<>();

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

            if (message.length() >= 5 && message.substring(0, 5).equals("ERROR")) {
                Main.Initialize();
                Main.error.setText("Användarnamnet används redan!");
            }

            else if (message.length() >= 6 && message.substring(0, 6).equals("JOINED")) {
                Date now = Calendar.getInstance().getTime();
                String reportnow = df.format(now);
                users.add(message.substring(7, message.length()));

                Main.appendToPane(Main.tpConversation, "<" + reportnow + "> " + message.substring(7, message.length()) + " gick med i chatten \n", Color.green);
                for (String value : users) {
                    System.out.println("User = " + value);
                    String[] usersList = new String[users.size()];
                    usersList = users.toArray(usersList);
                    Main.usersOnline.setListData(usersList);
                }
            }

            else if (message.substring(0, 4).equals("QUIT")) {
                Date now = Calendar.getInstance().getTime();
                String reportNow = df.format(now);
                Main.appendToPane(Main.tpConversation, "<" + reportNow + "> " + message.substring(5, message.length()) + " lämnade chatten \n", Color.red);
                users.remove(message.substring(5, message.length()));
                String[] usersList = new String[users.size()];
                usersList = users.toArray(usersList);
                Main.usersOnline.setListData(usersList);

            }
            else if (message.length() > 6 && message.substring(0, 7).equals("MESSAGE")) {
                String[] newMessage = message.split(" ");
                String testMessage = "";
                for (int i = 2; i < newMessage.length ; i++) {
                    testMessage += newMessage[i]+ " ";
                    System.out.println(newMessage[i]);
                }
                Date now = Calendar.getInstance().getTime();
                String reportNow = df.format(now);
                String printMessage = "<" + reportNow + "> " + "["+newMessage[1].substring(0, newMessage[1].length() - 1)+"] " + testMessage;
                if (Main.userName.equals(newMessage[1].substring(0, newMessage[1].length() - 1))) {
                    Main.appendToPane(Main.tpConversation, printMessage + "\n", Color.YELLOW);
                }
                else {
                    Main.appendToPane(Main.tpConversation, printMessage + "\n", Color.white);
                }



            }
            else {
                System.out.println(message);
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
        Main.tpConversation.setText("");
        socket.close();
        Main.Initialize();
    }


}
