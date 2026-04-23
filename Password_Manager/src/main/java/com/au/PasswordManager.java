package com.au;

import java.awt.Color;
import java.awt.Image;
import java.awt.TextField;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

// Main class
class PasswordManager {
    
    static void main() {

        Color panelBackground = new Color(153, 153, 255), buttonBackground = new Color(0, 0, 102), buttonForeground = new Color(204, 204, 255);
        Color newcolor= new Color(77, 77, 255);

        JFrame screen = new JFrame();
        Image img = new ImageIcon("src/main/java/images/password-manager_15096966.png").getImage();
        JButton buttonLogin = new JButton("Login"), buttonReg = new JButton("Register");
        TextField inputId = new TextField(), inputpswd = new TextField();

        // screen setup
        screen.setSize(600, 400);
        screen.setIconImage(img);
        screen.setLayout(null);
        screen.getContentPane().setBackground(panelBackground);

        //adding components to screen
        screen.add(buttonLogin);
        screen.add(buttonReg);
        screen.add(inputId);
        screen.add(inputpswd);

        // input user id
        inputId.setBackground(newcolor);
        inputId.setForeground(buttonForeground);
        // inputId.
        inputId.setBounds(screen.getWidth() / 2 - 124, screen.getHeight() / 2 - 50, 248, 20);

        // input password
        inputpswd.setBackground(buttonBackground);
        inputpswd.setForeground(buttonForeground);
        inputpswd.setBounds(screen.getWidth() / 2 - 124, screen.getHeight() / 2 - 20, 248, 20);

        // button login
        buttonLogin.setBounds(screen.getWidth() / 2 - 124, screen.getHeight() / 2 + 10, 120, 30);
        buttonLogin.setForeground(buttonForeground);
        buttonLogin.setBackground(buttonBackground);
        buttonLogin.setFocusPainted(false);

        buttonLogin.addActionListener(l -> {
            System.out.println("Button \"Login\" pressed.");
        });
        
        // button registration
        buttonReg.setBounds(screen.getWidth() / 2 + 4, screen.getHeight() / 2 + 10, 120, 30);
        buttonReg.setForeground(buttonForeground);
        buttonReg.setBackground(buttonBackground);
        buttonReg.setFocusPainted(false);
        
        buttonReg.addActionListener(l -> {
            System.out.println("Button \"Register\" pressed.");
        });

        screen.setVisible(true);
    }
}
