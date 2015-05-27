/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author HAMMAX
 */
class Connect {

    Connection getDBConnection(String url, String user, String pass) {

        System.out.println("-------- Start conection to DB-------");

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

        }
        try {

            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    void run_2PC() throws SQLException {
        int price_hotel = 120;
        int price_fly = 50;
        int user_amount = 0;
        String url_hotel = "jdbc:postgresql://127.0.0.1:5432/Hotel Booking";
        String url_fly = "jdbc:postgresql://127.0.0.1:5432/Fly Booking";
        String url_pocket = "jdbc:postgresql://127.0.0.1:5432/pocket";

        String user_1 = "postgres";
        String pass_1 = "1111";

        Connection dbConnection_fly = null;
        Statement statement_fly = null;
        Connection dbConnection_hotel = null;
        Statement statement_hotel = null;
        Connection dbConnection_pocket = null;
        Statement statement_pocket = null;
        PreparedStatement statement_pocket_upd = null;

        String SQL_Begin_fly = "BEGIN;\n"
                + "INSERT INTO first (fly_id , _from , _to, price) VALUES (3 , 'KYiv' , 'Lviv' , 50 );\n"
                + "PREPARE TRANSACTION 'foobar_fly' ;";

        String SQL_Commit_fly = "COMMIT PREPARED 'foobar_fly';";

        String SQL_Begin_hotel = "BEGIN;\n"
                + "INSERT INTO main (_id , _city , price) VALUES ( 3 , 'Lviv' , 120);\n"
                + "PREPARE TRANSACTION 'foobar_hotel' ;";
        String SQL_Commit_hotel = "COMMIT PREPARED 'foobar_hotel';";
        String SQL_Rollback_hotel = "ROLLBACK PREPARED 'foobar_hotel';";
        String SQL_Rollback_fly = "ROLLBACK PREPARED 'foobar_fly';";
        String SQL_getFromPocket = "SELECT  price from pocket where id = 1";
        String SQL_updatePocket = "BEGIN;\n"
                + "UPDATE pocket SET  price = ? where id = 1"
                + "PREPARE TRANSACTION 'foobar_pocket' ;";
        String SQL_Commit_Pocket = "COMMIT PREPARED 'foobar_pocket';";
        String SQL_Rollback_Pocket = "ROLLBACK PREPARED 'foobar_pocket';";

        try {
            //------------------------CONNECTION--------------------------------
            dbConnection_fly = getDBConnection(url_fly, user_1, pass_1);
            statement_fly = dbConnection_fly.createStatement();

            dbConnection_hotel = getDBConnection(url_hotel, user_1, pass_1);
            statement_hotel = dbConnection_hotel.createStatement();

            dbConnection_pocket = getDBConnection(url_pocket, user_1, pass_1);
            //statement_pocket = dbConnection_pocket.createStatement();
            //------------------------------------------------------------------

            statement_fly.execute(SQL_Begin_fly);
            statement_hotel.execute(SQL_Begin_hotel);
            System.out.println("Record is inserted into DBUSER table!");

            
            ResultSet rs = statement_pocket.executeQuery(SQL_getFromPocket);
            while (rs.next()) {
                user_amount = rs.getInt("price");
                System.out.println("price = " + user_amount);
            }
            //----------------------------buy_a ticket--------------------------
            user_amount = user_amount - price_fly;
           //---------------------buy_a_hotel-----------------------------------
            user_amount = user_amount - price_hotel;
            //---------------------chrck_inesrt-----------------------------------
            statement_pocket_upd = dbConnection_pocket.prepareStatement(SQL_updatePocket);
            statement_pocket_upd.setInt(1, user_amount);
            statement_pocket_upd.executeUpdate();

            statement_fly.execute(SQL_Commit_fly);
            System.out.println("Commit FLY!");
            statement_hotel.execute(SQL_Commit_hotel);
            System.out.println("Commit HOTEL");
            statement_pocket_upd.execute(SQL_Commit_Pocket);
            System.out.println("Commit pocket");

        } catch (SQLException e) {

            statement_fly.execute(SQL_Rollback_fly);
            System.out.println("Rollback fly ");
            statement_hotel.execute(SQL_Rollback_hotel);
            System.out.println("Rollback hotel");
            statement_pocket.execute(SQL_Rollback_Pocket);
            System.out.println("Rollback Pocket");
            e.printStackTrace();

        } finally {

            if (statement_fly != null) {
                statement_fly.close();
            }
            if (statement_hotel != null) {
                statement_hotel.close();
            }

            if (dbConnection_fly != null) {
                dbConnection_fly.close();
            }
            if (dbConnection_hotel != null) {
                dbConnection_hotel.close();
            }

        }

    }
}
