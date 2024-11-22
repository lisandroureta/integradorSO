# Simulador de memoria con estrategias de asignación

Este proyecto es un simulador de memoria que implementa estrategias de asignación de particiones dinámicas, incluyendo:
- **First-Fit**
- **Best-Fit**
- **Next-Fit**
- **Worst-Fit**

El programa lee un archivo de texto con una lista de trabajos y simula el comportamiento de las estrategias, calculando indicadores como:
- Tiempo de Retorno por proceso.
- Tiempo Medio de Retorno.
- Índice de Fragmentación Externa.

## Requisitos previos

Tener instalado lo siguiente:
- **Java Development Kit (JDK)** (el programa fue creado en la versión 8).
- **Línea de comandos** (Terminal, CMD o PowerShell).

## Estructura del proyecto

La carpeta del proyecto contiene las siguientes subcarpetas y archivos:
- **src/**: Contiene los archivos `.java` del código fuente.
- **trabajitos.txt**: Archivo que define la tanda de trabajos. (La tanda fue extraída y modificada del Trabajo Práctico N°4, punto 8)
- **log_simulacion.txt**: Archivo que se generará durante la ejecución del programa para registrar eventos e indicadores.

## Cómo compilar y ejecutar desde la línea de comandos

1. **Navegar a la carpeta del proyecto**:
   ```bash
   cd ruta/al/proyecto

2. **Compilar los archivos Java**:
   javac -d bin src/*.java

3. **Ejecutar el programa**:
   java -cp bin SimuladorMemoria

## Resultados
-Durante la ejecución, se solicitarán algunos parámetros como el tamaño de la memoria y los tiempos. Se recomienda ingresar los siguientes datos para poder corroborar los resultados con los Diagramas de Gantt del archivo Excel:
Tamaño de memoria física disponible: 130
Tiempo de selección de partición: 1
Tiempo de carga promedio: 1
Tiempo de liberación de partición: 1

-Al finalizar, revisar el archivo log_simulacion.txt para ver los resultados.
-Los trabajos se alocan en particiones diferentes según la estrategia seleccionado, pero los indicadores finales coinciden en los 4 algorítmos debido a la naturaleza de la tanda seleccionada. Revisar archivo de Excel para mejor entendimiento.

## Notas importantes
-No mover ni eliminar el archivo trabajitos.txt, ya que el programa lo utiliza para cargar la lista de trabajos.
-Si se desea reiniciar los resultados del log, simplemente eliminar el archivo log_simulacion.txt antes de ejecutar el programa.