package com.tercero.controller.tda.graph;

import com.tercero.controller.tda.list.LinkedList;


public abstract class Graph {

    public abstract Integer nro_Ver();
    public abstract Integer nro_Edges();
    public abstract Boolean is_Edge(Integer v1, Integer v2) throws Exception;
    public abstract Float weight_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2, Float weight) throws Exception;
    public abstract LinkedList<Adycencia> adyacencias(Integer v1);

    @Override

    public String toString() {
        String grafo = "";
        try {
            for (int i = 1; i <= this.nro_Ver(); i++) {
                grafo += "V" + i + "\n";
                LinkedList<Adycencia> lista = adyacencias(i);
                if (!lista.isEmpty()) {
                    Adycencia[] ady = lista.toArray();
                    for (int j = 0; j < ady.length; j++) {
                        Adycencia a = ady[j];
                        grafo += "ady " + "V" + a.getDestination() + " weight:" + a.getWeight() + "\n";
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error Graph:" + e);
        }

        return grafo;
    }
    
}
