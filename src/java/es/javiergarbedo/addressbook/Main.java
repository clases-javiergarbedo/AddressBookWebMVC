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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Javier García Escobedo (javiergarbedo.es)
 * @version 0.0.1
 * @date 2014-02-25
 */
@WebServlet(name = "Main", urlPatterns = {"/Main"})
public class Main extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddressBookDBManagerMySQL.class.getName());
    public static final String ACTION_EDIT_REQUEST = "E";
    public static final String ACTION_EDIT_RESPONSE = "S";
    public static final String ACTION_INSERT_REQUEST = "I";
    public static final String ACTION_INSERT_RESPONSE = "A";
    public static final String ACTION_DELETE = "D";
    public static final String ACTION_EXPORT_XML = "X";

    /**
     * Este método es ejecutado cada vez que se hace una llamada a esta página
     * @param request Contiene los datos que se pasan a esta página mediante parámetros GET o POST
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AddressBookDBManagerMySQL.connect("localhost", "address_book", "root", "root");

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
    }

    /**
     * Redirige la navegación web a la página indicada en newUrl, pasándole
     * en request los datos que necesite
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
        person.setPhotoFileName(request.getParameter("photo_file_name"));
        //Convierte a tipo Date la fecha de nacimento, que se recibe en formato 
        //  String desde el request, para almacenarla en el objeto Person
        if(!request.getParameter("birth_date").isEmpty()) {
            try {
                Calendar birthDate = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate.setTime(sdf.parse(request.getParameter("birth_date")));
                person.setBirthDate(birthDate.getTime());
            } catch (ParseException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                
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
