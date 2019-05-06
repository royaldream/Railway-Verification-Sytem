/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FP;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Vivek sanepara
 */
public class Journey implements ActionListener{
    
    public static JFrame mainFrame;
    private JLabel statusLabel;
    private JLabel headerLabel;
    private JLabel Source,Dest,Fare,fare;
    private JTextField source,dest;
    private JButton Proceed;
    Connection connection;
    Statement statement;
    Journey() throws SQLException{
        Conection conn=new Conection();
        if(conn.isConnection())
        {
            connection=conn.conreturn();
        }
        else
        {
              JOptionPane.showMessageDialog(mainFrame, "Databse is not connected!");
                  //  source.requestFocus();
                    return;
        }
        prepareLayout();
        makeLayout();
    }
    
    public static void main(String args[]) throws SQLException{
        Journey j=new Journey();
    }

    private void prepareLayout(){
        mainFrame = new JFrame("Ticket Vending Machine");      //Main Heading name
        mainFrame.setSize(800, 600);         //Set Size of window
        mainFrame.setLocation(283, 84);
        mainFrame.setLayout(null);

        headerLabel = new JLabel("WELCOME TO INDIAN RAILWAYS");
        headerLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        headerLabel.setBounds(100, 20, 400, 25);

        long millis = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(millis);
        statusLabel = new JLabel("");
        statusLabel.setText(date.toString());
        statusLabel.setBounds(10, 625, 250, 25);
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 17));

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        mainFrame.add(headerLabel);
        mainFrame.add(statusLabel);
    }
    
    private void makeLayout() {
        Source = new JLabel("Enter Source: ");
        Source.setBounds(200, 200, 200, 20);
        Dest = new JLabel("Enter Destination: ");
        Dest.setBounds(200, 250, 200, 20);
       // Fare = new JLabel("Fare: ");
       // Fare.setBounds(200, 300, 200, 20);
        fare=new JLabel("");
        fare.setBounds(400,300,200,20);

        source = new JTextField("");
        source.setBounds(400, 200, 200, 20);
        dest = new JTextField("");
        dest.setBounds(400, 250, 200, 20);
       
        Proceed = new JButton("Proceed");
        Proceed.setBounds(250, 350, 100, 50);
        Proceed.setActionCommand("Proceed");
        Proceed.addActionListener(this);

//btn banav ani click par check karsheok
        mainFrame.add(Source);
        mainFrame.add(source);
        mainFrame.add(Dest);
        mainFrame.add(dest);
        //mainFrame.add(statusLabel);
        //mainFrame.add(Fare);
        //mainFrame.add(fare);
        mainFrame.add(Proceed);//thayu
        mainFrame.setVisible(true);
        
        //text kai rite avshe ee mane aap
//        Menu menu = new Menu();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command=ae.getActionCommand();
        if(command.equals("Proceed")){
            String destination=dest.getText();
            String sources=source.getText();
            
            if (sources.length() == 0) {
                    JOptionPane.showMessageDialog(mainFrame, "Source is required");
                    source.requestFocus();
                    return;
                }
            if (destination.length() == 0) {
                    JOptionPane.showMessageDialog(mainFrame, "Destination is required");
                    dest.requestFocus();
                    return;
                }
            ComboBoxExample boxExample=new ComboBoxExample(destination,sources);
            
        }
    }
}
