package com.campusland.views;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.campusland.utils.conexionpersistencia.conexionbdmysql.ConexionBDMysql;


public class ViewReportes extends ViewMain {
    
    public static void startMenu() {

        int op = 0;

        do {

            op = mostrarMenu();
            switch (op) {
                case 1:
                    generarArchivo();
                    break;
                case 2:
                    informeVentas();
                    break;
                case 3:
                    informeDescuentos();
                    break;
                case 4:
                    informeImpuestos();
                    break;
                case 5:
                    listadoClientes();
                    break;
                case 6:
                    listadoProductos();
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }

        } while (op >= 1 && op < 6);

    }

    public static int mostrarMenu() {
        System.out.println("----Menu Reportes ----");
        System.out.println("1. generar archivo DIAN");
        System.out.println("2. Informe total ventas");
        System.out.println("3. Informe total Descuentos");
        System.out.println("4. Informe total impuestos");
        System.out.println("5. Listado Clientes x compras");
        System.out.println("6. Listado Producto mas vendido");
        return leer.nextInt();
    }


    public static void generarArchivo() {

    }

    public static void informeVentas() {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultado = null;
        try {
            conexion = ConexionBDMysql.getInstance();
            String query = "SELECT SUM(importe) as total_ventas FROM item_factura";
            statement = conexion.prepareStatement(query);
            resultado = statement.executeQuery();

            System.out.println("+---------------------+");
            System.out.printf("| %-20s |%n", "total_ventas");
            System.out.println("+---------------------+");
        

            while (resultado.next()) {
                System.out.printf("| %-20s |%n", resultado.getDouble("total_ventas"));
                System.out.println("+---------------------+");
            }
        } catch (SQLException e) {
            System.out.println("\n---> Ocurrió un error al traer la informacion\n\n");
            e.printStackTrace();
        } finally {
            ConexionBDMysql.cerrarRecursos(conexion, statement, resultado);
        }
    }

    public static void informeDescuentos() {
        //añadir columna descuento el faactura con el total
    }

    public static void informeImpuestos() {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultado = null;

        try {
            conexion = ConexionBDMysql.getInstance();
            String query = "SELECT SUM(iva) as total_impuestos FROM item_factura";
            statement = conexion.prepareStatement(query);
            resultado = statement.executeQuery();

            System.out.println("+---------------------+");
            System.out.printf("| %-20s |%n", "total_impuestos");
            System.out.println("+---------------------+");
        

            while (resultado.next()) {
                System.out.printf("| %-20s |%n", resultado.getDouble("total_impuestos"));
                System.out.println("+---------------------+");
            }
        } catch (SQLException e) {
            System.out.println("\n---> Ocurrió un error al traer la informacion\n\n");
            e.printStackTrace();
        } finally {
            ConexionBDMysql.cerrarRecursos(conexion, statement, resultado);
        }
    }


    public static void listadoClientes() {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultado = null;

        try {
            conexion = ConexionBDMysql.getInstance();
            String query = "SELECT * FROM listado_clientes";
            statement = conexion.prepareStatement(query);
            resultado = statement.executeQuery();

            System.out.println("+---------------------+----------------------+------------+---------------------+");
            System.out.printf("| %-20s | %-20s | %-20s |%n", "Compras_totales", "Cliente", "Documento_cliente");
            System.out.println("+---------------------+----------------------+------------+---------------------+");
        

            while (resultado.next()) {
                System.out.printf("| %-20s | %-30s | %-160s |%n", resultado.getInt("compras_totales"), resultado.getString("Cliente"), resultado.getString("documento"));
                System.out.println("+---------------------+----------------------+------------+---------------------+");
            }
        } catch (SQLException e) {
            System.out.println("\n---> Ocurrió un error al traer la informacion\n\n");
            e.printStackTrace();
        } finally {
            ConexionBDMysql.cerrarRecursos(conexion, statement, resultado);
        }
    }

    public static void listadoProductos() {

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultado = null;
        try {
            conexion = ConexionBDMysql.getInstance();
            String query = "SELECT * FROM productos_mas_vendidos";
            statement = conexion.prepareStatement(query);
            resultado = statement.executeQuery();

            System.out.println("+---------------------+---------------------+");
            System.out.printf("| %-20s |%n", "nombre", "ventas_totales");
            System.out.println("+---------------------+---------------------+");
        

            while (resultado.next()) {
                System.out.printf("| %-20s |%n", resultado.getString("nombre"), resultado.getDouble("ventas_totales"));
                System.out.println("+---------------------+---------------------+");
            }
        } catch (SQLException e) {
            System.out.println("\n---> Ocurrió un error al traer la informacion\n\n");
            e.printStackTrace();
        } finally {
            ConexionBDMysql.cerrarRecursos(conexion, statement, resultado);
        }
    }


}
