<%@page import="es.javiergarbedo.addressbook.beans.Person"%>
<%@page import="es.javiergarbedo.addressbook.Main"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    //En request se reciben los datos enviados desde Main
    Person person = (Person)request.getAttribute("person");    
    String action = request.getParameter("action");    
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Datos del contacto</h1>
        <!-- Se añade enctype="multipart/form-data" para la subida de archivos -->
        <form method="post" action="Main" enctype="multipart/form-data">
        <!--<form method="post" action="Main">-->
            <input type="hidden" name="id" value="<%=person.getId()%>">
            Nombre: <input type="text" name="name" value="<%=person.getName()%>"><br>
            Apellidos: <input type="text" name="surnames" value="<%=person.getSurnames()%>"><br>
            Alias: <input type="text" name="alias" value="<%=person.getAlias()%>"><br>
            Email: <input type="text" name="email" value="<%=person.getEmail()%>"><br>
            Teléfono: <input type="text" name="phone_number" value="<%=person.getPhoneNumber()%>"><br>
            Tlf.Móvil: <input type="text" name="mobile_number" value="<%=person.getMobileNumber()%>"><br>
            Dirección: <input type="text" name="address" value="<%=person.getAddress()%>"><br>
            Código Postal: <input type="text" name="post_code" value="<%=person.getPostCode()%>"><br>
            Ciudad: <input type="text" name="city" value="<%=person.getCity()%>"><br>
            Provincia: <input type="text" name="province" value="<%=person.getProvince()%>"><br>
            Pais: <input type="text" name="country" value="<%=person.getCountry()%>"><br>
            <% 
                String strBirthDate = "";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if(person.getBirthDate()!=null) {
                    strBirthDate = dateFormat.format(person.getBirthDate());
                }
                
            %>
            Fecha nacimiento: <input type="text" name="birth_date" value="<%=strBirthDate%>"><br>
            Observaciones: <input type="text" name="comments" value="<%=person.getComments()%>"><br>
            
            Foto:<br><img src='<%=Main.SAVE_DIR+"/"+person.getPhotoFileName()%>' width="128px">
            <input type="checkbox" name="deletePhoto">Borrar foto (tendrá efecto después de guardar)<br>
            <input type="file" name="photoFileName"><br><br>
            
            <%  //Botón guardar para editar o insertar
                if(action.equals(Main.ACTION_EDIT_REQUEST)) {
                    out.print("<input type='submit' value='Guardar'>");
                    out.print("<input type='hidden' name='action' value='"+Main.ACTION_EDIT_RESPONSE+"'>");
                } else if(action.equals(Main.ACTION_INSERT_REQUEST)) {
                    out.print("<input type='submit' value='Añadir'>");
                    out.print("<input type='hidden' name='action' value='"+Main.ACTION_INSERT_RESPONSE+"'>");
                }
            %>
        </form>
        
        <%-- Botón para Cancelar cambios.
            Para que se muestre de nuevo la lista no hay que indicar 
            ninguna acción y volver a cargar Main --%>
        <form method="post" action="Main">
            <input type="hidden" name="action" value="">
            <input type="submit" value="Cancelar">
        </form>
    </body>
</html>
