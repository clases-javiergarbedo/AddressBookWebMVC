<%@page import="es.javiergarciaescobedo.addressbookmvc.Person"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Lista de contactos</h1>
        <table border="1">
            <tr>
                <th>Nombre</th>
                <th>Apellidos</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Móvil</th>
            </tr>
        <% 
            ArrayList<Person> personsList = (ArrayList)request.getAttribute("personsList"); 
            for(Person person: personsList) {
                out.println("<tr>");
                out.println("<td>"+person.getName()+"</td>");
                out.println("<td>"+person.getSurnames()+"</td>");
                out.println("<td>"+person.getEmail()+"</td>");
                out.println("<td>"+person.getPhoneNumber()+"</td>");
                out.println("<td>"+person.getMobileNumber()+"</td>");
                //Enlace para editar el registro
                String editLink = "Main?action=E&id="+person.getId();
                out.println("<td><a href='"+editLink+"'>Editar</td>");
                //Enlace para eliminar el registro con confirmación por parte del usuario
                String deleteLink = "Main?action=D&id="+person.getId();
                String deleteConfirmText = "Confirme que desea eliminar el contacto:\\n"+person.getName()+" "+person.getSurnames();
                out.println("<td><a href='"+deleteLink+"' onCLick='return confirm(\""+deleteConfirmText+"\")'>Suprimir</td>");
                
                out.println("</tr>");
            }
        %>
        </table>
        <br>
        <form method="get" action="Main">
            <input type="hidden" name="action" value="I">
            <input type="submit" value="Nuevo Contacto">
        </form>
        <form method="get" action="Main" target="_blank">
            <input type="hidden" name="action" value="X">
            <input type="submit" value="Exportar XML">
        </form>
    </body>
</html>
