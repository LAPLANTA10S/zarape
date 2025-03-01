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
import org.utl.dsm403.zarape.control.ControllerCliente;
import org.utl.dsm403.zarape.modelo.Cliente;
/**
 *
 * @author LA PLANTA
 */
@Path("cliente")
public class RESTCliente {
@Path("getAllClientes")
@Produces(MediaType.APPLICATION_JSON)
@GET
public Response getAllClientes() {
    String out=null;
    List<Cliente> clientes = null;
    ControllerCliente ctrl = new ControllerCliente();
    try {
        clientes = ctrl.getAll(); // Llama al método del controlador para obtener la lista de empleados
        out = new Gson().toJson(clientes); // Convierte la lista a formato JSON
    } catch (Exception e) {
        out="""
            {"error":"Ocurrio un error. Intente mas tarde"}
            """;
//            out = new Gson().toJson(new ErrorResponse("Ocurrió un error. Intente más tarde"));
    }
    return Response.status(Response.Status.OK).entity(out).build();
}

@Path("agregar")
@Produces(MediaType.APPLICATION_JSON)
@POST
public Response agregar(@FormParam("datosCliente") @DefaultValue("") String cliente) throws SQLException {
    String out = null;
    Cliente c = null;
    ControllerCliente ctrl = null;
    Gson gson = new Gson();
    
    try {
        // Convertimos el JSON recibido a un objeto Cliente
        c = gson.fromJson(cliente, Cliente.class);
        ctrl = new ControllerCliente();
        
        // Si el idCliente es menor que 1, significa que es un cliente nuevo
        if (c.getIdCliente() < 1) {
            c.setIdCliente(ctrl.add(c).getIdCliente());  // Agregar cliente al sistema
        }
        // Si ya tiene id, se realiza una actualización
        else if (c.getIdCliente() >= 1){
            c = ctrl.update(c);
        }
        
        // Convertimos el objeto cliente de vuelta a JSON
        out = gson.toJson(c);
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
public Response eliminar(@FormParam("idCliente") @DefaultValue("0") int idCliente) throws SQLException{
    String out = null;
    ControllerCliente ctrl = null;
    try{
        ctrl = new ControllerCliente();
        ctrl.delete(idCliente);
        out = """
               {"Resultado":"empleado %d eliminado"}
               """;
        out=String.format(out,idCliente);
        } catch (JsonParseException jpe) {
            out = """
                  {"error":"Formato de datos no valido"}
                  """;
            jpe.printStackTrace();
        } catch (Exception ex) {
            out = """
                  {"error":"Error interno del servidor. Intente mas tarde"}
                  """;
            ex.printStackTrace();
        }
        return Response.status(Response.Status.OK).entity(out).build();
}

}
