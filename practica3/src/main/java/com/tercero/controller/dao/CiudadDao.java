package com.tercero.controller.dao;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tercero.controller.dao.implement.AdapterDao;
import com.tercero.controller.tda.graph.Adycencia;
import com.tercero.controller.tda.graph.GrapLabelNoDirect;
import com.tercero.controller.tda.graph.algoritmos.BellmanFord;
import com.tercero.controller.tda.graph.algoritmos.Floyd;
import com.tercero.controller.tda.list.LinkedList;
import com.tercero.models.Ciudad;

public class CiudadDao extends AdapterDao<Ciudad> {
    private Ciudad ciudad;
    private LinkedList<Ciudad> listAll;
    private GrapLabelNoDirect<String> graph;
    private LinkedList<String> vertexName;
    private static final String GRAPH_PATH = "media/";
    private String graphFileName = "ciudadGrafo.json";

    public CiudadDao() {
        super(Ciudad.class);
    }

    
 
    public GrapLabelNoDirect<String> createGraph() {
        if (vertexName == null) {
            vertexName = new LinkedList<>();
        }
        if (graph == null) {
            initializeGraph();
        }
        return this.graph;
    }

    public void saveGraph() {
        if (graph == null) {
            System.err.println("Error: El grafo no esta incializado");
            return;
        }
        try {
            JsonArray jsonArray = new JsonArray();
            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                Integer vertexId = Integer.valueOf(i);
                JsonObject vertex = new JsonObject();

                // Información básica del vértice
                vertex.addProperty("id", vertexId);
                vertex.addProperty("nombre", (String) graph.getLabelL(vertexId));

                // Obtener y procesar las adyacencias
                LinkedList<Adycencia> adyacenciasList = graph.adyacencias(vertexId);
                JsonArray edges = new JsonArray();

                if (adyacenciasList != null && !adyacenciasList.isEmpty()) {
                    Object[] adyArray = adyacenciasList.toArray();
                    for (Object obj : adyArray) {
                        if (obj instanceof Adycencia) {
                            Adycencia ady = (Adycencia) obj;
                            JsonObject edge = new JsonObject();
                            edge.addProperty("destino", ady.getDestination());
                            edge.addProperty("peso", ady.getWeight());
                            edges.add(edge);
                        }
                    }
                }

                // Añadir el array de adyacencias al vértice
                vertex.add("adyacencias", edges);
                vertex.addProperty("grado", edges.size());

                // Añadir el vértice al array principal
                jsonArray.add(vertex);
            }

            // Crear el directorio si no existe
            Files.createDirectories(Paths.get("media/", new String[0]));

            // Convertir a JSON con formato legible
            String jsonOutput = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(jsonArray);

            // Guardar el archivo
            Files.write(
                    Paths.get("media/" + graphFileName),
                    jsonOutput.getBytes(),
                    new OpenOption[0]);

            System.out.println("Grafo guardado correctamente en: media/" + graphFileName);
            System.out.println("JSON generado: \n" + jsonOutput);

        } catch (Exception e) {
            System.err.println("Error al guardar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGraph() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("media/" + graphFileName)));
            JsonArray graphArray = JsonParser.parseString(content).getAsJsonArray();

            // Inicializar el grafo con el número correcto de vértices
            graph = new GrapLabelNoDirect(Integer.valueOf(graphArray.size()), String.class);

            // Primera pasada: establecer las etiquetas de los vértices
            for (JsonElement element : graphArray) {
                JsonObject vertex = element.getAsJsonObject();
                Integer id = vertex.get("id").getAsInt();
                String nombre = vertex.get("nombre").getAsString();
                graph.labelsVertices(id, nombre);
            }

            // Segunda pasada: establecer las adyacencias
            for (JsonElement element : graphArray) {
                JsonObject vertex = element.getAsJsonObject();
                Integer sourceId = vertex.get("id").getAsInt();

                JsonArray adyacencias = vertex.getAsJsonArray("adyacencias");
                if (adyacencias != null) {
                    for (JsonElement adyElement : adyacencias) {
                        JsonObject ady = adyElement.getAsJsonObject();
                        Integer destinoId = ady.get("destino").getAsInt();
                        Float peso = ady.get("peso").getAsFloat();

                        graph.add_edge(sourceId, destinoId, peso);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateRandomConnections() {
        try {
            Random random = new Random();
            int connections = random.nextInt(3) + 2; // Genera entre 2 y 4 conexiones por vértice

            // Para cada vértice
            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                int attemptedConnections = 0;
                int successfulConnections = 0;

                
                while (successfulConnections < connections && attemptedConnections < 10) {
                    Integer destino = random.nextInt(graph.nro_Ver().intValue()) + 1;

                    
                    if (!destino.equals(i) && !graph.is_Edge(i, destino).booleanValue()) {
                        float peso = random.nextFloat() * 10; // Peso entre 0 y 10
                        graph.add_edge(i, destino, peso);
                        System.out.println("Conexión generada entre: " + i + " y " + destino + " con peso: " + peso);
                        successfulConnections++;
                    }
                    attemptedConnections++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al generar conexiones aleatorias: " + e.getMessage());
        }
    }


    public void initializeGraph() {
        try {
            if (this.graph != null) {
                return; 
            }

            LinkedList<Ciudad> ciudad = this.listAll();
            if (!ciudad.isEmpty()) {
                this.graph = new GrapLabelNoDirect<>(ciudad.getSize(), String.class);
                Ciudad[] ciudadArray = new Ciudad[ ciudad.getSize()];
                for (int i = 0; i < ciudad.getSize(); i++) {
                    ciudadArray[i] = ciudad.get(i);
                }

                
                for (int i = 0; i < ciudadArray.length; i++) {
                    this.graph.labelsVertices(i + 1, ciudadArray[i].getNombre());
                }
            }
            generateRandomConnections();
        } catch (Exception e) {
            System.err.println("Error al inicializar el grafo: " + e.getMessage());
        }
    }

    public boolean existsGraphFile() {
        return Files.exists(Paths.get(GRAPH_PATH + graphFileName));
    }

    // Getters y setters para el grafo
    public GrapLabelNoDirect<String> getGraph() {
        return this.graph;
    }

    public void setGraphFileName(String fileName) {
        this.graphFileName = fileName;
    }

    public Ciudad getCiudad() {
        if (this.ciudad == null) {
            this.ciudad = new Ciudad();
        }
        return this.ciudad;
    }

    public void setCiudad(Ciudad usuario) {
        this.ciudad = usuario;
    }

    public LinkedList<Ciudad> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public Boolean save() throws Exception {
        Integer id = listAll().getSize() + 1;
        ciudad.setId(id);
        this.persist(this.ciudad);
        this.listAll = listAll();
        return true;
    }




    public String caminoCorto(int origen, int destino, int algoritmo) throws Exception {
        
        loadGraph();

        if (graph == null) {
            throw new Exception("Grafo no existe");
        }

        System.out.println("Calculando camino corto desde " + origen + " hasta " + destino);

        String camino = "";

        if (algoritmo == 1) {
            Floyd floydWarshall = new Floyd(graph, origen, destino);
            camino = floydWarshall.caminoCorto(); 

            
            saveMatricesToJson(floydWarshall.getMatrices(), "floyd_matrices.json");
        } else { 
            BellmanFord bellmanFord = new BellmanFord(graph, origen, destino);
            camino = bellmanFord.caminoCorto(algoritmo); 
                                                         

            // Guardar las matrices de adyacencia en un nuevo archivo .json
            saveMatricesToJson(bellmanFord.getMatrices(), "bellman_ford_matrices.json");
        }

        System.out.println("Camino corto calculado: " + camino);
        return camino; // Regresar el camino calculado
    }

    private void saveMatricesToJson(Object matrices, String fileName) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(matrices);

            // Crear el directorio si no existe
            Files.createDirectories(Paths.get("media/", new String[0]));

            // Guardar el archivo
            Files.write(
                    Paths.get("media/" + fileName),
                    jsonOutput.getBytes(),
                    new OpenOption[0]);

            System.out.println("Matrices guardadas correctamente en: media/" + fileName);
            System.out.println("JSON generado: \n" + jsonOutput);

        } catch (Exception e) {
            System.err.println("Error al guardar las matrices: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
