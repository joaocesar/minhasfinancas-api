package br.eti.jcp.minhasfinancas.model.repositories;

import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void verificarAExistenciaDeUmEmail() {
        Usuario usuario = getUsuario();
        entityManager.persist(usuario);
        boolean emailJaExiste = repository.existsByEmail("usuario@email.com");
        Assertions.assertThat(emailJaExiste).isTrue();
    }

    @Test
    public void verificarAInexistenciaDeUmEmail() {
        boolean emailExiste = repository.existsByEmail("usuario@email.com");
       Assertions.assertThat(emailExiste).isFalse();
    }

    @Test
    public void salvarUsuarioNaBase() {
        Usuario usuario = getUsuario();
        Usuario usuarioSalvo = entityManager.persist(usuario);
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void pesquisaUsuarioPorEmail() {
        entityManager.persist(getUsuario());
        Optional<Usuario> usuarioOptional = repository.findByEmail("usuario@email.com");
        Assertions.assertThat(usuarioOptional.isPresent()).isTrue();
    }

    @Test
    public void pesquisaUsuarioPorEmailNaoCadastrado() {
        Optional<Usuario> usuarioOptional = repository.findByEmail("usuario@email.com");
        Assertions.assertThat(usuarioOptional.isPresent()).isFalse();
    }

    private Usuario getUsuario() {
        return Usuario.builder().nome("Usuario").email("usuario@email.com").senha("1234").build();
    }

}
