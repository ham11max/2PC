/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author HAMMAX
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connect a = new Connect();
        try {

            a.run_2PC();

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

}
