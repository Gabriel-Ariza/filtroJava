package com.campusland.utils.conexionpersistencia.conexionbdmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.campusland.utils.Configuracion;

public class ConexionBDMysql {

    private static String url = Configuracion.obtenerValor("db.url");
    private static String username = Configuracion.obtenerValor("db.username");
    private static String password = Configuracion.obtenerValor("db.password");
    private static Connection connection;


    
    public static Connection getInstance() throws SQLException {

        return connection = DriverManager.getConnection(url, username, password);

    }


    public static void cerrarRecursos(Connection conexion, PreparedStatement statement, ResultSet resultado) {
        try {
            if (resultado != null) {
                resultado.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (conexion != null) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}