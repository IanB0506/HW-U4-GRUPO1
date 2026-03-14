package dbii_hw4.respositories;

import dbii_hw4.models.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // GET /pedidos?region=X&status=Y
    Page<Pedido> findByRegionAndStatus(String region, String status, Pageable pageable);

    // GET /pedidos/resumen?region=X
    @Query(value = "SELECT region, COUNT(*) AS cantidad, SUM(total) AS gran_total " +
            "FROM pedidos " +
            "WHERE region = :region " +
            "GROUP BY region",
            nativeQuery = true)
    ResumenRegionProjection obtenerResumenPorRegion(@Param("region") String region);
}
