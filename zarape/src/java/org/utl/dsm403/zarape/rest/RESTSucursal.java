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
import org.utl.dsm403.zarape.control.ControllerSucursal;
import org.utl.dsm403.zarape.modelo.Sucursal;
/**
 *
 * @author LA PLANTA
 */
@Path("sucursal")
public class RESTSucursal {
    @Path("getAllSucursales")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllSucursales() {
        String out;

        List<Sucursal> sucursales;
        ControllerSucursal cs = new ControllerSucursal();

        try {
            sucursales = cs.getAll();
            out = new Gson().toJson(sucursales);
        } catch (Exception e) {
            out = new Gson().toJson(new ErrorResponse("Ocurrió un error. Intente más tarde"));
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    // Clase para la respuesta de error
    private static class ErrorResponse {
        String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }
    
    @Path("agregar")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response agregar(@FormParam("datosSucursal") @DefaultValue("") String sucursalJson) throws SQLException {
        String out = null;
        Sucursal sucursal = null;
        ControllerSucursal ctrl = null;
        Gson gson = new Gson();

        try {
            sucursal = gson.fromJson(sucursalJson, Sucursal.class);
            ctrl = new ControllerSucursal();
            if (sucursal.getIdSucursal() < 1) {
                sucursal.setIdSucursal(ctrl.add(sucursal).getIdSucursal());
            } else if (sucursal.getIdSucursal() >= 1) {
                sucursal = ctrl.update(sucursal);
            }
            out = gson.toJson(sucursal);
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
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("eliminar")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response eliminar(@FormParam("idSucursal") @DefaultValue("0") int idSucursal) throws SQLException {
        String out = null;
        ControllerSucursal ctrl = null;

        try {
            ctrl = new ControllerSucursal();
            ctrl.delete(idSucursal);
            out = """
                  {"Resultado":"Sucursal %d eliminada"}
                  """;
            out = String.format(out, idSucursal);
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
        return Response.status(Response.Status.OK).entity(out).build();
    }
}