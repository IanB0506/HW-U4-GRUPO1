package dbii_hw4.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_ref", nullable = false, length = 40, unique = true)
    private String pedidoRef;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "categoria_id", nullable = false)
    private Integer categoriaId;

    @Column(nullable = false, length = 40)
    private String region;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 20)
    private String canal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
