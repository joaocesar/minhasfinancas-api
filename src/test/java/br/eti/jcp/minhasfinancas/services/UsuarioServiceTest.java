package br.eti.jcp.minhasfinancas.services;

import br.eti.jcp.minhasfinancas.exceptions.AutenticacaoException;
import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.repositories.UsuarioRepository;
import br.eti.jcp.minhasfinancas.services.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    private UsuarioServiceImpl service;

    @MockBean
    private UsuarioRepository repository;

    @Test
    public void validarEmailInexistente() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        service.validarEmail("usuario@email.com");
    }

    @Test
    public void invalidarEmailExistente() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validarEmail("usuario@email.com");})
                .withMessage("Já existe um usuário cadastrado com esse email");
    }

    @Test
    public void autenticarUsuarioComSucesso() {
        String email = "usuario@email.com";
        String senha = "1234";
        Usuario usuarioMock = Usuario.builder()
                .nome("Usuario")
                .email(email)
                .senha(senha)
                .id(1L)
                .build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuarioMock));
        Usuario usuario = service.autenticar(email, senha);
        Assertions.assertThat(usuario).isNotNull();
    }

    @Test
    public void naoAutenticarUsuarioComEmailInvalido() {
        String email = "usuario@email.com";
        String senha = "1234";
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(AutenticacaoException.class)
                .isThrownBy(()-> {service.autenticar(email, senha);})
                .withMessage("Usuário ou senha invalidos.");
    }

    @Test
    public void naoAutenticarUsuarioComSenhaInvalida() {
        String email = "usuario@email.com";
        String senha = "1234";
        Usuario usuarioMock = Usuario.builder().nome("Usuario").email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuarioMock));
        Assertions.assertThatExceptionOfType(AutenticacaoException.class)
                .isThrownBy(()-> {service.autenticar(email, "senha");})
                .withMessage("Usuário ou senha invalidos.");
    }

    @Test
    public void salvarUsuarioComSucesso() {
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuarioMock = Usuario.builder()
                .id(1L)
                .nome("Usuario")
                .email("usuario@email.com")
                .senha("1234")
                .build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuarioMock);
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("1234");
    }

    @Test
    public void naoSalvarUsuarioComEmailJaCadastrado() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Usuario usuarioMock = Usuario.builder().email("usuario@email.com").build();
        Assertions.assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.salvarUsuario(usuarioMock);})
                .withMessage("Já existe um usuário cadastrado com esse email");
        Mockito.verify(repository, Mockito.never()).save(usuarioMock);
    }
}
