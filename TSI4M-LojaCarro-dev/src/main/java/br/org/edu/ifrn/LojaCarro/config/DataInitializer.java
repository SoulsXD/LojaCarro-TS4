package br.org.edu.ifrn.LojaCarro.config;

import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.model.TipoUsuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        String emailAdmin = "admin@lojacarro.com";

        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador Principal");
            admin.setEmail(emailAdmin);
            admin.setSenha("admin123");
            admin.setCargoNovo(TipoUsuario.ADMIN);

            usuarioRepository.save(admin);
            System.out.println("Administrador principal criado com sucesso EMAIL: admin@lojacarro.com SENHA: admin123");
        } else {
            System.out.println("Administrador principal já existe no sistema. EMAIL: admin@lojacarro.com SENHA: admin123");
        }
    }
}