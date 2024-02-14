package com.campusland.respository.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.sql.Statement;


import com.campusland.utils.Formato;
import com.campusland.utils.conexionpersistencia.conexionbdmysql.ConexionBDMysql;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import java.util.Locale;

@Data
public class Factura {

    private int numeroFactura;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")    
    private LocalDateTime fecha;
    private Cliente cliente;
    private List<ItemFactura> items;
    private static int nextNumeroFactura;

    public Factura(){

    }

    public String getDayofWeek(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }

    public Factura(int numeroFactura, LocalDateTime fecha, Cliente cliente) {
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.cliente = cliente;
        this.items = new ArrayList<>();
    }

    public Factura(LocalDateTime fecha, Cliente cliente) {
        this.numeroFactura = ++nextNumeroFactura;
        this.fecha = fecha;
        this.cliente = cliente;
        this.items = new ArrayList<>();
    }

    public double getTotalFactura() {
        double totalFactura = 0;
        for (ItemFactura item : items) {
            totalFactura += item.getImporte();
        }
        return totalFactura;
    }


    public double getDescuentoTotal() {
        double descuentoTipo1 = validarDescuento1();
        double descuentoTipo2 = validarDescuento2();
        double descuentoTipo3 = validarDescuento3(this.cliente.getDocumento());
        double descuentoTipo4 = validarDescuento4();
        double descuentoTipo5 = validarDescuento5();
    

        double descuentoTotal = descuentoTipo1 + descuentoTipo2 + descuentoTipo3 + descuentoTipo4 + descuentoTipo5;
    
        if (descuentoTotal > 1) {
            descuentoTotal = 1;
        }
    
        return descuentoTotal;
    }

    public double validarDescuento1() {
        Double total_factura = this.getTotalFactura();
        double descuento = 0;

        if(total_factura > 1000){
            descuento += 0.10;
        }

        return descuento;
    }


    public double validarDescuento2() {
        double descuento = 0;
        for (ItemFactura item : this.items) {
            if(item.getNombreProducto().equals("Producto3")){
                if(item.getCantidad() > 5){
                    descuento += 0.05; // Changed from 5 to 0.05
                }
            }
        }
        return descuento;
    }


    public double validarDescuento3(String clienteDocumento) {
        double descuento = 0.0;
        try {
            Connection connection = ConexionBDMysql.getInstance();
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) as compras_totales, CONCAT(cliente.nombre, ' ', cliente.apellido) AS 'Cliente', cliente.documento FROM factura " +
                        "JOIN cliente ON cliente.id = factura.cliente_id " +
                        "WHERE cliente.documento = '" + clienteDocumento + "' " +
                        "GROUP BY cliente_id";
            ResultSet resultSet = statement.executeQuery(sql);
    
            while (resultSet.next()) {
                int numCompras = resultSet.getInt("compras_totales");
    
                if (numCompras >= 10) {
                    descuento = 0.05;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return descuento;
    }

    public double validarDescuento4() {
        double descuento = 0;
        String dia = getDayofWeek(new Date(), Locale.ENGLISH);

        if(dia.equals("Friday")){
            descuento = 0.05;
        }
        return descuento;
    }

    public double validarDescuento5() {
        double descuento = 0;
        DateFormat monthFormatter = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        Date currentDate = new Date();
        String currentMonth = monthFormatter.format(currentDate);
    
        if(currentMonth.equals("December")) {
            descuento = 0.05;
        }
    
        return descuento;
    }


    public void agregarItem(ItemFactura item){
        this.items.add(item);
    }

    public void display() {
        double descuentoTotal = this.getDescuentoTotal();
    
        if (descuentoTotal > 1) {
            descuentoTotal = 1;
        }
    
        double totalFactura = this.getTotalFactura();
        double totalConIva = totalFactura * 1.19;
        double totalConDescuento = totalConIva * (1 - descuentoTotal);
    
        System.out.println();
        System.out.println("Factura: " + this.numeroFactura);
        System.out.println("Cliente: " + this.cliente.getFullName());
        System.out.println("Fecha: " + Formato.formatoFechaHora(this.getFecha()));
        System.out.println("-----------Detalle Factura----------------------");
        for (ItemFactura item : this.items) {
            System.out.println(item.getProducto().getCodigoNombre() + " " + item.getCantidad() + " "
                    + Formato.formatoMonedaPesos(item.getImporte()));
        }
        System.out.println();
        System.out.println("Total                        " + Formato.formatoMonedaPesos(totalFactura));
        System.out.println("Total CON IVA                " + Formato.formatoMonedaPesos(totalConIva));
        System.out.println("Descuento                    " + Formato.formatoMonedaPesos(totalConIva - totalConDescuento));
        System.out.println("Total CON DESCUENTO          " + Formato.formatoMonedaPesos(totalConDescuento));
    }

}
