package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/carro")
public class CarroController {

    @Autowired
    private CarroService carroService;

    private boolean temPermissaoDeEdicao(String cargo) {
        return cargo != null && (cargo.equalsIgnoreCase("ADMIN") || cargo.equalsIgnoreCase("ADMINISTRADOR") || cargo.equalsIgnoreCase("VENDEDOR"));
    }

    @PostMapping
    public ResponseEntity<?> salvarCarro(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) String cargo,
            @RequestBody Carro c) {

        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");
        }
        if (!temPermissaoDeEdicao(cargo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas Administradores e Vendedores podem registar carros.");
        }

        Carro savedCarro = carroService.save(c);
        return ResponseEntity.ok(savedCarro);
    }

    @PutMapping
    public ResponseEntity<?> atualizarCarro(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) String cargo,
            @RequestBody Carro c) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");
        if (!temPermissaoDeEdicao(cargo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        Carro updatedCarro = carroService.update(c);
        return ResponseEntity.ok(updatedCarro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCarro(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) String cargo,
            @PathVariable Long id) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");
        if (!temPermissaoDeEdicao(cargo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        carroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> pesquisarCarroPorId(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @PathVariable Long id) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");

        Optional<Carro> carro = carroService.findById(id);
        if (carro.isPresent()) {
            return ResponseEntity.ok(carro.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<?> pesquisarTodosCarros(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");

        List<Carro> carros = carroService.findAll();
        return ResponseEntity.ok(carros);
    }

    @PostMapping(value = "/getCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pesquisarCarroPorModelo(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestBody String modelo) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");

        Optional<Carro> carro = carroService.findByModelo(modelo.trim());
        if (carro.isPresent()) {
            return ResponseEntity.ok(carro.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/deleteCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deletarCarroPorModelo(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) String cargo,
            @RequestBody String modelo) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");
        if (!temPermissaoDeEdicao(cargo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        Carro carro = carroService.deleteByModelo(modelo.trim());
        return ResponseEntity.ok("Carro deletado: " + carro.getModelo());
    }

    @PostMapping(value = "/updateCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> atualizarCarroPorModelo(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) String cargo,
            @RequestBody String payload) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");
        if (!temPermissaoDeEdicao(cargo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        String[] partes = payload.split(",", 2);
        if (partes.length < 2) {
            throw new CarroException("Payload inválido. Use modelo,preco.");
        }
        String modelo = partes[0].trim();
        double preco = parsePreco(partes[1].trim());
        Carro updatedCarro = carroService.updateByModelo(modelo, preco);
        return ResponseEntity.ok(updatedCarro);
    }

    @PostMapping(value = "/teste", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> bomDia(
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario,
            @RequestBody String nome) {

        if (idUsuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado.");

        return ResponseEntity.ok("bem vindo, " + nome.trim());
    }

    private double parsePreco(String preco) {
        try {
            return Double.parseDouble(preco);
        } catch (NumberFormatException ex) {
            throw new CarroException("Preço inválido: " + preco);
        }
    }
}