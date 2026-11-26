package cl.duoc.casasembrujadas.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class RunValidator implements ConstraintValidator<RunValido, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        if (valor == null || valor.isBlank()) {
            return true;
        }
        return RunUtils.esValido(valor);
    }
}