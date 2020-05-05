package br.eti.jcp.minhasfinancas.services;

import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.enums.TipoLancamento;
import br.eti.jcp.minhasfinancas.model.factory.LancamentoTestFactory;
import br.eti.jcp.minhasfinancas.model.repositories.LancamentoRepository;
import br.eti.jcp.minhasfinancas.services.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    private LancamentoTestFactory factory = new LancamentoTestFactory();

    @Test
    public void salvarLancamento() {
        Lancamento lancamentoASalvar = factory.criarLancamento();
        doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = factory.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

    }

    @Test
    public void naoSalvarLancamentoInvalido() {
        Lancamento lancamentoASalvar = factory.criarLancamento();
        doThrow(RegraDeNegocioException.class).when(service).validar(lancamentoASalvar);
        catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraDeNegocioException.class);
        verify(repository, never()).save(lancamentoASalvar);

    }

    @Test
    public void atualizarLancamento() {

        Lancamento lancamentoSalvo = factory.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        doNothing().when(service).validar(lancamentoSalvo);
        when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.salvar(lancamentoSalvo);

        verify(repository, times(1)).save(lancamentoSalvo);
    }

    @Test
    public void naoAtualizarLancamentoNaoSalvo() {
        Lancamento lancamentoASalvar = factory.criarLancamento();
        catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
        verify(repository, never()).save(lancamentoASalvar);
    }

    @Test
    public void excluirLancamento() {

        Lancamento lancamento = factory.criarLancamento();
        lancamento.setId(1L);

        service.excluir(lancamento);

        verify(repository, times(1)).delete(lancamento);
    }

    @Test
    public void naoExcluirLancamentoNaoSalvo() {
        Lancamento lancamento = factory.criarLancamento();
        catchThrowableOfType(() -> service.excluir(lancamento), NullPointerException.class);
        verify(repository, never()).delete(lancamento);
    }

    @Test
    public void buscaLancamento() {
        Lancamento lancamento = factory.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        when(repository.findAll(any(Example.class))).thenReturn(lista);

        List<Lancamento> lancamentos = service.buscar(lancamento);

        assertThat(lancamentos).isNotEmpty().hasSize(1).contains(lancamento);
    }

    @Test
    public void atualizarStatusDoLancamento() {
        Lancamento lancamento = factory.criarLancamento();
        lancamento.setId(1L);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        verify(service, times(1)).atualizar(lancamento);
    }

    @Test
    public void carregarPorId() {
        Long id = 1L;
        Lancamento lancamento = factory.criarLancamento();
        lancamento.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> resultado = service.carregarPorId(id);
        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void naoCarregarPorIdNaoExistente() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Lancamento> resultado = service.carregarPorId(id);
        assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void validarLancamento() {
        Lancamento lancamento = factory.criarLancamento();
        assertThatCode(() -> {service.validar(lancamento);}).doesNotThrowAnyException();
        lancamento.setDescricao("");
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe uma descrição válida.");
        lancamento.setDescricao(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe uma descrição válida.");
        lancamento.setDescricao("Descrição Testada");
        lancamento.setMes(0);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um mês válido.");
        lancamento.setMes(13);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um mês válido.");
        lancamento.setMes(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um mês válido.");
        lancamento.setMes(1);
        lancamento.setAno(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um ano válido");
        lancamento.setAno(123);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um ano válido");
        lancamento.setAno(12344);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um ano válido");
        lancamento.setAno(2020);
        Usuario usuario = lancamento.getUsuario();
        lancamento.setUsuario(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um usuário válido");
        usuario.setId(null);
        lancamento.setUsuario(usuario);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um usuário válido");
        usuario.setId(1L);
        lancamento.setUsuario(usuario);
        lancamento.setValor(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um valor válido.");
        lancamento.setValor(BigDecimal.ZERO);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um valor válido.");
        lancamento.setValor(BigDecimal.TEN);
        lancamento.setTipo(null);
        assertThatExceptionOfType(RegraDeNegocioException.class)
                .isThrownBy(()-> {service.validar(lancamento);})
                .withMessage("Informe um tipo de lançamento");
    }

    @Test
    public void verificarExecoesLancadasAoValidarLancamento() {
        Lancamento lancamento = new Lancamento();
        Throwable exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe uma descrição válida.");
        lancamento.setDescricao("");
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe uma descrição válida.");
        lancamento.setDescricao("Salario");
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um mês válido.");
        lancamento.setMes(0);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um mês válido.");
        lancamento.setMes(13);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um mês válido.");
        lancamento.setMes(1);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um ano válido");
        lancamento.setAno(123);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um ano válido");
        lancamento.setAno(12345);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um ano válido");
        lancamento.setAno(2020);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um usuário válido");
        Usuario usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").senha("senha123").build();
        lancamento.setUsuario(usuario);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um usuário válido");
        usuario.setId(1L);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um valor válido.");
        lancamento.setValor(BigDecimal.ZERO);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um valor válido.");
        lancamento.setValor(BigDecimal.TEN);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Informe um tipo de lançamento");
        lancamento.setTipo(TipoLancamento.RECEITA);
        exception = catchThrowable(() -> service.validar(lancamento));
        assertThat(exception).isNull();
    }

}
