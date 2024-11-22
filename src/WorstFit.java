import java.util.List;

public class WorstFit implements EstrategiaAsignacion {
    @Override
    public boolean asignarTrabajo(Trabajo trabajo, List<Particion> memoria) {
        Particion peorParticion = null;

        for (Particion particion : memoria) {
            if (particion.estaLibre() && particion.getTamanio() >= trabajo.getMemoriaRequerida()) {
                if (peorParticion == null || particion.getTamanio() > peorParticion.getTamanio()) {
                    peorParticion = particion;
                }
            }
        }

        if (peorParticion != null) {
            peorParticion.asignarTrabajo(trabajo);

            //dividir la particion si sobra espacio
            int espacioSobrante = peorParticion.getTamanio() - trabajo.getMemoriaRequerida();
            if (espacioSobrante > 0) {
                int direccionNueva = peorParticion.getDireccionInicio() + trabajo.getMemoriaRequerida();
                Particion nuevaParticion = new Particion(direccionNueva, espacioSobrante);
                memoria.add(memoria.indexOf(peorParticion) + 1, nuevaParticion);
                peorParticion.setTamanio(trabajo.getMemoriaRequerida());
            }
            return true; //trabajo asignado con exito
        }

        return false; //no se encontro una particion adecuada
    }
}
