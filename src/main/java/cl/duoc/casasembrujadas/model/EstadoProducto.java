package cl.duoc.casasembrujadas.model;

public enum EstadoProducto {
    DISPONIBLE,
    RESERVADA,
    VENDIDA,
    INACTIVA;

    public boolean puedeCambiarA(EstadoProducto destino) {
        return switch (this) {
            case DISPONIBLE -> destino == RESERVADA || destino == VENDIDA || destino == INACTIVA;
            case RESERVADA -> destino == DISPONIBLE || destino == VENDIDA;
            case VENDIDA -> destino == DISPONIBLE || destino == INACTIVA;
            case INACTIVA -> destino == DISPONIBLE;
        };
    }
}
