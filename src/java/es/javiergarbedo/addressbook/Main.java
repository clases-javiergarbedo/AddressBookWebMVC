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
package es.javiergarbedo.addressbook;

import es.javiergarbedo.addressbook.db.AddressBookDBManagerMySQL;
import es.javiergarbedo.addressbook.beans.Person;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Javier García Escobedo (javiergarbedo.es)
 * @version 0.2.0
 * @date 2014-02-27
 */
@WebServlet(name = "Main", urlPatterns = {"/Main"})
//Anotación requerida para la subida de archivos usando enctype="multipart/form-data"
@MultipartConfig
public class Main extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddressBookDBManagerMySQL.class.getName());
    
    public static final String ACTION_EDIT_REQUEST = "E";
    public static final String ACTION_EDIT_RESPONSE = "S";
    public static final String ACTION_INSERT_REQUEST = "I";
    public static final String ACTION_INSERT_RESPONSE = "A";
    public static final String ACTION_DELETE = "D";
    public static final String ACTION_EXPORT_XML = "X";

    //Nombre de la carpeta donde se guardarán las imágenes subidas
    public static final String SAVE_DIR = "uploadFiles";

    /**
     * Este método es ejecutado cada vez que se hace una llamada a esta página
     *
     * @param request Contiene los datos que se pasan a esta página mediante
     * parámetros GET o POST
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Obtener los datos de conexión a la BD desde un archivo de propiedades
        Properties properties = new Properties();
        properties.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
        String dbServer = properties.getProperty("db_server");
        String dbName = properties.getProperty("db_name");
        String dbUser = properties.getProperty("db_user");
        String dbPassword = properties.getProperty("db_password");

        //Conexión con la base de datos
        AddressBookDBManagerMySQL.connect(dbServer, dbName, dbUser, dbPassword);

        //Comprueba si se ha podido realizar la conexión
        if (AddressBookDBManagerMySQL.isConnected()) {
            String action = request.getParameter("action");
            logger.fine("action = " + action);
            if (action != null && !action.isEmpty()) {
                //Se ha llamado a Main tras pulsar el enlace de Editar en la lista
                if (action.equals(ACTION_EDIT_REQUEST)) {
                    //Obtener el id de la persona a partir del parámetro is que se
                    //  ha debido utilizar al realizar la llamada a esta página Main
                    int id = Integer.valueOf(request.getParameter("id"));
                    //Lee de la BD los datos de la persona con el id solicitado
                    Person person = AddressBookDBManagerMySQL.getPersonByID(id);
                    //Se prepara el objeto Person generado para pasarlo a otra página
                    request.setAttribute("person", person);
                    //Se redirige a otra página que muestra el detalle de la persona,
                    //  pasando en request la persona
                    redirectTo("person_detail.jsp", request, response);
                    //Se ha llamado a Main tras pulsar el botón Guardar en la página de 
                    //  detalle cuando se estaba editando una persona existente
                } else if (action.equals(ACTION_EDIT_RESPONSE)) {
                    int id = Integer.valueOf(request.getParameter("id"));
                    Person person = AddressBookDBManagerMySQL.getPersonByID(id);
                    //Se modifican los datos que había en la BD, asignado los datos
                    //  que se han introducido en la página de detalle. Esos datos
                    //  se reciben como parámetros de la llamada (request) a esta página Main 
                    updatePersonWithRequestData(person, request);
                    //Se actualiza en la BD la persona
                    AddressBookDBManagerMySQL.updatePerson(person);
                    //Volvemos a recargar la página Main con lista (no se indica 
                    //  ninguna acción si se quiere mostrar la lista)
                    response.sendRedirect("Main?action=");
                    //Se ha llamado a Main tras pulsar el botón Insertar
                } else if (action.equals(ACTION_INSERT_REQUEST)) {
                    Person person = new Person();
                    request.setAttribute("person", person);
                    redirectTo("person_detail.jsp", request, response);
                    //Se ha llamado a Main tras pulsar el botón Guardar en la página de 
                    //  detalle cuando se estaba insertando una nueva persona
                } else if (action.equals(ACTION_INSERT_RESPONSE)) {
                    Person person = new Person();
                    updatePersonWithRequestData(person, request);
                    AddressBookDBManagerMySQL.insertPerson(person);
                    response.sendRedirect("Main?action=");
                    //Se ha llamado a Main tras pulsar el enlace de Eliminar en la lista
                } else if (action.equals(ACTION_DELETE)) {
                    String id = request.getParameter("id");
                    //Se borra de la BD la persona con el ID indicado
                    AddressBookDBManagerMySQL.deletePersonById(id);
                    response.sendRedirect("Main?action=");
                    //Se ha llamado a Main tras pulsar el botón Exportar XML
                } else if (action.equals(ACTION_EXPORT_XML)) {
                    //Se obtiene desde la BD una lista con todas las personas
                    ArrayList<Person> personsList = AddressBookDBManagerMySQL.getPersonsList();
                    //Se prepara la lista obtenida para pasarla a otra página
                    request.setAttribute("personsList", personsList);
                    //Se redirige a otra página que genera el XML pasando en request 
                    //  la lista de personas
                    redirectTo("export_xml.jsp", request, response);
                }
                //Si no se indica ninguna acción se entiende que se quiere mostrar la lista
            } else {
                ArrayList<Person> personsList = AddressBookDBManagerMySQL.getPersonsList();
                request.setAttribute("personsList", personsList);
                redirectTo("person_list.jsp", request, response);
            }
        } else { //No se ha podido hacer la conexión con la BD
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("Error: No se ha podido conectar con la BD.<br>");
            out.println("Compruebe la configuración en el archivo 'config.properties'");
            out.close();
        }
    }

    /**
     * Redirige la navegación web a la página indicada en newUrl, pasándole en
     * request los datos que necesite
     *
     * @param newUrl
     * @param request
     * @param response
     */
    private static void redirectTo(String newUrl, HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestDispatcher dispatcher = null;
            dispatcher = request.getRequestDispatcher(newUrl);
            dispatcher.forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Actualiza los datos de un objeto Person, usando los datos recibidos en
     * request cuyo contenido se ha formado en person_detail.jsp
     *
     * @param person
     * @param request
     */
    private void updatePersonWithRequestData(Person person, HttpServletRequest request) {
        person.setName(request.getParameter("name"));
        person.setSurnames(request.getParameter("surnames"));
        person.setAlias(request.getParameter("alias"));
        person.setEmail(request.getParameter("email"));
        person.setPhoneNumber(request.getParameter("phone_number"));
        person.setMobileNumber(request.getParameter("mobile_number"));
        person.setAddress(request.getParameter("address"));
        person.setPostCode(request.getParameter("post_code"));
        person.setCity(request.getParameter("city"));
        person.setProvince(request.getParameter("province"));
        person.setCountry(request.getParameter("country"));
        person.setComments(request.getParameter("comments"));
        //Convierte a tipo Date la fecha de nacimento, que se recibe en formato 
        //  String desde el request, para almacenarla en el objeto Person
        if (!request.getParameter("birth_date").isEmpty()) {
            try {
                Calendar birthDate = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate.setTime(sdf.parse(request.getParameter("birth_date")));
                person.setBirthDate(birthDate.getTime());
            } catch (ParseException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Asignación de foto
        //Consultar si se ha solicitado dejar el contacto sin foto activando el 
        //  checkbox "deletePhoto" del formulario
        logger.fine("DeletePhoto: "+request.getParameter("deletePhoto"));
        if(request.getParameter("deletePhoto")!=null && request.getParameter("deletePhoto").equals("on")) {
            //Si el contacto tenía alguna foto, borrar el archivo y dejar la propiedad foto en blanco
            if(!person.getPhotoFileName().isEmpty() && person.getPhotoFileName()!=null) {
                deletePhotoFile(request, person.getPhotoFileName());
                person.setPhotoFileName("");
            }
        } else { //No se ha pedido dejar sin foto
            //Guardar la imagen en el servidor y asignar el nombre a la persona
            String photoFileName = savePhotoFile(request);
            if(photoFileName!=null) {
                if(person.getPhotoFileName().isEmpty() || person.getPhotoFileName()==null) {
                    //Si no tenía foto, le asigna la nueva
                    person.setPhotoFileName(photoFileName);
                } else {
                    //Si ya tenía foto, borra el archivo antiguo y asigna la nueva
                    deletePhotoFile(request, person.getPhotoFileName());
                    person.setPhotoFileName(photoFileName);                
                }
            }
        }
    }
    
    /**
     * Almacena la imagen indicada en el input "photoFileName" del formulario
     * dentro de la carpeta indicada en la constante SAVE_DIR, generando un
     * nombre nuevo para el archivo en función de una codificación MD5
     * 
     * @param request
     * @return Nombre generado para la imagen o null si no se ha enviado ningún
     * archivo
     */
    private String savePhotoFile(HttpServletRequest request) {
        //Crear la ruta completa donde se guardarán las imágenes
        String appPath = request.getServletContext().getRealPath("");
        String savePath = appPath + File.separator + SAVE_DIR;
        logger.fine("Save path = " + savePath);
        
        //Crear la carpeta si no existe
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }

        //Crear un nombre para la imagen utilizando un código MD5 en función
        //  del tiempo y una palabra secreta. 
        //  Se utiliza una codificación de este tipo para que se dificulte la 
        //  localización no permitida de las imágenes
        String secret = "W8fQAP9X";
        long time = Calendar.getInstance().getTimeInMillis();
        //Para generar el código MD5 se usa la clase DigestUtils de la librería 
        //  org.apache.commons.codec (se incluye en la carpeta 'libs' del proyecto)
        String fileName = DigestUtils.md5Hex(secret+time);
                
        try {
            //Obtener los datos enviados desde el input "photoFileName" del formulario
            Part filePart = request.getPart("photoFileName");
            String fullPathFile = savePath + File.separator + fileName;
            //Guardar la imagen en la ruta y nombre indicados
            if(filePart.getSize()>0) {
                filePart.write(fullPathFile);
                logger.fine("Saved file: " + fullPathFile);
                return fileName;
            } else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServletException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        return null;
    }
    
    private void deletePhotoFile(HttpServletRequest request, String photoFileName) {
        String appPath = request.getServletContext().getRealPath("");
        String filePath = appPath + File.separator + SAVE_DIR + File.separator + photoFileName ;
        File photoFile = new File(filePath);
        photoFile.delete();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
