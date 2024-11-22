public class Particion {
    private int direccionInicio;
    private int tamanio;
    private boolean estaLibre;
    private Trabajo trabajoAsignado; //será null si la particion esta libre

    public Particion(int direccionInicio, int tamanio) {
        if (tamanio <= 0) {
            throw new IllegalArgumentException("El tamaño de la partición debe ser mayor a 0.");
        }
        this.direccionInicio = direccionInicio;
        this.tamanio = tamanio;
        this.estaLibre = true; //por defecto una particion esta libre al crearse
        this.trabajoAsignado = null;
    }

    public int getDireccionInicio() {
        return direccionInicio;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaLibre() {
        return estaLibre;
    }

    public Trabajo getTrabajoAsignado() {
        return trabajoAsignado;
    }

    public void asignarTrabajo(Trabajo trabajo) {
        if (!estaLibre) {
            throw new IllegalStateException("La partición ya está ocupada.");
        }
        if (trabajo.getMemoriaRequerida() > tamanio) {
            throw new IllegalArgumentException("El trabajo requiere más memoria que el tamaño de la partición.");
        }
        this.trabajoAsignado = trabajo;
        this.estaLibre = false;
    }

    public void liberar() {
        if (estaLibre) {
            throw new IllegalStateException("La partición ya está libre.");
        }
        this.trabajoAsignado = null;
        this.estaLibre = true;
    }


    @Override
    public String toString() {
        return "Particion{" +
                "direccionInicio=" + direccionInicio +
                ", tamanio=" + tamanio +
                ", estaLibre=" + estaLibre +
                ", trabajoAsignado=" + (trabajoAsignado != null ? trabajoAsignado.getNombre() : "Ninguno") +
                '}';
    }

    public void setTamanio(int tamanio) {
        if (tamanio <= 0) {
            throw new IllegalArgumentException("El tamaño de la partición debe ser mayor a 0.");
        }
        this.tamanio = tamanio;
    }

}

