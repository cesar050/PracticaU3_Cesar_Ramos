package com.tercero.rest;

import com.tercero.controller.tda.list.LinkedList;
import com.tercero.controller.excepcion.ListEmptyException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/myresource")
public class Myresource {

    private static void ejecutarFloydWarshall(int[][] matrizAdyacencia) {
        int vertices = matrizAdyacencia.length;
        int[][] distancias = new int[vertices][vertices];

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                distancias[i][j] = matrizAdyacencia[i][j];
            }
        }

        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    if (distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                    }
                }
            }
        }
    }

    private static void ejecutarBellmanFord(LinkedList<int[]> listaAristas, int vertices, int origen)
            throws ListEmptyException {
        LinkedList<Integer> distancias = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            distancias.add(Integer.MAX_VALUE);
        }
        distancias.set(origen, 0);

        for (int i = 1; i < vertices; i++) {
            for (int j = 0; j < listaAristas.getSize(); j++) {
                int[] arista = listaAristas.get(j);
                int u = arista[0];
                int v = arista[1];
                int peso = arista[2];
                if (distancias.get(u) != Integer.MAX_VALUE && distancias.get(u) + peso < distancias.get(v)) {
                    distancias.set(v, distancias.get(u) + peso);
                }
            }
        }
    }

    private static String medirTiempoEjecucion(int[][] matrizAdyacencia, LinkedList<int[]> listaAristas, int vertices)
            throws ListEmptyException {
        long inicio, fin;
        inicio = System.nanoTime();
        ejecutarFloydWarshall(matrizAdyacencia);
        fin = System.nanoTime();
        long tiempoFloydWarshall = fin - inicio;

        // Medir tiempo de Bellman-Ford
        inicio = System.nanoTime();
        ejecutarBellmanFord(listaAristas, vertices, 0);
        fin = System.nanoTime();
        long tiempoBellmanFord = fin - inicio;

        return "Tiempo de Floyd-Warshall: " + tiempoFloydWarshall + " ns\n" +
                "Tiempo de Bellman-Ford: " + tiempoBellmanFord + " ns";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String obtenerRendimiento() throws ListEmptyException {
        int[][] grafoPequeño = generarGrafoAleatorio(10);
        int[][] grafoMediano = generarGrafoAleatorio(20);
        int[][] grafoGrande = generarGrafoAleatorio(30);

        LinkedList<int[]> aristasPequeñas = generarListaAristas(grafoPequeño);
        LinkedList<int[]> aristasMedianas = generarListaAristas(grafoMediano);
        LinkedList<int[]> aristasGrandes = generarListaAristas(grafoGrande);

        StringBuilder resultados = new StringBuilder();

        // Encabezado
        resultados.append("================================================================================\n");
        resultados.append("                          RESULTADOS DE TIEMPOS DE EJECUCIÓN                     \n");
        resultados.append("================================================================================\n\n");

        // Resultados para 10 vértices
        resultados.append(" Resultados para 10 vertices:\n");
        resultados.append(medirTiempoEjecucion(grafoPequeño, aristasPequeñas, 10)).append("\n");
        resultados.append("--------------------------------------------------------------------------------\n\n");

        // Resultados para 20 vértices
        resultados.append(" Resultados para 20 vertices:\n");
        resultados.append(medirTiempoEjecucion(grafoMediano, aristasMedianas, 20)).append("\n");
        resultados.append("--------------------------------------------------------------------------------\n\n");

        // Resultados para 30 vértices
        resultados.append("Resultados para 30 vertices:\n");
        resultados.append(medirTiempoEjecucion(grafoGrande, aristasGrandes, 30)).append("\n");
        resultados.append("================================================================================\n");

        // Devolver el resultado formateado
        return resultados.toString();

        
    }

    private static int[][] generarGrafoAleatorio(int tamaño) {
        int[][] grafo = new int[tamaño][tamaño];
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño; j++) {
                grafo[i][j] = (int) (Math.random() * 10);
            }
        }
        return grafo;
    }

    private static LinkedList<int[]> generarListaAristas(int[][] grafo) {
        LinkedList<int[]> listaAristas = new LinkedList<>();
        for (int i = 0; i < grafo.length; i++) {
            for (int j = 0; j < grafo[i].length; j++) {
                if (grafo[i][j] != 0) {
                    listaAristas.add(new int[] { i, j, grafo[i][j] });
                }
            }
        }
        return listaAristas;
    }
}