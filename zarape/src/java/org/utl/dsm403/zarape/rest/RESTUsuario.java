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
import org.utl.dsm403.zarape.control.ControllerUsuario;

/**
 *
 * @author vicky
 */
@Path("usuario")
public class RESTUsuario {

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("u") @DefaultValue("") String usuario, @FormParam("c") @DefaultValue("") String contrasenia) {
        String out;
        try {
            ControllerUsuario cu = new ControllerUsuario();
            out = cu.login(usuario, contrasenia);
        } catch (ClassNotFoundException | SQLException ex) {
            out = "{\"error\":\"" + ex.getLocalizedMessage() + "\"}";
        }
        return Response.ok(out).build();
    }
    
@Path("cheecky")
@Produces(MediaType.APPLICATION_JSON)
@GET
    public Response checkingUser(@QueryParam("nombreU")@DefaultValue("")String nombreU) {
        String out=null;
        String usuario=null;
        ControllerUsuario cu = new ControllerUsuario();
        try {
            usuario=cu.checkUsers(nombreU);
            out = new Gson().toJson(usuario);
        } catch (Exception e) {
            out = """
                  {"error":"Por ahí no joven"}
                  """;
            System.out.println(e.getMessage());
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
@Path("logout")
@Produces(MediaType.APPLICATION_JSON)
@GET
public Response logoutUser(@QueryParam("nombreU") @DefaultValue("") String nombreU) {
    String out;
    ControllerUsuario cu = new ControllerUsuario();
    try {
        boolean success = cu.logoutUser(nombreU);
        
        if (success) {
            out = """
                  {"message":"Sesión cerrada correctamente"}
                  """;
        } else {
            out = """
                  {"error":"Usuario no encontrado o sin sesión activa"}
                  """;
        }
    } catch (Exception e) {
        out = """
              {"error":"Error en el servidor"}
              """;
        System.out.println(e.getMessage());
    }
    
    return Response.status(Response.Status.OK).entity(out).build();
}

@Path("verificarToken")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response verificarToken(@QueryParam("nombreU") @DefaultValue("") String nombreU) {
    String out;
    ControllerUsuario cu = new ControllerUsuario();
    
    try {
        out = cu.verificarToken(nombreU);
    } catch (Exception e) {
        out = "{\"error\":\"Error en el servidor\"}";
        System.out.println(e.getMessage());
    }
    
    return Response.status(Response.Status.OK).entity(out).build();
}

}
