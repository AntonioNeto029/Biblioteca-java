package funcional;

import java.math.BigDecimal;

public record LinhaMulta(int idEmprestimo, String titulo, String usuario, long diasAtraso, BigDecimal multa) {
}
