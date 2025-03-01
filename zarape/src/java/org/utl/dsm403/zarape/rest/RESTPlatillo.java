package org.utl.dsm403.zarape.rest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.utl.dsm403.zarape.control.ControllerPlatillo;
import org.utl.dsm403.zarape.modelo.Alimento;
import org.utl.dsm403.zarape.modelo.Producto;

/**
 *
 * @author vicky
 */
@Path("platillo")
public class RESTPlatillo {
    @Path("getAllAlimentos")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllAlimentos() {
        String out = null;
        List<Alimento> alimentos = null;
        ControllerPlatillo ctrl = new ControllerPlatillo();

        try {
            alimentos = ctrl.getAll(); // Llama al método del controlador para obtener la lista de alimentos
            out = new Gson().toJson(alimentos); // Convierte la lista a formato JSON
        } catch (Exception e) {
             out = "{\"error\":\"" + e.getLocalizedMessage() + "\"}";
        }

        return Response.status(Response.Status.OK).entity(out).build();
    }
 
    // Endpoint para agregar un nuevo platillo
    @Path("agregar")
    @POST
    public Response agregar(@FormParam("datosPlatillo") @DefaultValue("") String platilloJson) throws SQLException {
        String out = null;
        Alimento alimento = null;
        ControllerPlatillo ctrl = null;
        Gson gson = new Gson();
        
        try {
            // Convertimos el JSON recibido a un objeto Alimento
            alimento = gson.fromJson(platilloJson, Alimento.class);
            ctrl = new ControllerPlatillo();
            
            // Agregamos el platillo al sistema
            alimento = ctrl.add(alimento);  // Usamos el método 'add' que ya tienes definido en el Controller

            // Convertimos el objeto alimento de vuelta a JSON
            out = gson.toJson(alimento);
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

    
@Path("eliminar")
@Produces(MediaType.APPLICATION_JSON)
@POST
public Response eliminar(@FormParam("idAlimento") @DefaultValue("0") int idAlimento) throws SQLException {
    String out = null;
    ControllerPlatillo ctrl = null;

    try {
        ctrl = new ControllerPlatillo();
        // Llamamos al método de eliminación
        ctrl.delete(idAlimento);
        out = """
              {"Resultado":"Alimento %d eliminado"}
              """;
        out = String.format(out, idAlimento);
    } catch (JsonParseException jpe) {
        out = """
              {"error":"Formato de datos no válido"}
              """;
        jpe.printStackTrace();
    } catch (Exception ex) {
        out = """
              {"error":"Error interno del servidor. Intente más tarde"}
              """;
        ex.printStackTrace();
    }

    // Retornamos la respuesta con el resultado de la eliminación
    return Response.status(Response.Status.OK).entity(out).build();
} 

}