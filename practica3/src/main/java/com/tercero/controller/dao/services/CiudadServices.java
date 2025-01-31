package com.tercero.controller.dao.services;

import com.tercero.controller.dao.CiudadDao;
import com.tercero.controller.tda.graph.GrapLabelNoDirect;
import com.tercero.controller.tda.list.LinkedList;
import com.tercero.models.Ciudad;

public class CiudadServices {
    private CiudadDao obj;

    public CiudadServices() {
        obj = new CiudadDao();
    }

    public Ciudad getCiudad() {
        return obj.getCiudad();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public LinkedList<Ciudad> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setCiudad(Ciudad ciudad) {
        obj.setCiudad(ciudad);
    }

    public GrapLabelNoDirect<String> crearGrafo() {
        return obj.createGraph();
    }

    public void guardarGrafo() {
        obj.saveGraph();
    }

    public void cargarGrafo() {
        obj.loadGraph();
    }

    public void generarConexionesAleatorias() {
        obj.generateRandomConnections();
    }

    public void inicializarGrafo() {
        obj.initializeGraph();
    }

    public boolean existeArchivoGrafo() {
        return obj.existsGraphFile();
    }

    public GrapLabelNoDirect<String> obtenerGrafo() {
        return obj.getGraph();
    }

    public void setNombreArchivoGrafo(String nombreArchivo) {
        obj.setGraphFileName(nombreArchivo);
    }

    public String calcularCaminoCorto(int origen, int destino, int algoritmo) throws Exception {
        return obj.caminoCorto(origen, destino, algoritmo);
    }


}
