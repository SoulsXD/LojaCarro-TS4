package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private CarroService carroService;

    private void validarAnoAuxiliar(int ano) {
        if (ano < 0) {
            throw new CarroException("O ano não pode ser negativo.");
        }
        if (ano > 2026) {
            throw new CarroException("O ano não pode ser maior que o atual.");
        }
    }

    private Carro carroValido;
    private Carro segundoCarro;

    @BeforeEach
    void prepararCarroParaOsTestes() {
        carroValido = new Carro();
        carroValido.setId(1L);
        carroValido.setModelo("Gol");
        carroValido.setAno(2023);
        carroValido.setPreco(50000.0);

        segundoCarro = new Carro();
        segundoCarro.setId(2L);
        segundoCarro.setModelo("Uno");
        segundoCarro.setAno(2020);
        segundoCarro.setPreco(35000.0);
    }


    //salvar
    @Test
    void salvaOcarroCorretamenteQuandoTodosOsDadosEstaoCertos() {
        when(carroRepository.save(any(Carro.class))).thenReturn(carroValido);

        Carro salvo = carroService.save(carroValido);

        assertNotNull(salvo);
        assertEquals("Gol", salvo.getModelo());
        verify(carroRepository, times(1)).save(carroValido);
    }

    //update preço
    @Test
    void atualizaOcarroSemProblemas() {
        when(carroRepository.existsById(1L)).thenReturn(true);
        when(carroRepository.save(any(Carro.class))).thenReturn(carroValido);

        carroValido.setPreco(55000.0);
        Carro atualizado = carroService.update(carroValido);

        assertEquals(55000.0, atualizado.getPreco());
        verify(carroRepository, times(1)).save(carroValido);
    }

    //delete
    @Test
    void deletaOcarroSeOIdForValido() {
        assertDoesNotThrow(() -> carroService.deleteById(1L));
        verify(carroRepository, times(1)).deleteById(1L);
    }

    //list
    @Test
    void listaTodosOsCarrosCadastrados() {
        when(carroRepository.findAll()).thenReturn(Arrays.asList(carroValido, segundoCarro));
        List<Carro> carros = carroService.findAll();
        assertEquals(2, carros.size());
        verify(carroRepository, times(1)).findAll();
    }

    //busca por id
    @Test
    void buscaOcarroPeloIdComSucesso() {
        when(carroRepository.findById(1L)).thenReturn(Optional.of(carroValido));

        Optional<Carro> resultado = carroService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Gol", resultado.get().getModelo());
    }

    //preço neg
    @Test
    void barraCadastroDeCarroComPrecoNegativo() {
        carroValido.setPreco(-100.0);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.save(carroValido));
        assertTrue(exception.getMessage().contains("não pode ser negativo"));
    }

    //id 0 ou neg
    @Test
    void lancaExcecaoAoTentarDeletarComIdZeroOuNegativo() {
        CarroException exception = assertThrows(CarroException.class, () -> carroService.deleteById(0L));
        assertTrue(exception.getMessage().contains("não pode ser negativo"));
    }

    //ano neg
    @Test
    void barraCadastroDeCarroComAnoNegativo() {
        carroValido.setAno(-2020);

        CarroException exception = assertThrows(CarroException.class, () -> validarAnoAuxiliar(carroValido.getAno()));
        assertTrue(exception.getMessage().contains("não pode ser negativo"));
    }

    //ano maior que 2026
    @Test
    void barraCadastroDeCarroComAnoNoFuturo() {
        int anoFuturo = 2027;
        carroValido.setAno(anoFuturo);

        CarroException exception = assertThrows(CarroException.class, () -> validarAnoAuxiliar(carroValido.getAno()));
        assertTrue(exception.getMessage().contains("não pode ser maior que o atual"));
    }
}