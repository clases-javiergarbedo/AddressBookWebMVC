package es.javiergarciaescobedo.addressbookmvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileOperations {

    private static final Logger LOG = Logger.getLogger(FileOperations.class.getName());

    /**
     * Obtiene la información contenida en el archivo data/agenda.csv, generando
     * una lista de objetos Person
     * @return Lista de objetos Person generada a partir de los datos del archivo, 
     * o null en caso de producirse algún error de acceso o procesamiento del archivo
     */
    public static ArrayList<Person> dataImport(InputStream is) {
        LOG.fine("Starting data import");
        // Inicialmente no se crea la lista para retornar null si hay algún problema
        ArrayList<Person> personList = null;
        BufferedReader br = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        try {
            br = new BufferedReader(new InputStreamReader(is));
            // Lista en la que se almacenarán todos los objetos Person
            personList = new ArrayList<Person>();

            String line = br.readLine();
            while (line != null) {
                LOG.finest("Line read: \n" + line);
                // Obtener por separado cada dato que está contenido en una línea
                String[] data = line.split(";");
                // Crear un objeto con los datos
                Person person = new Person();
                person.setName(data[0]);
                person.setSurnames(data[1]);
                person.setAddress(data[4]);
                person.setPostCode(data[5]);
                person.setCity(data[6]);
                person.setPhoneNumber(data[7]);
                person.setMobileNumber(data[8]);
                person.setBirthDate(formatter.parse(data[9]));
                // Almacenar el objeto en la lista
                personList.add(person);
                LOG.finest("Person added to list: " + person.getName() + " " + person.getSurnames());

                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            LOG.log(Level.WARNING, "File not found", ex);
            return null;
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Error reading file", ex);
            return null;
        } catch (IndexOutOfBoundsException ex) {
            LOG.log(Level.WARNING, "Error parsing file", ex);
            return null;
        } catch (ParseException ex) {
            LOG.log(Level.WARNING, "Error parsing date in file", ex);
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Error closing file", ex);
            }
        }
        return personList;
    }

}
