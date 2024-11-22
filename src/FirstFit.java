import java.util.List;

public class FirstFit implements EstrategiaAsignacion {
    @Override
    public boolean asignarTrabajo(Trabajo trabajo, List<Particion> memoria) {
        for (Particion particion : memoria) {
            if (particion.estaLibre() && particion.getTamanio() >= trabajo.getMemoriaRequerida()) {
                particion.asignarTrabajo(trabajo);

                //dividir la particion si sobra espacio
                int espacioSobrante = particion.getTamanio() - trabajo.getMemoriaRequerida();
                if (espacioSobrante > 0) {
                    int direccionNueva = particion.getDireccionInicio() + trabajo.getMemoriaRequerida();
                    Particion nuevaParticion = new Particion(direccionNueva, espacioSobrante);
                    memoria.add(memoria.indexOf(particion) + 1, nuevaParticion);
                    particion.setTamanio(trabajo.getMemoriaRequerida());
                }
                return true; //trabajo asignado con exito
            }
        }
        return false; //no se encontro una particion adecuada
    }
}

