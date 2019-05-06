/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FP;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shri Hari
 */
public class Conection {

    Connection con;

    Conection() throws SQLException {
        

    }

    public boolean isConnection() throws SQLException {
        try {
            //        System.out.println("Hello World!");
            Class.forName("com.mysql.jdbc.Driver");
//            Connection con;l
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/railway", "root", "");
            return true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Connection conreturn() {
        return con;
    }

}
