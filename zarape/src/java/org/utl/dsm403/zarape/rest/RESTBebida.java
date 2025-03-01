/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.dsm403.zarape.rest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import org.utl.dsm403.zarape.control.ControllerBebida;
import org.utl.dsm403.zarape.modelo.Bebida;

/**
 *
 * @author LA PLANTA
 */
@Path("bebida")
public class RESTBebida {
    @Path("getAllBebida")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllBebidas() {
        String out = null;
        List<Bebida> bebidas = null;
        ControllerBebida ctrl = new ControllerBebida();

        try {
            bebidas = ctrl.getAll(); // Llama al método del controlador para obtener la lista de bebidas
            out = new Gson().toJson(bebidas); // Convierte la lista a formato JSON
        } catch (Exception e) {
            out = "{\"error\":\"" + e.getLocalizedMessage() + "\"}";
        }

        return Response.status(Response.Status.OK).entity(out).build();
    }
 
    // Endpoint para agregar una nueva bebida
    @Path("agregar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregar(@FormParam("datosBebida") @DefaultValue("") String bebidaJson) throws SQLException {
        String out = null;
        Bebida bebida = null;
        ControllerBebida ctrl = new ControllerBebida();
        Gson gson = new Gson();
        
        try {
            // Convertimos el JSON recibido a un objeto Bebida
            bebida = gson.fromJson(bebidaJson, Bebida.class);
            ctrl = new ControllerBebida();
            
            // Agregamos la bebida al sistema
            bebida = ctrl.add(bebida);  // Usamos el método 'add' que ya tienes definido en el Controller

            // Convertimos el objeto bebida de vuelta a JSON
            out = gson.toJson(bebida);
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