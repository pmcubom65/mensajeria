package com.example.mensajesactividad;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class ConexionBBDD {

    private static final String LOG = "DEBUG";
    private static String ip = "localhost";
    private static String port = "3306";
    private static String classs = "net.sourceforge.jtds.jdbc.Driver";
    private static String db = "SMARTLABS_CHAT";
    private static String un = "root";
    private static String password = "";
    private static Connection conn = null;
    private static String ConnURL = null;


    public static Connection connect() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(classs);
           ConnURL = "jdbc:jtds:sqlserver://" + ip +":"+port+";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException e) {
            Log.d(LOG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d(LOG, e.getMessage());
        }
        return conn;
    }


    public void insertarMensaje(String fecha) {
        PreparedStatement comm;
        try {
            comm = conn.prepareStatement("insert into Chat("
                    + "hora_inicio) values(?)");
            comm.setString(1, fecha);

            comm.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Error en la creación de chat");
        }
    }


    public void crearChat(String fecha) {
        PreparedStatement comm;
        try {
            comm = conn.prepareStatement("insert into Chat("
                    + "hora_inicio) values(?)");
            comm.setString(1, fecha);

            comm.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Error en la creación de chat");
        }
    }

}
