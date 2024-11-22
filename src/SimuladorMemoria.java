import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimuladorMemoria {
    private List<Particion> memoria;
    private EstrategiaAsignacion estrategia;
    private Queue<Trabajo> colaEspera;
    private List<TrabajoEnEjecucion> trabajosEnEjecucion;
    private FileWriter logWriter;
    private int tiempoActual;
    private int tiempoSeleccionParticion;
    private int tiempoCargaPromedio;
    private int tiempoLiberacion;
    private Map<String, Integer> tiemposRetornoProcesos = new HashMap<>(); //tiempo de retorno por proceso
    private int tiempoRetornoTanda = 0;
    private int fragmentacionExternaAcumulada = 0;
    private int memoriaLibreAnterior = 0; //variable que me ayuda a calcular el ife
    private boolean ifeFinalizado = false; //variable que me ayuda a calcular el ife

    public SimuladorMemoria(int tamanioMemoria, EstrategiaAsignacion estrategia, int tiempoSeleccionParticion, int tiempoCargaPromedio, int tiempoLiberacion, FileWriter logWriter) throws IOException {
        if (tamanioMemoria <= 0) {
            throw new IllegalArgumentException("El tamaño de la memoria debe ser mayor a 0.");
        }
        this.memoria = new ArrayList<>();
        this.estrategia = estrategia;
        this.tiempoSeleccionParticion = tiempoSeleccionParticion;
        this.tiempoCargaPromedio = tiempoCargaPromedio;
        this.tiempoLiberacion = tiempoLiberacion;
        this.colaEspera = new LinkedList<>();
        this.trabajosEnEjecucion = new ArrayList<>();
        this.memoria.add(new Particion(0, tamanioMemoria)); //se crea con una partición inicial
        this.logWriter = logWriter;
        this.tiempoActual = -1;
    }

    public void avanzarTiempo(List<Trabajo> trabajos) {
        tiempoActual++;
        registrarEvento("Avanzando al tiempo: " + tiempoActual);

        //acumular ife solo si no está finalizado
        if (!ifeFinalizado) {
            int memoriaLibre = calcularMemoriaLibre();
            fragmentacionExternaAcumulada += memoriaLibreAnterior * (tiempoActual - (tiempoActual - 1));
            memoriaLibreAnterior = memoriaLibre;
        }

        //liberar trabajos terminados
        Iterator<TrabajoEnEjecucion> it = trabajosEnEjecucion.iterator();
        while (it.hasNext()) {
            TrabajoEnEjecucion ejecucion = it.next();
            ejecucion.tiempoRestante--;
            if (ejecucion.tiempoRestante == 0) {
                liberarTrabajo(ejecucion.trabajo.getNombre());
                it.remove();
            }
        }

        //intentar asignar trabajos en la cola de espera
        if (!colaEspera.isEmpty()) {
            Trabajo trabajoEnEspera = colaEspera.peek();
            if (asignarTrabajo(trabajoEnEspera)) {
                colaEspera.poll();
            }
        }

        //solo si la cola de espera esta vacia, procesar trabajos nuevos
        for (Trabajo trabajo : trabajos) {
            if (trabajo.getInstanteArribo() == tiempoActual) {
                if (!colaEspera.isEmpty()) {
                    colaEspera.add(trabajo);
                    break;
                }
                if (!asignarTrabajo(trabajo)) {
                    colaEspera.add(trabajo);
                }
            }
        }

        //mostrar el estado de la memoria al final de cada tiempo
        registrarEstadoMemoria();
    }


    private void registrarEstadoMemoria() {
        registrarEvento("Estado actual de la memoria:");
        for (Particion particion : memoria) {
            registrarEvento(particion.toString());
        }
    }

    public void procesarLlegadaDeTrabajo(Trabajo trabajo) {
        if (!asignarTrabajo(trabajo)) {
            registrarEvento("Trabajo " + trabajo.getNombre() + " encolado.");
            colaEspera.add(trabajo);
        }
    }

    public boolean asignarTrabajo(Trabajo trabajo) {
        boolean asignado = estrategia.asignarTrabajo(trabajo, memoria);
        if (asignado) {
            //calcular la duración total incluyendo tiempos adicionales
            int tiempoTotal = trabajo.getDuracion() + tiempoSeleccionParticion + tiempoCargaPromedio + tiempoLiberacion;
            registrarEvento("Trabajo " + trabajo.getNombre() + " asignado. Duración total ajustada a " + tiempoTotal + " unidades de tiempo.");

            trabajosEnEjecucion.add(new TrabajoEnEjecucion(trabajo, tiempoTotal));

            //verificar si este es el ultimo proceso
            if (!ifeFinalizado && trabajo.getNombre().equals(obtenerUltimoTrabajoNombre())) {
                ifeFinalizado = true; //detener el calculo del ife
                registrarEvento("IFE detenido. Último trabajo asignado: " + trabajo.getNombre());
            }
            return true;
        }
        registrarEvento("No se pudo asignar el trabajo " + trabajo.getNombre() + " en este momento.");
        return false;
    }
    private String obtenerUltimoTrabajoNombre() {
        return colaEspera.isEmpty() ? "" : colaEspera.peek().getNombre();
    }
    public void liberarTrabajo(String nombreTrabajo) {
        for (Particion particion : memoria) {
            if (!particion.estaLibre() && particion.getTrabajoAsignado().getNombre().equals(nombreTrabajo)) {
                particion.liberar();
                registrarEvento("Trabajo " + nombreTrabajo + " liberado.");

                //tiempo de retorno del proceso
                tiemposRetornoProcesos.put(nombreTrabajo, tiempoActual);

                //compactar memoria
                compactarParticionesLibres();

                //verificar si todas las particiones estan libres
                if (todasLasParticionesLibres()) {
                    tiempoRetornoTanda = tiempoActual; // Actualizar el tiempo de retorno de la tanda
                    registrarEvento("Todas las particiones están libres. Tiempo de retorno de la tanda: " + tiempoRetornoTanda);
                }
                return;
            }
        }
        registrarEvento("Trabajo " + nombreTrabajo + " no encontrado para liberación.");
    }
    private boolean todasLasParticionesLibres() {
        for (Particion particion : memoria) {
            if (!particion.estaLibre()) {
                return false;
            }
        }
        return true;
    }
    private void compactarParticionesLibres() {
        for (int i = 0; i < memoria.size() - 1; i++) {
            Particion actual = memoria.get(i);
            Particion siguiente = memoria.get(i + 1);

            if (actual.estaLibre() && siguiente.estaLibre()) {
                //combinar particiones
                int nuevoTamanio = actual.getTamanio() + siguiente.getTamanio();
                actual.setTamanio(nuevoTamanio);
                memoria.remove(i + 1); //eliminar la partición siguiente
                i--; //volver a verificar desde la partición combinada
            }
        }
    }

    private void registrarEvento(String evento) {
        try {
            System.out.println(evento); //mostrar evento en la terminal
            logWriter.write(evento + "\n"); //mostrar evento en el log
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Error al registrar el evento en el log.");
        }
    }


    public void cerrarLog() {
        try {
            logWriter.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el archivo de log.");
        }
    }
    public void calcularIndicadores() {
        registrarEvento("--- Cálculo de indicadores finales ---");

        //tiempo medio de retorno
        int sumaTiemposRetorno = tiemposRetornoProcesos.values().stream().mapToInt(Integer::intValue).sum();
        double tiempoMedioRetorno = (double) sumaTiemposRetorno / tiemposRetornoProcesos.size();

        for (Map.Entry<String, Integer> entry : tiemposRetornoProcesos.entrySet()) {
            registrarEvento("Trabajo " + entry.getKey() + ", Tiempo de Retorno: " + entry.getValue());
        }
        registrarEvento("Tiempo Medio de Retorno: " + tiempoMedioRetorno);
        registrarEvento("Tiempo de Retorno de la Tanda: " + tiempoRetornoTanda);
        registrarEvento("Índice de Fragmentación Externa (IFE): " + fragmentacionExternaAcumulada);


    }

    private int calcularMemoriaLibre() {
        int totalLibre = 0;
        for (Particion particion : memoria) {
            if (particion.estaLibre()) {
                totalLibre += particion.getTamanio();
            }
        }
        return totalLibre;
    }

    public static void main(String[] args) {
        try (FileWriter logWriter = new FileWriter("log_simulacion.txt", true)) {

            //lectura del archivo y carga de trabajos
            File archivo = new File("trabajitos.txt");
            if (!archivo.exists()) {
                System.out.println("El archivo de trabajos no se encuentra en la ruta especificada.");
                return;
            }

            LectorArchivo lector = new LectorArchivo();
            List<Trabajo> trabajos = lector.leerTrabajosDesdeArchivo(archivo.getPath());

            if (trabajos.isEmpty()) {
                System.out.println("No se pudieron cargar los trabajos. Verifica el archivo.");
                return;
            }

            //configuración de la memoria
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese el tamaño total de la memoria física disponible:");
            int tamanioMemoria = scanner.nextInt();

            //tiempo de seleccion de particion
            System.out.println("Ingrese el tiempo de selección de partición (en milisegundos):");
            int tiempoSeleccionParticion = scanner.nextInt();

            //tiempo de carga promedio
            System.out.println("Ingrese el tiempo de carga promedio (en milisegundos):");
            int tiempoCargaPromedio = scanner.nextInt();

            //tiempo de liberacion de particion
            System.out.println("Ingrese el tiempo de liberación de partición (en milisegundos):");
            int tiempoLiberacion = scanner.nextInt();

            logWriter.write("=== Inicio de la simulación ===\n");
            logWriter.write("Estrategia 1: First-fit\n");
            logWriter.write("Estrategia 2: Best-fit\n");
            logWriter.write("Estrategia 3: Next-fit\n");
            logWriter.write("Estrategia 4: Worst-fit\n");

            for (int estrategiaSeleccionada = 1; estrategiaSeleccionada <= 4; estrategiaSeleccionada++) {
                String mensajeEstrategia = "\n=== Ejecutando estrategia " + estrategiaSeleccionada + " ===";
                System.out.println(mensajeEstrategia);
                logWriter.write(mensajeEstrategia + "\n"); //registrar tambien en el log

                //seleccion de estrategia
                EstrategiaAsignacion estrategia;
                switch (estrategiaSeleccionada) {
                    case 1:
                        estrategia = new FirstFit();
                        break;
                    case 2:
                        estrategia = new BestFit();
                        break;
                    case 3:
                        estrategia = new NextFit();
                        break;
                    case 4:
                        estrategia = new WorstFit();
                        break;
                    default:
                        throw new IllegalArgumentException("Estrategia desconocida.");
                }

                //crear una nueva instancia de SimuladorMemoria para cada estrategia
                SimuladorMemoria simulador = new SimuladorMemoria(tamanioMemoria, estrategia, tiempoSeleccionParticion, tiempoCargaPromedio, tiempoLiberacion, logWriter);

                //simulacion de 30 tiempos para que de tiempo de sobra a que terminen todos los trabajos
                for (int tiempo = 0; tiempo < 30; tiempo++) {
                    simulador.avanzarTiempo(trabajos);
                }

                simulador.calcularIndicadores();

            }

            logWriter.write("=== Fin de la simulación ===\n");

        } catch (IOException e) {
            System.err.println("Error al inicializar el simulador: " + e.getMessage());
        }
    }
}

class TrabajoEnEjecucion {
    Trabajo trabajo;
    int tiempoRestante;

    public TrabajoEnEjecucion(Trabajo trabajo, int tiempoRestante) {
        this.trabajo = trabajo;
        this.tiempoRestante = tiempoRestante;
    }
}



