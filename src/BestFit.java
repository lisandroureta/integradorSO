import java.util.List;

public class BestFit implements EstrategiaAsignacion {
    @Override
    public boolean asignarTrabajo(Trabajo trabajo, List<Particion> memoria) {
        Particion mejorParticion = null;

        for (Particion particion : memoria) {
            if (particion.estaLibre() && particion.getTamanio() >= trabajo.getMemoriaRequerida()) {
                if (mejorParticion == null || particion.getTamanio() < mejorParticion.getTamanio()) {
                    mejorParticion = particion;
                }
            }
        }

        if (mejorParticion != null) {
            mejorParticion.asignarTrabajo(trabajo);

            //dividir la particion si sobra espacio
            int espacioSobrante = mejorParticion.getTamanio() - trabajo.getMemoriaRequerida();
            if (espacioSobrante > 0) {
                int direccionNueva = mejorParticion.getDireccionInicio() + trabajo.getMemoriaRequerida();
                Particion nuevaParticion = new Particion(direccionNueva, espacioSobrante);
                memoria.add(memoria.indexOf(mejorParticion) + 1, nuevaParticion);
                mejorParticion.setTamanio(trabajo.getMemoriaRequerida());
            }
            return true; //trabajo asignado con exito
        }

        return false; //no se encontro una particion adecuada
    }
}

