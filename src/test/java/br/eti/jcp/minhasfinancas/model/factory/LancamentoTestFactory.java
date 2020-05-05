package br.eti.jcp.minhasfinancas.model.factory;

import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.enums.TipoLancamento;
import br.eti.jcp.minhasfinancas.model.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class LancamentoTestFactory {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public LancamentoTestFactory() {
    }

    public LancamentoTestFactory(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Lancamento criarLancamento() {
        return Lancamento.builder()
                .usuario(criarUsuario())
                .ano(2020)
                .mes(1)
                .descricao("Um lancamento qualquer")
                .valor(BigDecimal.TEN)
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    private Usuario criarUsuario() {
        Usuario usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").senha("abdc123").build();
        if (Objects.nonNull(usuarioRepository)) {
            return usuarioRepository.save(usuario);
        } else {
            usuario.setId(1L);
        }
        return usuario;
    }

}
