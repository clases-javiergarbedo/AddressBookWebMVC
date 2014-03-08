/*
 * Copyright (C) 2014 Javier García Escobedo (javiergarbedo.es)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.javiergarbedo.addressbook.db;

import es.javiergarbedo.addressbook.beans.Person;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier García Escobedo (javiergarbedo.es)
 * @version 0.1.0
 * @date 2014-02-26
 */
public class AddressBookDBManagerMySQL {

    private static Connection connection;

    public static void connect(String databaseServer, String databaseName, String databaseUser, String databasePassword) {
        String strConection = "jdbc:mysql://" + databaseServer + "/" + databaseName;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(strConection, databaseUser, databasePassword);
            createTables();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1049) {
                Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                        "Database not found: " + strConection + " - " + databaseUser + "/" + databasePassword);
                createDatabase(databaseServer, databaseName, databaseUser, databasePassword);
                Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                        "Database created");
                try {
                    connection = DriverManager.getConnection(strConection, databaseUser, databasePassword);
                    createTables();
                } catch (SQLException ex1) {
                    Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean isConnected() {
        if(connection!=null) {
            return true;
        } else {
            return false;
        }
    }

    private static void createDatabase(String databaseServer, String databaseName, String databaseUser, String databasePassword) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String strConecction = "jdbc:mysql://" + databaseServer;
            connection = DriverManager.getConnection(strConecction, databaseUser, databasePassword);
            Statement statement = connection.createStatement();
            String sql = "CREATE DATABASE " + databaseName;
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            boolean result = statement.execute(sql);
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createTables() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS person ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(50), "
                    + "surnames VARCHAR(100), "
                    + "alias VARCHAR(50), "
                    + "email VARCHAR(50), "
                    + "phone_number VARCHAR(50), "
                    + "mobile_number VARCHAR(50), "
                    + "address VARCHAR(255), "
                    + "post_code VARCHAR(10), "
                    + "city VARCHAR(50), "
                    + "province VARCHAR(50), "
                    + "country VARCHAR(50), "
                    + "birth_date DATE, "
                    + "comments TEXT, "
                    + "photo_file_name VARCHAR(50))";
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            boolean result = statement.execute(sql);
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertPerson(Person person) {
        //Formato para campos de tipo fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("''yyyy-MM-dd''");
        Date birthDate = person.getBirthDate();
        String birthDateSql = null;
        if (birthDate != null) {
            birthDateSql = dateFormat.format(birthDate);
        }

        try {
            String sql = "INSERT INTO person "
                    //No se incluye el ID, ya que es autonumérico
                    + "(name, surnames, alias, email, phone_number, mobile_number, address, "
                    + "post_code, city, province, country, birth_date, comments, photo_file_name) "
                    + "VALUES ("
                    + "'" + person.getName() + "', "
                    + "'" + person.getSurnames() + "', "
                    + "'" + person.getAlias() + "', "
                    + "'" + person.getEmail() + "', "
                    + "'" + person.getPhoneNumber() + "', "
                    + "'" + person.getMobileNumber() + "', "
                    + "'" + person.getAddress() + "', "
                    + "'" + person.getPostCode() + "', "
                    + "'" + person.getCity() + "', "
                    + "'" + person.getProvince() + "', "
                    + "'" + person.getCountry() + "', "
                    + birthDateSql + ", "
                    + "'" + person.getComments() + "', "
                    + "'" + person.getPhotoFileName() + "')";
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            boolean result = statement.execute(sql);
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Person> getPersonsList() {
        ArrayList<Person> personsList = new ArrayList();
        try {
            String sql = "SELECT * FROM person";
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            ResultSet rs = statement.executeQuery(sql);
            boolean result = rs.isBeforeFirst();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
            while (rs.next()) {
                int columnIndex = 1;
                int id = rs.getInt(columnIndex++);
                String name = rs.getString(columnIndex++);
                String surnames = rs.getString(columnIndex++);
                String alias = rs.getString(columnIndex++);
                String email = rs.getString(columnIndex++);
                String phoneNumber = rs.getString(columnIndex++);
                String mobileNumber = rs.getString(columnIndex++);
                String address = rs.getString(columnIndex++);
                String postCode = rs.getString(columnIndex++);
                String city = rs.getString(columnIndex++);
                String province = rs.getString(columnIndex++);
                String country = rs.getString(columnIndex++);
                Date birthDate = rs.getDate(columnIndex++);
                String comments = rs.getString(columnIndex++);
                String photoFileName = rs.getString(columnIndex++);
                Person person = new Person(id, name, surnames, alias, email, phoneNumber, mobileNumber, address, postCode, city, province, country, birthDate, comments, photoFileName);
                personsList.add(person);
            }
            return personsList;
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Person getPersonByID(int personId) {
        Person person = null;
        try {
            String sql = "SELECT * FROM person WHERE id=" + personId;
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            ResultSet rs = statement.executeQuery(sql);
            boolean result = rs.isBeforeFirst();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
            if (rs.next()) {
                int columnIndex = 1;
                int id = rs.getInt(columnIndex++);
                String name = rs.getString(columnIndex++);
                String surnames = rs.getString(columnIndex++);
                String alias = rs.getString(columnIndex++);
                String email = rs.getString(columnIndex++);
                String phoneNumber = rs.getString(columnIndex++);
                String mobileNumber = rs.getString(columnIndex++);
                String address = rs.getString(columnIndex++);
                String postCode = rs.getString(columnIndex++);
                String city = rs.getString(columnIndex++);
                String province = rs.getString(columnIndex++);
                String country = rs.getString(columnIndex++);
                Date birthDate = rs.getDate(columnIndex++);
                String comments = rs.getString(columnIndex++);
                String photoFileName = rs.getString(columnIndex++);
                person = new Person(id, name, surnames, alias, email, phoneNumber, mobileNumber, address, postCode, city, province, country, birthDate, comments, photoFileName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return person;
    }

    public static void updatePerson(Person person) {
        //Formato para campos de tipo fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("''yyyy-MM-dd''");
        Date birthDate = person.getBirthDate();
        String birthDateSql = null;
        if (birthDate != null) {
            birthDateSql = dateFormat.format(birthDate);
        }
        try {
            String sql = "UPDATE person SET "
                    + "name='" + person.getName() + "', "
                    + "surnames='" + person.getSurnames() + "', "
                    + "alias='" + person.getAlias() + "', "
                    + "email='" + person.getEmail() + "', "
                    + "phone_number='" + person.getPhoneNumber() + "', "
                    + "mobile_number='" + person.getMobileNumber() + "', "
                    + "address='" + person.getAddress() + "', "
                    + "post_code='" + person.getPostCode() + "', "
                    + "city='" + person.getCity() + "', "
                    + "province='" + person.getProvince() + "', "
                    + "country='" + person.getCountry() + "', "
                    + "birth_date=" + birthDateSql + ", "
                    + "comments='" + person.getComments() + "', "
                    + "photo_file_name='" + person.getPhotoFileName() + "' "
                    + "WHERE id=" + person.getId();
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            boolean result = statement.execute(sql);
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deletePersonById(String id) {
        try {
            String sql = "DELETE FROM person WHERE id=" + id;
            Statement statement = connection.createStatement();
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "Executing SQL statement: " + sql);
            boolean result = statement.execute(sql);
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.FINE,
                    "SQL result: " + result);
        } catch (SQLException ex) {
            Logger.getLogger(AddressBookDBManagerMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
