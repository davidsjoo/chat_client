package com.company;


import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;


public class Main {

    public static JFrame mainWindow = new JFrame();
    private static JButton connectButton = new JButton();
    private static TestThread testThread;
    public static JTextArea conversation = new JTextArea();
    private static JScrollPane spConversation = new JScrollPane();
    public static JTextField userNameBox = new JTextField(200);
    private static JLabel userNameLabel = new JLabel("Användarnamn: ");
    public static String userName = "Anonymous";
    public static JList<String> usersOnline = new JList<>();
    private static JScrollPane spUsersOnline = new JScrollPane();
    public static JTextField messageField = new JTextField(20);
    private static JButton send = new JButton();
    private static JButton disconnect = new JButton();
    public static JTextPane tpConversation = new JTextPane();
    public static JLabel error = new JLabel();

    public static void main(String[] args) {

        BuildMainWindow();
        Initialize();

        tpConversation.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                tpConversation.setEditable(true);

            }

            @Override
            public void focusGained(FocusEvent e) {
                tpConversation.setEditable(false);

            }
        });

    }

    public static void Initialize()
    {
        spConversation.setVisible(false);
        spUsersOnline.setVisible(false);
        messageField.setVisible(false);
        disconnect.setVisible(false);
        send.setVisible(false);
        userNameLabel.setVisible(true);
        userNameBox.setVisible(true);
        connectButton.setVisible(true);

    }

    public static void connect() {
        final int port = 1337;
        final String host = "chat.linkura.se";

        userName = userNameBox.getText().trim();
        int count = 0;
        for (int i = 0; i < userName.length() ; i++) {
            if (userName.charAt(i) == ' ') {
                count++;
            }
        }

        if (count != 0 ) {
            error.setText("Du får inte ha mellanslag i användarnamnet!");
        }
        else if (userName.isEmpty()) {
            error.setText("Skriv ett användarnamn!");
        }
        else if (!userName.matches("[a-öA-Ö0-9]+")) {
            error.setText("Användarnamnet får bara innhålla siffror och bokstäver!");
        }
        else if (userName.length() > 15) {
            error.setText("Ditt användarnamn får max vara 15 tecken!");
        }
        else {
            try {
                Socket socket = new Socket(host, port);
                testThread = new TestThread(socket);
                Thread x = new Thread(testThread);
                x.start();

                PrintWriter out = new PrintWriter(socket.getOutputStream());

                out.println("NICK " + userName);
                out.flush();

                userNameLabel.setVisible(false);
                userNameBox.setVisible(false);
                connectButton.setVisible(false);
                messageField.setVisible(true);
                messageField.requestFocus();
                send.setVisible(true);
                spConversation.setVisible(true);
                spUsersOnline.setVisible(true);
                disconnect.setVisible(true);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void BuildMainWindow()
    {
        mainWindow.setTitle("Chat");
        mainWindow.setSize(450, 500);
        mainWindow.setLocation(220, 180);
        mainWindow.setResizable(false);
        ConfigureMainWindow();
        MainWindow_Action();
        sendAction();
        disconnectAction();
        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public static void ConfigureMainWindow()
    {
        userNameLabel.setFont(new Font("Lucida Console", 0, 14));
        userNameLabel.setForeground(new Color(255, 255, 255));

        mainWindow.getContentPane().setBackground(new Color(4, 3, 10));
        mainWindow.setSize(800, 600);
        mainWindow.getContentPane().setLayout(null);
        connectButton.setBackground(new Color(255, 255, 255));
        connectButton.setForeground(new Color(4, 3, 10));
        connectButton.setFont(new Font("Lucida Console", 0, 14));

        connectButton.setText("Anslut");
        connectButton.setToolTipText("");
        mainWindow.getContentPane().add(connectButton);
        connectButton.setBounds(455, 10, 100, 25);

        conversation.setColumns(20);
        conversation.setFont(new Font("Lucida Console", 0, 14));
        conversation.setForeground(new Color(4, 3, 10));
        conversation.setLineWrap(true);
        conversation.setRows(5);
        //conversation.setEditable(false);

        tpConversation.setForeground(new Color(255, 255, 255));
        tpConversation.setBounds(10, 10, 600, 500);
        tpConversation.setBackground(new Color(4, 3, 10));
        tpConversation.setFont(new Font("Lucida Console", 0, 14));

        spConversation.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spConversation.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        spConversation.setViewportView(tpConversation);
        mainWindow.getContentPane().add(spConversation);
        spConversation.setBounds(10, 10, 600, 500);


        userNameBox.setBounds(120, 10, 325, 25);
        userNameLabel.setBounds(10, 10, 130, 25);
        mainWindow.getContentPane().add(userNameBox);
        mainWindow.getContentPane().add(userNameLabel);

        usersOnline.setForeground(new Color(0, 180, 255));
        usersOnline.setBackground(new Color(4, 3, 10));
        usersOnline.setFont(new Font("Lucida Console", 0, 14));
        spUsersOnline.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spUsersOnline.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        spUsersOnline.setViewportView(usersOnline);
        mainWindow.getContentPane().add(spUsersOnline);
        spUsersOnline.setBounds(620, 10, 165, 300);

        messageField.setForeground(new Color(255, 255, 255));
        messageField.setBackground(new Color(4, 3, 10));
        messageField.requestFocus();
        messageField.addActionListener(action);
        mainWindow.getContentPane().add(messageField);
        messageField.setBounds(10, 520, 600, 30);


        send.setBackground(new Color(255, 255, 255));
        send.setForeground(new Color(4, 3, 10));
        send.setText("Skicka");
        send.setFont(new Font("Lucida Console", 0, 14));
        mainWindow.getContentPane().add(send);
        send.setBounds(620, 520, 165, 30);

        disconnect.setBackground(new Color(255, 255, 255));
        disconnect.setForeground(new Color(4, 3, 10));
        disconnect.setText("Avsluta chatten");
        disconnect.setFont(new Font("Lucida Console", 0, 14));
        mainWindow.getContentPane().add(disconnect);
        disconnect.setBounds(620, 320, 165, 30);

        error.setFont(new Font("Lucida Console", 0, 14));
        error.setForeground(new Color(255, 0, 23));
        error.setBounds(120, 40, 525, 25);


        mainWindow.getContentPane().add(error);


    }

    public static void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    public static void MainWindow_Action()
    {
        connectButton.addActionListener(e -> connect());
    }

    public static Action action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            send();
        }
    };

    public static void sendAction() {
        send.addActionListener(e -> send());
    }
    public static void disconnectAction() {
        disconnect.addActionListener(e -> {
            try {
                disconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static void send()
    {
        if(!messageField.getText().equals(""))
        {
            testThread.send(messageField.getText());
            messageField.requestFocus();
        }
    }

    public static void disconnect() throws IOException {
        testThread.disconnect();
    }

}
