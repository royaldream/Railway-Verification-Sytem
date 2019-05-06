/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FP;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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

import com.sun.xml.internal.ws.api.message.Message;

/**
 *
 * @author Vivek sanepara
 */
public class Verification implements ActionListener {
	FPS Serialconnector;
	int id;
	private JFrame mainFrame;
	private JLabel statusLabel;
	private JLabel headerLabel;
	private JLabel Name, Adhaar, Pin;
	private JTextField name, adhaar;
	private JPasswordField pin;
	private JButton Proceed;
	Connection connection;
	int amount;
	String train_id;
	Statement statement;
	String source, destination;

	Verification() {

	}

	Verification(int amout, String source, String destination, String train_id) throws SQLException {
		amount = amout;
		this.source = source;
		this.destination = destination;
		this.train_id = train_id;
		Conection conn;
		try {
			conn = new Conection();
			if (conn.isConnection()) {
				connection = conn.conreturn();
			} else {
				JOptionPane.showMessageDialog(mainFrame, "Database is not connected!");
				// source.requestFocus();
				return;
			}
		} catch (SQLException ex) {
			Logger.getLogger(Verification.class.getName()).log(Level.SEVERE, null, ex);
		}

		prepareLayout();
		makeLayout();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void prepareLayout() {
		mainFrame = new JFrame("Ticket Vending Machine"); // Main Heading name
		mainFrame.setSize(800, 600); // Set Size of window
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
		Name = new JLabel("Enter Name: ");
		Name.setBounds(200, 200, 200, 20);
		Adhaar = new JLabel("Enter Adhaar: ");
		Adhaar.setBounds(200, 250, 200, 20);
		Pin = new JLabel("Enter Pin: ");
		Pin.setBounds(200, 300, 200, 20);

		name = new JTextField("");
		name.setBounds(400, 200, 200, 20);
		adhaar = new JTextField("");
		adhaar.setBounds(400, 250, 200, 20);
		pin = new JPasswordField("");
		pin.setBounds(400, 300, 200, 20);

		Proceed = new JButton("Proceed");
		Proceed.setBounds(250, 350, 100, 50);
		Proceed.setActionCommand("Proceed");
		Proceed.addActionListener(this);

//btn banav ani click par check karsheok
		mainFrame.add(Name);
		mainFrame.add(name);
		mainFrame.add(Adhaar);
		mainFrame.add(adhaar);
		// mainFrame.add(Pin);
		// mainFrame.add(pin);
		mainFrame.add(Proceed);// thayu
		mainFrame.setVisible(true);

		// text kai rite avshe ee mane aap
//        Menu menu = new Menu();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Proceed")) {
			String txt_name = name.getText();
			String txt_adhar = adhaar.getText();
			pin.setVisible(false);
			// String txt_pin = pin.getText();
			if (txt_adhar.length() == 0) {
				JOptionPane.showMessageDialog(mainFrame, "Adhaar no is required!");
				adhaar.requestFocus();
				return;
			}
			if (txt_adhar.length() <= 15 || txt_adhar.length() >= 17) {
				JOptionPane.showMessageDialog(mainFrame, "Adhaar no should be 16 digit number!");
				adhaar.requestFocus();
				return;
			}
			try {
				statement = connection.createStatement();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String sql_pin = "SELECT * FROM authentication WHERE aadhar_number = " + "\"" + txt_adhar + "\"";
//			ResultSet resultSet2;
			try {
				System.out.println(sql_pin);
				ResultSet resultSet2 = statement.executeQuery(sql_pin);
				if (resultSet2.next()) {
					String pin_db = resultSet2.getString(3);
					if (call_Verification()) {
						System.out.println(id + "\n Database pin" + pin_db);
						if (String.valueOf(id).equals(pin_db)) {
							try {
								System.out.println("equal all");
								statement = connection.createStatement();
								String sql_que = "SELECT * FROM authentication WHERE aadhar_number=" + "\"" + txt_adhar
										+ "\"";
								ResultSet resultSet = statement.executeQuery(sql_que);
								if (resultSet.next()) {
									String sql_que1 = "SELECT counter FROM fare WHERE train_id=" + "\"" + train_id
											+ "\"";
									ResultSet rs1 = statement.executeQuery(sql_que1);
									if (rs1.next()) {
										int sits = rs1.getInt("counter");
										if (sits > 0) {
											sits = sits - 1;
											String sql_que2 = "UPDATE fare SET counter=" + sits + " WHERE train_id="
													+ "\"" + train_id + "\"";
											statement.executeUpdate(sql_que2);
											String insert_sql = "INSERT INTO booking VALUES (" + "\"" + txt_adhar + "\""
													+ "," + "\"" + source + "\"" + "," + "\"" + destination + "\"" + ","
													+ amount + ")";
											statement.executeUpdate(insert_sql);
											JOptionPane.showMessageDialog(mainFrame,
													"Verification Successfull !\n Amount = " + amount);

											mainFrame.dispose();

//				                    adhaar.requestFocus();
											return;
										} else {
											JOptionPane.showMessageDialog(mainFrame, "Seat not exist!");
//				                    adhaar.requestFocus();
											return;
										}

									}

								} else {
									JOptionPane.showMessageDialog(mainFrame, "Adhaarcard or pin not valid");
									adhaar.requestFocus();
									return;
								}
							} catch (Exception ex) {
								Logger.getLogger(Verification.class.getName()).log(Level.SEVERE, null, ex);
							}

						} else {
							JOptionPane.showMessageDialog(mainFrame, "Wrong Authentication");
							mainFrame.dispose();
							return;
						}
					}else
					{
						JOptionPane.showMessageDialog(mainFrame, "Authentication Failed");
						mainFrame.dispose();
						return;
					}
					// String verfiy_pin=call_Verification();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}

	}

	private boolean call_Verification() throws Exception {
		System.out.println("Call Function Verification");
		String pin = null;
		Boolean result = false;
		Serialconnector = new FPS();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));

			if (Serialconnector.open("COM6")) {
				Serialconnector.LEDON();
				System.out.println("Please place your finger");
				int c = 0;
				while (result == false) {
					if(c>20)
						{result= false;
						break;}
					Serialconnector.CaptureFinger(true);
					id = Serialconnector.Identify1_N();
					if (id < 200) {
						System.out.println("Verified ID:" + id);
						result = true;
					} else {
						System.out.println("Finger not found");
						result = false;
					}
					c++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		Serialconnector.LEDOFF();
		Serialconnector.Close();
		Serialconnector.close();
		return result;
	}
}
