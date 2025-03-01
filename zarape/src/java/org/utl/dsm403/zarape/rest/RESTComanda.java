/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.dsm403.zarape.rest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import org.utl.dsm403.zarape.control.ControllerComanda;
import org.utl.dsm403.zarape.modelo.DetalleTicket;
import org.utl.dsm403.zarape.modelo.Ticket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.utl.dsm403.zarape.modelo.Comanda;

@Path("comanda")
public class RESTComanda {

    @Path("agregarTicket")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregar(@FormParam("datosTicket") @DefaultValue("") String ticketJson) throws SQLException {
        String out;
        Ticket ticket;
        ControllerComanda ctrl = new ControllerComanda();
        Gson gson = new Gson();

        try {
            // Convertimos el JSON recibido a un objeto Ticket
            ticket = gson.fromJson(ticketJson, Ticket.class);

            // Agregamos el ticket al sistema
            ticket = ctrl.addTicket(ticket);  // Usamos el método 'addTicket' del Controller

            // Convertimos el objeto ticket de vuelta a JSON
            out = gson.toJson(ticket);
        } catch (JsonParseException jpe) {
            out = """
                  {"error":"Formato de datos no válido"}
                  """;
            jpe.printStackTrace();
        } catch (Exception ex) {
            out = "{\"error\":\"" + ex.getLocalizedMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("actualizarTicket")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@FormParam("datosTicket") @DefaultValue("") String ticketJson) throws SQLException {
        String out;
        Ticket ticket;
        ControllerComanda ctrl = new ControllerComanda();
        Gson gson = new Gson();

        try {
            // Convertimos el JSON recibido a un objeto Ticket
            ticket = gson.fromJson(ticketJson, Ticket.class);

            // Agregamos el ticket al sistema
            ticket = ctrl.updateTicket(ticket);  // Usamos el método 'updateTicket' del Controller

            // Convertimos el objeto ticket de vuelta a JSON
            out = gson.toJson(ticket);
        } catch (JsonParseException jpe) {
            out = """
                  {"error":"Formato de datos no válido"}
                  """;
            jpe.printStackTrace();
        } catch (Exception ex) {
            out = "{\"error\":\"" + ex.getLocalizedMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("agregarDetalleTicket")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarDetalle(@FormParam("datosDetalle") @DefaultValue("") String detalleJson) throws SQLException {
        String out;
        DetalleTicket detalle;
        ControllerComanda ctrl = new ControllerComanda();
        Gson gson = new Gson();

        try {
            // Convertimos el JSON recibido a un objeto DetalleTicket
            detalle = gson.fromJson(detalleJson, DetalleTicket.class);

            // Agregamos el detalle del ticket al sistema
            ctrl.addDetalleTicket(detalle);  // Llamamos al método addDetalleTicket

            // Convertimos el objeto detalle de vuelta a JSON
            out = gson.toJson(detalle);
        } catch (JsonParseException jpe) {
            out = """
                  {"error":"Formato de datos no válido"}
                  """;
            jpe.printStackTrace();
        } catch (Exception ex) {
            out = "{\"error\":\"" + ex.getLocalizedMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }   
    
    @Path("agregarComanda")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarComanda(@FormParam("datosComanda") @DefaultValue("") String comandaJson) throws SQLException {
        String out;
        Comanda comanda;
        ControllerComanda ctrl = new ControllerComanda();
        Gson gson = new Gson();

        try {
            // Convertimos el JSON recibido a un objeto Comanda
            comanda = gson.fromJson(comandaJson, Comanda.class);

            // Agregamos la comanda al sistema
            comanda = ctrl.addComanda(comanda);  // Usamos el método 'addComanda' del Controller

            // Convertimos el objeto comanda de vuelta a JSON
            out = gson.toJson(comanda);
        } catch (JsonParseException jpe) {
            out = """
                  {"error":"Formato de datos no válido"}
                  """;
            jpe.printStackTrace();
        } catch (Exception ex) {
            out = "{\"error\":\"" + ex.getLocalizedMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

}

