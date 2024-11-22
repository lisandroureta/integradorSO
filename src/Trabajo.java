public class Trabajo {
    private String nombre;
    private int instanteArribo;
    private int duracion;
    private int memoriaRequerida;

    public Trabajo(String nombre, int instanteArribo, int duracion, int memoriaRequerida) {
        this.nombre = nombre;
        this.instanteArribo = instanteArribo;
        this.duracion = duracion;
        this.memoriaRequerida = memoriaRequerida;
    }

    @Override
    public String toString() {
        return "Trabajo{" +
                "nombre='" + nombre + '\'' +
                ", instanteArribo=" + instanteArribo +
                ", duracion=" + duracion +
                ", memoriaRequerida=" + memoriaRequerida +
                '}';
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getMemoriaRequerida() {
        return this.memoriaRequerida;
    }

    public int getInstanteArribo() {
        return this.instanteArribo;
    }

    public int getDuracion() {
        return this.duracion;
    }
}
