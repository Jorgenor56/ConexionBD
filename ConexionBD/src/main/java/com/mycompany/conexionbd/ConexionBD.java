package com.mycompany.conexionbd;

import java.sql.*;
import java.util.Scanner; 

public class ConexionBD {

    //Datos para la  conexión a la base de datos
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jcvd";
    private static final String USER = "dam2";
    private static final String PASS = "1234";

    public static void main(String[] args) { 
        //Creo una instancia de la clase ConexionBD
        ConexionBD bd = new ConexionBD(); 

        //Llamadas de metodos que realizan distintas operaciones con la base de datos
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

    //Metodo para buscar un registro por nombre
    public boolean buscaNombre(String nombre) {
        //Consulta SQL para buscar un registro
        String query = "SELECT 1 FROM videojuegos WHERE Nombre = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            //Asigno el nombre al parámetro de la consulta
            pstmt.setString(1, nombre); 
            //Ejecuto la consulta
            try (ResultSet rs = pstmt.executeQuery()) { 
                //Devuelvo true si encuentra el registro
                return rs.next(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; //Devuelvo false si hay una excepción
        }
    }

    //Metodo para lanzar una consulta SQL
    public void lanzaConsulta(String consulta) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {
            ResultSetMetaData metaData = rs.getMetaData();
            //Obtengo la cantidad de columnas
            int columnCount = metaData.getColumnCount(); 
            //Itero sobre cada fila del resultado
            while (rs.next()) { 
                //Itero sobre cada columna
                for (int i = 1; i <= columnCount; i++) { 
                    //Muestro el nombre de la columna y su valor
                    System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace(); //Muestro la excepción
        }
    }

    public void nuevoRegistro(String nombre, String genero, String fechaLanzamiento, String compania, double precio) {
        //Consulta SQL preparada
        String insertQuery = "INSERT INTO videojuegos (Nombre, Genero, FechaLanzamiento, Compañia, Precio) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            //Asigno los valores a los parámetros de la consulta
            pstmt.setString(1, nombre);
            pstmt.setString(2, genero);
            pstmt.setDate(3, Date.valueOf(fechaLanzamiento));
            pstmt.setString(4, compania);
            pstmt.setDouble(5, precio);
            pstmt.executeUpdate(); //Ejecuto la consulta
        } catch (SQLException e) {
            e.printStackTrace(); //Imprimo la traza de la excepción
        }
    }

    //Metodo para insertar un registro mediante entrada de datos por teclado
    public void nuevoRegistroPorTeclado() {
        Scanner scanner = new Scanner(System.in);
        //Solicito datos al usuario y los lee desde el teclado
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

        nuevoRegistro(nombre, genero, fechaLanzamiento, compania, precio); // Llama al método para insertar el registro
    }

    //Metodo para eliminar un registro de la base de datos
    public boolean eliminarRegistro(String nombre) {
        //Consulta SQL preparada
        String deleteQuery = "DELETE FROM videojuegos WHERE Nombre = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            //Asigno el nombre al parámetro de la consulta
            pstmt.setString(1, nombre); 
            //Ejecuto la consulta y obtiene el número de filas afectadas
            int affectedRows = pstmt.executeUpdate(); 
            //Devuelvo true si se eliminó algún registro
            return affectedRows > 0; 
        } catch (SQLException e) {
            e.printStackTrace(); //Imprimo la traza de la excepción
            return false; //Devuelve false si hay una excepción
        }
    }
}
