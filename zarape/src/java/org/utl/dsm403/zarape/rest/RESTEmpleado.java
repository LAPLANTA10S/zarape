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
import org.utl.dsm403.zarape.control.ControllerEmpleado;
import org.utl.dsm403.zarape.modelo.Empleado;

/**
 *
 * @author LA PLANTA
 */
@Path("empleado")
public class RESTEmpleado {
    @Path("getAllEmpleados")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllEmpleados() {
        String out = null;
        List<Empleado> empleados = null;
        ControllerEmpleado ctrl = new ControllerEmpleado();

        try {
            // Llamamos a la función verificarToken y validamos la respuesta usando el primer nombre de usuario en la base de datos
            String nombreU = ctrl.obtenerUsuarioConToken();  // Método que obtiene el primer nombre de usuario de la base de datos
            if (nombreU == null || nombreU.isEmpty()) {
                out = "{\"error\":\"Acceso Denegado\"}";
                return Response.status(Response.Status.NOT_FOUND).entity(out).build();
            }

            String tokenResponse = ctrl.validarToken(nombreU); // Usamos el nombre de usuario obtenido
            if (tokenResponse.contains("{\"tokenValido\":false}")) {
                out = "{\"error\":\"Token no válido. Autenticación requerida\"}";
            } else {
                empleados = ctrl.getAll(); // Llama al método del controlador para obtener la lista de empleados
                out = new Gson().toJson(empleados); // Convierte la lista a formato JSON
            }
        } catch (Exception e) {
            out = "{\"error\":\"Ocurrió un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
 
@Path("agregar")
@Produces(MediaType.APPLICATION_JSON)
@POST
public Response agregar(@FormParam("datosEmpleado") @DefaultValue("") String empleado) throws SQLException {
    String out = null;
    Empleado e = null;
    ControllerEmpleado ctrl = new ControllerEmpleado(); // Inicialización del controlador
    Gson gson = new Gson();

    try {
        // Validación del token
        String nombreU = ctrl.obtenerUsuarioConToken();  // Método que obtiene el primer nombre de usuario de la base de datos
        if (nombreU == null || nombreU.isEmpty()) {
            out = "{\"error\":\"Acceso Denegado\"}";
            return Response.status(Response.Status.NOT_FOUND).entity(out).build();
        }

        String tokenResponse = ctrl.validarToken(nombreU); // Usamos el nombre de usuario obtenido
        if (tokenResponse.contains("{\"tokenValido\":false}")) {
            out = "{\"error\":\"Token no válido. Autenticación requerida\"}";
            return Response.status(Response.Status.UNAUTHORIZED).entity(out).build();
        }

        // Procesamiento del empleado
        e = gson.fromJson(empleado, Empleado.class);
        if (e.getIdEmpleado() < 1) {
            e.setIdEmpleado(ctrl.add(e).getIdEmpleado());
        } else if (e.getIdEmpleado() >= 1) {
            e = ctrl.update(e);
        }
        out = gson.toJson(e);
    } catch (JsonParseException jpe) {
        out = "{\"error\":\"Formato de datos no valido\"}";
        jpe.printStackTrace();
    } catch (Exception ex) {
        out = "{\"error\":\"Error interno del servidor. Intente más tarde\"}";
        ex.printStackTrace();
    }
    return Response.status(Response.Status.OK).entity(out).build();
}
    
@Path("eliminar")
@Produces(MediaType.APPLICATION_JSON)
@POST
public Response eliminar(@FormParam("idEmpleado") @DefaultValue("0") int idEmpleado) throws SQLException {
    String out = null;
    ControllerEmpleado ctrl = new ControllerEmpleado(); // Inicialización del controlador
    try {
        // Validación del token antes de procesar la solicitud
        String nombreU = ctrl.obtenerUsuarioConToken(); // Obtiene el nombre del usuario
        if (nombreU == null || nombreU.isEmpty()) {
            out = "{\"error\":\"Acceso Denegado\"}";
            return Response.status(Response.Status.NOT_FOUND).entity(out).build();
        }

        String tokenResponse = ctrl.validarToken(nombreU); // Validación del token
        if (tokenResponse.contains("{\"tokenValido\":false}")) {
            out = "{\"error\":\"Token no válido. Autenticación requerida\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(out).build(); // Se usa 403 para autenticación fallida
        }

        // Procedemos con la eliminación del empleado
        ctrl.delete(idEmpleado);
        out = String.format("{\"Resultado\":\"Empleado %d eliminado\"}", idEmpleado);

    } catch (JsonParseException jpe) {
        out = "{\"error\":\"Formato de datos no válido\"}";
        jpe.printStackTrace();
    } catch (Exception ex) {
        out = "{\"error\":\"Error interno del servidor. Intente más tarde\"}";
        ex.printStackTrace();
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
}
