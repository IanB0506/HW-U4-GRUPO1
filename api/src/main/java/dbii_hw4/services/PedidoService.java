package dbii_hw4.services;

import dbii_hw4.models.Pedido;
import dbii_hw4.respositories.PedidoRepository;
import dbii_hw4.respositories.ResumenRegionProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final Random random = new Random();

    // POST /pedidos
    public Pedido crearPedido(Pedido pedido) {
        StopWatch watch = new StopWatch();

        pedido.setTotal(pedido.getPrecioUnit().multiply(BigDecimal.valueOf(pedido.getCantidad())));
        pedido.setPedidoRef("PED-" + Instant.now().toEpochMilli() + "-" + random.nextInt(10000));
        pedido.setStatus("pending");

        watch.start();

        Pedido guardado = pedidoRepository.save(pedido);

        watch.stop();
        log.info("[POST /pedidos] Ejecución SQL tomó {} ms", watch.getTotalTimeMillis());
        return guardado;
    }

    // GET /pedidos/{id}
    public Pedido obtenerPorId(Long id) {
        StopWatch watch = new StopWatch();
        watch.start();

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        watch.stop();
        log.info("[GET /pedidos/{}] Ejecución SQL tomó {} ms", id, watch.getTotalTimeMillis());
        return pedido;
    }

    // GET /pedidos?region=X&status=Y
    public Page<Pedido> listarPorRegionYStatus(String region, String status, Pageable pageable) {
        StopWatch watch = new StopWatch();
        watch.start();

        Page<Pedido> resultados = pedidoRepository.findByRegionAndStatus(region, status, pageable);

        watch.stop();
        log.info("[GET /pedidos?region={}&status={}] Ejecución SQL tomó {} ms", region, status, watch.getTotalTimeMillis());
        return resultados;
    }

    // GET /pedidos/resumen?region=X
    public ResumenRegionProjection obtenerResumen(String region) {
        StopWatch watch = new StopWatch();
        watch.start();

        ResumenRegionProjection resumen = pedidoRepository.obtenerResumenPorRegion(region);

        watch.stop();
        log.info("[GET /pedidos/resumen?region={}] Ejecución SQL nativa tomó {} ms", region, watch.getTotalTimeMillis());
        return resumen;
    }

    // PUT /pedidos/{id}/status
    public Pedido actualizarStatus(Long id, String nuevoStatus) {
        StopWatch watch = new StopWatch();

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        pedido.setStatus(nuevoStatus);

        watch.start();

        Pedido actualizado = pedidoRepository.save(pedido);

        watch.stop();
        log.info("[PUT /pedidos/{}/status] Ejecución SQL tomó {} ms", id, watch.getTotalTimeMillis());
        return actualizado;
    }
}
