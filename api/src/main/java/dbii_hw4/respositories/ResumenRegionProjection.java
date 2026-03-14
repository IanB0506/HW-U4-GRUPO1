package dbii_hw4.respositories;

import java.math.BigDecimal;

// Interfaz para mapear el resultado del request /pedidos/resumen?region=Norte
public interface ResumenRegionProjection {

    String getRegion();
    Long getCantidad();      // Para el COUNT(id)
    BigDecimal getGranTotal(); // Para el SUM(total)
}
