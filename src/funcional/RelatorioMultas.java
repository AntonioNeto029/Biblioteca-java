package funcional;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioMultas(List<LinhaMulta> itens, BigDecimal total) {
}
