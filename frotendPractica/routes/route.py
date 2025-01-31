from flask import Blueprint, render_template, jsonify, redirect, url_for
import requests

router = Blueprint('router', __name__)

@router.route('/')
def home():
    return redirect(url_for('router.mapagrafos'))

@router.route('/mapagrafos')
def mapagrafos():
    try:
        r = requests.get("http://localhost:8080/myapp/ciudad/grafo")
        if r.status_code == 200:
            response_data = r.json()
            
            ciudades = response_data.get("data", [])
            
            if not ciudades:
                return jsonify({"error": "No se encontraron ciudades en la respuesta"}), 404
            
            nodos = []
            aristas = set()  # Usamos un set para evitar duplicados
            
            # Crear nodos
            for ciudad in ciudades:
                nodos.append({
                    "id": ciudad["id"],
                    "label": f"{ciudad['nombre']} ({ciudad['id']})",
                    "color": "#00FF00",  # Verde brillante como en la imagen
                    "title": f"Grado: {ciudad.get('grado', 0)}"
                })
                
                # Procesar adyacencias
                for adyacencia in ciudad.get("adyacencias", []):
                    # Creamos un identificador único para cada arista
                    edge_id = tuple(sorted([ciudad["id"], adyacencia["destino"]]))
                    aristas.add((
                        edge_id,
                        {
                            "from": ciudad["id"],
                            "to": adyacencia["destino"],
                            "label": f"{adyacencia['peso']:.2f}",
                            "width": 2,
                            "color": {
                                 "color": "#000000",
                                 "highlight": "#1c1c1c",
                                 "hover": "#333333"
                            }
                        }
                    ))
            
            # Convertir el set de aristas a lista de diccionarios
            aristas_lista = [arista[1] for arista in aristas]
            
            graph_data = {
                "nodos": nodos,
                "aristas": aristas_lista
            }
            
            return render_template('inicio.html', graph_data=graph_data)
        
        return jsonify({"error": "No se pudo obtener el grafo"}), r.status_code
        
    except requests.exceptions.RequestException as e:
        print("Error de conexión:", str(e))
        return jsonify({"error": f"No se pudo conectar con el servidor: {str(e)}"}), 502
    

@router.route('/camino_corto/<int:origen>/<int:destino>/<int:algoritmo>', methods=['GET'])
def camino_corto(origen, destino, algoritmo):
    try:
        # Realizar la consulta al servicio Java para calcular el camino corto
        url = f"http://localhost:8080/myapp/camino_corto/{origen}/{destino}/{algoritmo}"
        response = requests.get(url)

        # Verificar si la respuesta del backend es exitosa
        if response.status_code == 200:
            # Convertir la respuesta JSON del backend Java y devolverla
            data = response.json()
            return jsonify(data)
        else:
            # Si el backend no responde con un 200, devolver un mensaje de error
            return jsonify({"error": "No se pudo calcular el camino corto"}), 400
    except requests.exceptions.RequestException as e:
        # Manejar errores de la red o del servicio
        return jsonify({"error": f"Error de conexión: {str(e)}"}), 502
