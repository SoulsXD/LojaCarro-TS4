package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.model.TipoUsuario;
import br.org.edu.ifrn.LojaCarro.model.Log;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import br.org.edu.ifrn.LojaCarro.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogRepository logRepository;

    public Usuario fazerLogin(String email, String senha) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("E-mail não encontrado."));

        if (!usuario.getSenha().equals(senha)) {
            throw new Exception("Senha incorreta.");
        }
        return usuario;
    }

    public Usuario criarUsuario(Usuario novoUsuario, Long usuarioLogadoId) throws Exception {
        Usuario usuarioLogado = usuarioRepository.findById(usuarioLogadoId)
                .orElseThrow(() -> new Exception("Usuário logado não encontrado."));

        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new Exception("Este e-mail já está em uso.");
        }

        if (novoUsuario.getCargoNovo() == TipoUsuario.VENDEDOR && usuarioLogado.getCargoNovo() != TipoUsuario.ADMIN) {
            throw new Exception("Apenas administradores podem adicionar vendedores.");
        }

        if (novoUsuario.getCargoNovo() == TipoUsuario.CLIENTE) {
            if (usuarioLogado.getCargoNovo() != TipoUsuario.ADMIN && usuarioLogado.getCargoNovo() != TipoUsuario.VENDEDOR) {
                throw new Exception("Apenas vendedores ou administradores podem adicionar clientes.");
            }
        }

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        registrarLog(usuarioLogado.getId(), usuarioLogado.getNome(), "CRIOU o usuário: " + novoUsuario.getNome());
        return usuarioSalvo;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(Long id, Usuario dadosAtualizados, Long usuarioLogadoId) throws Exception {
        Usuario usuarioLogado = usuarioRepository.findById(usuarioLogadoId)
                .orElseThrow(() -> new Exception("Usuário logado não encontrado."));

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário a ser atualizado não encontrado."));

        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setCargoNovo(dadosAtualizados.getCargoNovo());

        Usuario atualizado = usuarioRepository.save(usuarioExistente);
        registrarLog(usuarioLogado.getId(), usuarioLogado.getNome(), "ATUALIZOU o usuário ID: " + id);
        return atualizado;
    }

    public void deletarUsuario(Long id, Long usuarioLogadoId) throws Exception {
        if (id.equals(usuarioLogadoId)) {
            throw new Exception("Operação negada: Você não pode deletar a si mesmo do sistema.");
        }

        Usuario usuarioLogado = usuarioRepository.findById(usuarioLogadoId)
                .orElseThrow(() -> new Exception("Usuário logado não encontrado."));

        if (usuarioLogado.getCargoNovo() != TipoUsuario.ADMIN) {
            throw new Exception("Apenas administradores podem deletar usuários.");
        }

        usuarioRepository.deleteById(id);
        registrarLog(usuarioLogado.getId(), usuarioLogado.getNome(), "DELETOU o usuário ID: " + id);
    }

    private void registrarLog(Long idUsuario, String nome, String acao) {
        Log log = new Log(idUsuario, nome, acao);
        logRepository.save(log);
    }
}