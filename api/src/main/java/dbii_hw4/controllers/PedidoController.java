package dbii_hw4.controllers;

import dbii_hw4.models.Pedido;
import dbii_hw4.respositories.ResumenRegionProjection;
import dbii_hw4.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return pedidoService.crearPedido(pedido);
    }

    @GetMapping("/{id}")
    public Pedido obtenerPedido(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id);
    }

    @GetMapping
    public Page<Pedido> listarPedidos(
            @RequestParam String region,
            @RequestParam String status,
            Pageable pageable) {
        return pedidoService.listarPorRegionYStatus(region, status, pageable);
    }

    @GetMapping("/resumen")
    public ResumenRegionProjection obtenerResumen(@RequestParam String region) {
        return pedidoService.obtenerResumen(region);
    }

    @PutMapping("/{id}/status")
    public Pedido actualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // Formato del JSON que recibe: {"status": "completed"}
        String nuevoStatus = body.get("status");
        if (nuevoStatus == null) {
            throw new IllegalArgumentException("El campo 'status' es requerido");
        }
        return pedidoService.actualizarStatus(id, nuevoStatus);
    }
}
