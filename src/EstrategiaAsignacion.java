import java.util.List;

public interface EstrategiaAsignacion {
    boolean asignarTrabajo(Trabajo trabajo, List<Particion> memoria);
}
