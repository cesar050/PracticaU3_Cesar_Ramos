package com.tercero.rest;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.google.gson.Gson;
import com.tercero.controller.dao.CiudadDao;
import com.tercero.controller.dao.services.CiudadServices;
import com.tercero.controller.excepcion.ListEmptyException;
import com.tercero.controller.tda.graph.GrapLabelNoDirect;
import com.tercero.controller.tda.list.LinkedList;
import com.tercero.models.Ciudad;

@Path("/ciudad")
public class CiudadApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ListEmptyException, Exception {
        HashMap map = new HashMap<>();
        CiudadServices ps = new CiudadServices();
        map.put("msg", "OK");
        map.put("data", ps.listAll().toArray());
        if (ps.listAll().isEmpty()) {
            map.put("data", new Object[] {});
        }
        return Response.ok(map).build();
    }

    @Path("/save")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap map) {

        HashMap res = new HashMap<>();
        Gson g = new Gson();
        String a = g.toJson(map);
        System.out.println("Datos recibidos: " + a);

        try {
            CiudadServices ps = new CiudadServices();
            ps.getCiudad().setNombre(map.get("nombre").toString());
            ps.getCiudad().setLatitud(Double.parseDouble(map.get("latitud").toString()));
            ps.getCiudad().setLongitud(Double.parseDouble(map.get("longitud").toString()));

            ps.save();
            res.put("msg", "OK");
            res.put("data", "Ciudad registrada correctamente");
            return Response.ok(res).build();
        } catch (Exception e) {
            System.out.println("Error en sav data: " + e.toString());
            res.put("msg", "Error");
            res.put("data", e.toString());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    /*
     * *@PUT
     * 
     * @Path("/update")
     * 
     * @Consumes(MediaType.APPLICATION_JSON)
     * 
     * @Produces(MediaType.APPLICATION_JSON)
     * public Response update(HashMap map) {
     * HashMap res = new HashMap<>();
     * 
     * try {
     * CiudadServices ps = new CiudadServices();
     * ps.setCiudad(ps.get(Integer.parseInt(map.get("id").toString())));
     * if (ps.getCiudad().getId() == null) {
     * res.put("msg", "error");
     * res.put("data", "Error no existe la Ciudad");
     * return Response.status(Status.BAD_REQUEST).entity(res).build();
     * } else {
     * ps.getCiudad().setNombre(map.get("nombre").toString());
     * ps.getCiudad().setLatitud(Double.parseDouble(map.get("latitud").toString()));
     * ps.getCiudad().setLongitud(Double.parseDouble(map.get("longitud").toString())
     * );
     * ps.update();
     * res.put("status", "success");
     * res.put("message", "Ciudad editada con éxito.");
     * return Response.ok(res).build();
     * }
     * } catch (Exception e) {
     * res.put("status", "error");
     * res.put("message", "Error interno del servidor: " + e.getMessage());
     * return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
     * }
     * }
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/grafo")
    public Response grafo() {
        HashMap<String, Object> res = new HashMap<>();

        try {

            CiudadServices ciudadServices = new CiudadServices();
            LinkedList<Ciudad> lista = ciudadServices.listAll();
            GrapLabelNoDirect<String> grafo = ciudadServices.crearGrafo();
            ciudadServices.guardarGrafo();
            ciudadServices.generarConexionesAleatorias();
            res.put("estado", "Ok");
            res.put("msg", "Grafo creado y guardado con éxito.");
            res.put("data", lista.toArray());
            res.put("grafo", grafo);
            System.out.println("Ady" + grafo.toString());

            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("msg", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    /*
     * @Path("/camino_corto/{origen}/{destino}/{algoritmo}")
     * 
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_JSON)
     * public Response calcularCaminoCorto(
     * 
     * @PathParam("origen") int origen,
     * 
     * @PathParam("destino") int destino,
     * 
     * @PathParam("algoritmo") int algoritmo) {
     * 
     * // Respuesta JSON
     * HashMap<String, Object> response = new HashMap<>();
     * 
     * try {
     * // Validar parámetros
     * if (origen <= 0 || destino <= 0) {
     * throw new
     * IllegalArgumentException("Los valores de origen y destino deben ser mayores que 0."
     * );
     * }
     * if (algoritmo != 1 && algoritmo != 2) {
     * throw new
     * IllegalArgumentException("El algoritmo debe ser 1 (Floyd) o 2 (Bellman-Ford)."
     * );
     * }
     * 
     * CiudadDao ciudadDao = new CiudadDao();
     * 
     * String resultado = ciudadDao.caminoCorto(origen, destino, algoritmo);
     * 
     * response.put("status", "success");
     * response.put("message", "Camino corto calculado exitosamente");
     * response.put("resultado", resultado);
     * 
     * return Response.ok(response).build();
     * 
     * } catch (IllegalArgumentException e) {
     * response.put("status", "error");
     * response.put("message", "Parámetros inválidos: " + e.getMessage());
     * return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
     * 
     * } catch (Exception e) {
     * response.put("status", "error");
     * response.put("message", "Error al calcular el camino corto: " +
     * e.getMessage());
     * return
     * Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build
     * ();
     * }
     * }
     */
    @Path("/camino_corto/{origen}/{destino}/{algoritmo}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularCaminoCorto(
            @PathParam("origen") int origen,
            @PathParam("destino") int destino,
            @PathParam("algoritmo") int algoritmo) {

        // Respuesta JSON
        HashMap<String, Object> response = new HashMap<>();

        try {
            // Validar parámetros
            if (origen <= 0 || destino <= 0) {
                throw new IllegalArgumentException("Los valores de origen y destino deben ser mayores que 0.");
            }
            if (algoritmo != 1 && algoritmo != 2) {
                throw new IllegalArgumentException("El algoritmo debe ser 1 (Dijkstra) o 2 (Floyd).");
            }

            CiudadDao ciudadDao = new CiudadDao();

            // Aquí debes llamar al método que calcula el camino corto
            String resultado = ciudadDao.caminoCorto(origen, destino, algoritmo);

            response.put("status", "success");
            response.put("message", "Camino corto calculado exitosamente");
            response.put("resultado", resultado);

            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", "Parámetros inválidos: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al calcular el camino corto: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

}
