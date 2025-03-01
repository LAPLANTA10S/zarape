/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.dsm403.zarape.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.utl.dsm403.zarape.control.ControllerCategoria;
import org.utl.dsm403.zarape.control.ControllerCiudad;
import org.utl.dsm403.zarape.control.ControllerEstado;
import org.utl.dsm403.zarape.modelo.Categoria;
import org.utl.dsm403.zarape.modelo.Ciudad;
import org.utl.dsm403.zarape.modelo.Estado;

/**
 *
 * @author LAPLANTA
 */
@Path("zarape")
public class RESTZarape {

    @Path("getAllEstados")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllEstados() {
        String out;

        List<Estado> estados;
        ControllerEstado ce = new ControllerEstado();

        try {
            estados = ce.getAll();
            out = new Gson().toJson(estados);
        } catch (Exception e) {
            out = new Gson().toJson(new ErrorResponse("Ocurrió un error. Intente más tarde"));
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getAllCiudades")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllCiudades() {
        String out;

        List<Ciudad> ciudades;
        ControllerCiudad cc = new ControllerCiudad();

        try {
            ciudades = cc.getAll();
            out = new Gson().toJson(ciudades);
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
    
@Path("getAllCategorias")
@Produces(MediaType.APPLICATION_JSON)
@GET
public Response getAllCategorias() {
    String out;

    List<Categoria> categorias;  // Corregido de 'estados' a 'categorias'
    ControllerCategoria ccs = new ControllerCategoria();

    try {
        categorias = ccs.getAll();  // Se usa 'categorias' en lugar de 'estados'
        out = new Gson().toJson(categorias);
    } catch (Exception e) {
        out = new Gson().toJson(new ErrorResponse("Ocurrió un error. Intente más tarde"));
    }
    return Response.status(Response.Status.OK).entity(out).build();
}
}
