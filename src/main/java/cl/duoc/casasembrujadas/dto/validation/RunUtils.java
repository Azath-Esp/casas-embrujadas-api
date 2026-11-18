package cl.duoc.casasembrujadas.dto.validation;

/** Operaciones sobre el RUN chileno: normalizar el formato y calcular el dígito verificador.
 */
public final class RunUtils {

    private RunUtils() {
    }

    public static String normalizar(String run) {
        if (run == null) {
            return null;
        }
        String limpio = run.replace(".", "").replace("-", "").replace(" ", "").toUpperCase();
        if (limpio.length() < 2) {
            return limpio;
        }
        return limpio.substring(0, limpio.length() - 1) + "-" + limpio.substring(limpio.length() - 1);
    }

    public static char calcularDigitoVerificador(int cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        while (cuerpo > 0) {
            suma += (cuerpo % 10) * multiplicador;
            cuerpo /= 10;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) {
            return '0';
        }
        if (resto == 10) {
            return 'K';
        }
        return (char) ('0' + resto);
    }

    public static boolean esValido(String run) {
        String normalizado = normalizar(run);
        if (normalizado == null || !normalizado.matches("^\\d{7,8}-[\\dK]$")) {
            return false;
        }
        int cuerpo = Integer.parseInt(normalizado.substring(0, normalizado.indexOf('-')));
        char digito = normalizado.charAt(normalizado.length() - 1);
        return calcularDigitoVerificador(cuerpo) == digito;
    }
}