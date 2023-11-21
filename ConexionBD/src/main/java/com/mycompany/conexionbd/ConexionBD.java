package com.mycompany.conexionbd;

import java.sql.*;
import java.util.Scanner;

public class ConexionBD {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/jcvd";
    private static final String USER = "dam2";
    private static final String PASS = "1234";

    public static void main(String[] args) {
        ConexionBD bd = new ConexionBD();

        if (bd.buscaNombre("UnNombreDeJuego")) {
            System.out.println("El juego existe en la base de datos.");
        }

        bd.lanzaConsulta("SELECT * FROM videojuegos");

        bd.nuevoRegistro("NuevoJuego", "Aventura", "2023-01-01", "NuevaCompañia", 59.99);

        bd.nuevoRegistroPorTeclado();

        if (bd.eliminarRegistro("UnNombreDeJuego")) {
            System.out.println("El juego ha sido eliminado correctamente.");
        }
    }

    public boolean buscaNombre(String nombre) {
        String query = "SELECT 1 FROM videojuegos WHERE Nombre = ?";
        try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            try ( ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void lanzaConsulta(String consulta) {
        try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  
            Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(consulta)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void nuevoRegistro(String nombre, String genero, String fechaLanzamiento, String compania, double precio) {
        String insertQuery = "INSERT INTO videojuegos (Nombre, Genero, FechaLanzamiento, Compañia, Precio) VALUES (?, ?, ?, ?, ?)";
        try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  
            PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, genero);
            pstmt.setDate(3, Date.valueOf(fechaLanzamiento));
            pstmt.setString(4, compania);
            pstmt.setDouble(5, precio);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void nuevoRegistroPorTeclado() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce el nombre del videojuego:");
        String nombre = scanner.nextLine();
        System.out.println("Introduce el género del videojuego:");
        String genero = scanner.nextLine();
        System.out.println("Introduce la fecha de lanzamiento del videojuego (YYYY-MM-DD):");
        String fechaLanzamiento = scanner.nextLine();
        System.out.println("Introduce la compañía del videojuego:");
        String compania = scanner.nextLine();
        System.out.println("Introduce el precio del videojuego:");
        double precio = scanner.nextDouble();
        scanner.close();

        nuevoRegistro(nombre, genero, fechaLanzamiento, compania, precio);
    }

    public boolean eliminarRegistro(String nombre) {
        String deleteQuery = "DELETE FROM videojuegos WHERE Nombre = ?";
        try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, nombre);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
