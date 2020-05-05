package br.eti.jcp.minhasfinancas.model.repositories;

import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.factory.LancamentoTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario;

    private LancamentoTestFactory factory;

    @BeforeEach
    public void initTest() {
        factory = new LancamentoTestFactory(usuarioRepository);

    }

    @Test
    public void salvarLancamentoHappyDay() {
        Lancamento lancamento = factory.criarLancamento();
        lancamento = repository.save(lancamento);
        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void excluirLancamentoHappyDay() {
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());
        repository.delete(lancamento);
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoInexistente).isNull();
    }

    @Test
    public void atualizarLancamentoHappyDay() {
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento.setAno(2019);
        lancamento.setStatus(StatusLancamento.CANCELADO);
        lancamento.setDescricao("Lancamento Atualizado");
        repository.save(lancamento);

        Lancamento lancamentoAtulizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoAtulizado.getAno()).isEqualTo(2019);
        assertThat(lancamentoAtulizado.getDescricao()).isEqualTo("Lancamento Atualizado");
        assertThat(lancamentoAtulizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

    }

    @Test
    public void buscarLancamentoPorIdHappyDay() {
        Lancamento lancamento = criarEPersistirLancamento();
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    private Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = factory.criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

}
