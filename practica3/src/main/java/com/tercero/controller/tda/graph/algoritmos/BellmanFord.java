package com.tercero.controller.tda.graph.algoritmos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tercero.controller.tda.graph.Adycencia;
import com.tercero.controller.tda.graph.GrapLabelNoDirect;
import com.tercero.controller.tda.list.LinkedList;

import java.util.Arrays;

public class BellmanFord {
    private GrapLabelNoDirect<String> grafo;
    private int origen;
    private float[] distancias;
    private int[] predecesores;

    public BellmanFord(GrapLabelNoDirect<String> grafo, int origen, int destino) {
        this.grafo = grafo;
        this.origen = origen;
        int n = grafo.nro_Ver();
        this.distancias = new float[n + 1];
        this.predecesores = new int[n + 1];
    }

    public String caminoCorto(int destino) throws Exception {
        int n = grafo.nro_Ver();

        // Inicialización
        Arrays.fill(distancias, Float.MAX_VALUE);
        distancias[origen] = 0;
        Arrays.fill(predecesores, -1);

        // Relajación de las aristas (n-1 veces)
        for (int i = 1; i < n; i++) {
            for (int u = 1; u <= n; u++) {
                LinkedList<Adycencia> adyacencias = grafo.adyacencias(u);
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adycencia adyacencia = adyacencias.get(j);
                    int v = adyacencia.getDestination();
                    float peso = adyacencia.getWeight();
                    if (distancias[u] != Float.MAX_VALUE && distancias[u] + peso < distancias[v]) {
                        distancias[v] = distancias[u] + peso;
                        predecesores[v] = u;

                        // DEBUG: Ver qué se está asignando
                        System.out.println("Predecesor de " + v + " es " + u);
                    }
                }
            }
        }

        // Verificación de ciclos negativos
        for (int u = 1; u <= n; u++) {
            LinkedList<Adycencia> adyacencias = grafo.adyacencias(u);
            for (int j = 0; j < adyacencias.getSize(); j++) {
                Adycencia adyacencia = adyacencias.get(j);
                int v = adyacencia.getDestination();
                float peso = adyacencia.getWeight();
                if (distancias[u] != Float.MAX_VALUE && distancias[u] + peso < distancias[v]) {
                    return "El grafo tiene un ciclo negativo";
                }
            }
        }

        // Reconstruir el camino
        return reconstruirCamino(origen, destino);
    }

    private String reconstruirCamino(int origen, int destino) throws Exception {
        if (distancias[destino] == Float.MAX_VALUE) {
            return "No hay camino";
        }

        StringBuilder camino = new StringBuilder();
        int actual = destino;

        // Recorrer los predecesores hasta llegar al origen
        while (actual != -1) {
            String nombre = grafo.getLabelL(actual);
            camino.insert(0, nombre + "(" + actual + ")");

            if (predecesores[actual] != -1) {
                camino.insert(0, " <- "); // Solo agregar " <- " si no es el origen
            }

            // Depuración: Ver cómo se actualizan los predecesores
            System.out.println("Nodo actual: " + actual + " (predecesor: " + predecesores[actual] + ")");
            actual = predecesores[actual];
        }

        System.out.println("en otra vida total: " + camino.toString());
        return camino.toString();

    }

    public JsonObject getMatrices() {
        JsonObject matrices = new JsonObject();

        // Guardar distancias
        JsonArray distanciasArray = new JsonArray();
        for (int i = 1; i < distancias.length; i++) {
            distanciasArray.add(distancias[i]);
        }
        matrices.add("distancias", distanciasArray);

        // Guardar predecesores
        JsonArray predecesoresArray = new JsonArray();
        for (int i = 1; i < predecesores.length; i++) {
            predecesoresArray.add(predecesores[i]);
        }
        matrices.add("predecesores", predecesoresArray);

        return matrices;
    }

}
