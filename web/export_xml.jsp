<?xml version="1.0" encoding="UTF-8"?>
<%-- La línea anterior debe ir siempre la primera si se genera un XML --%>

<%@page import="java.util.ArrayList"%>
<%@page import="es.javiergarbedo.addressbook.beans.Person"%>

<%-- Se informa que el contenido va a ser XML --%>
<%@page contentType="text/xml" pageEncoding="UTF-8"%>

<persons>
<% 
    ArrayList<Person> personsList = (ArrayList)request.getAttribute("personsList"); 
    for(Person person: personsList) {
        out.println("<person>");
        out.println("<id>"+person.getId()+"</id>");
        out.println("<name>"+person.getName()+"</name>");
        out.println("<surnames>"+person.getSurnames()+"</surnames>");
        out.println("<alias>"+person.getAlias()+"</alias>");
        out.println("<email>"+person.getEmail()+"</email>");
        out.println("<phone_number>"+person.getPhoneNumber()+"</phone_number>");
        out.println("<mobile_number>"+person.getMobileNumber()+"</mobile_number>");
        out.println("<address>"+person.getAddress() +"</address>");
        out.println("<post_code>"+person.getPostCode()+"</post_code>");
        out.println("<city>"+person.getCity()+"</city>");
        out.println("<province>"+person.getProvince()+"</province>");
        out.println("<country>"+person.getCountry() +"</country>");
        out.println("<birth_date>"+person.getBirthDate()+"</birth_date>");
        out.println("<comments>"+person.getComments()+"</comments>");
        out.println("<photo_file_name>"+person.getPhotoFileName()+"</photo_file_name>");
        out.println("</person>");
    }
%>
</persons>
