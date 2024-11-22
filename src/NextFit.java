import java.util.List;

public class NextFit implements EstrategiaAsignacion {
    private int ultimaPosicion = 0; //posicion donde termino la ultima asignacion

    @Override
    public boolean asignarTrabajo(Trabajo trabajo, List<Particion> memoria) {
        int n = memoria.size();
        int inicio = ultimaPosicion;

        do {
            Particion particion = memoria.get(ultimaPosicion);
            if (particion.estaLibre() && particion.getTamanio() >= trabajo.getMemoriaRequerida()) {
                particion.asignarTrabajo(trabajo);

                //dividir la particion si sobra espacio
                int espacioSobrante = particion.getTamanio() - trabajo.getMemoriaRequerida();
                if (espacioSobrante > 0) {
                    int direccionNueva = particion.getDireccionInicio() + trabajo.getMemoriaRequerida();
                    Particion nuevaParticion = new Particion(direccionNueva, espacioSobrante);
                    memoria.add(ultimaPosicion + 1, nuevaParticion);
                    particion.setTamanio(trabajo.getMemoriaRequerida());
                }
                ultimaPosicion = (ultimaPosicion + 1) % n; //actualizar la ultima posicion
                return true; //trabajo asignado con exito
            }
            ultimaPosicion = (ultimaPosicion + 1) % n; //avanzar circularmente
        } while (ultimaPosicion != inicio);

        return false; //no se encontro una particion adecuada
    }
}
