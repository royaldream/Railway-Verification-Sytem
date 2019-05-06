

package FP;
/**
 *
 * @author Vivek sanepara
 */

import java.awt.Component;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import static FP.Journey.mainFrame;

public class ComboBoxExample {

    private Connection connection;
    Statement statement;
    JFrame f;
    String destination;
    String sources;
    ArrayList trains_id;
    ArrayList trains_sit;
    ArrayList trains_amount;
    int i = 0;
    final JComboBox cb = new JComboBox();

    public ComboBoxExample(String destination, String sources) {
        i = 0;
        trains_id = new ArrayList();
        trains_sit = new ArrayList();
        trains_amount = new ArrayList();
        this.destination = destination;
        this.sources = sources;
        Conection conn;
        try {
            conn = new Conection();
            if (conn.isConnection()) {
                connection = conn.conreturn();
            } else {
                
				JOptionPane.showMessageDialog(mainFrame, "Databse is not connected!");
                //  source.requestFocus();
                return;
            }

        } catch (SQLException ex) {
            Logger.getLogger(ComboBoxExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        f = new JFrame("Ticket vending machine");
//        f.setSize(1000, 1000);          //Set Size of window

        final JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setSize(400, 100);
        JButton b = new JButton("available seats");
        b.setBounds(100, 250, 150, 20);
        JButton q = new JButton("Proceed");
        q.setBounds(350, 250, 100, 20);
        q.setActionCommand("Proceed");
        /*JLabel h1 = new JLabel("WELCOME TO INDIAN RAILWAYS");
        h1.setFont(new Font("Serif", Font.PLAIN, 20));
        h1.setBounds(100, 20, 400, 25);
         */
        //mainFrame.add(q);
        action();

        cb.setBounds(100, 150, 350, 20);
        f.add(cb);
        f.add(label);
        f.add(b);
        f.add(q);
//        f.add(h1);
        f.setLayout(null);
        f.setSize(800, 600);
        f.setLocation(283, 84);
        f.setVisible(true);
        q.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int amt = Integer.parseInt((String) trains_amount.get(cb.getSelectedIndex()));
                String id = (String) trains_id.get(cb.getSelectedIndex());
                try {
                    Verification verification = new Verification(amt, sources, destination, id);
                } catch (SQLException ex) {
                    Logger.getLogger(ComboBoxExample.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
        b.addActionListener((ActionEvent e) -> {
            /*String data = "Available Seats: "
                    + cb.getItemAt(cb.getSelectedIndex());*/

            label.setText((String) trains_sit.get(cb.getSelectedIndex()));

        });
    }

    /*
    public static void main(String[] args) throws SQLException {
        new ComboBoxExample();
    }*/
    void action() {
        try {

            statement = connection.createStatement();
            String sql_que = "SELECT * FROM `fare` WHERE `src`=" + "\"" + sources + "\"" + " and `destination`=" + "\"" + destination + "\"";
            ResultSet resultSet = statement.executeQuery(sql_que);

            while (resultSet.next()) {
                cb.addItem(resultSet.getString("train_id"));
                trains_amount.add(resultSet.getString("amount"));
                trains_id.add(resultSet.getString("train_id"));
                trains_sit.add(resultSet.getString("counter"));
                i++;
            }/*
            if (resultSet.next()) {
                int amout = resultSet.getInt("amount");
//                    fare.setText(String.valueOf(amout));
                Verification v = new Verification(amout, sources, destination);

            } else {
                JOptionPane.showMessageDialog(mainFrame, "Invalid source to destination !");
//                    source.requestFocus();
                return;
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(Journey.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
