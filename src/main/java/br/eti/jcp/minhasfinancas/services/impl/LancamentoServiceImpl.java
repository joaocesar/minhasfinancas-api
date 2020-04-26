package br.eti.jcp.minhasfinancas.services.impl;

import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.repositories.LancamentoRepository;
import br.eti.jcp.minhasfinancas.services.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }

    private LancamentoRepository repository;

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    public void excluir(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example<Lancamento> example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        atualizar(lancamento);

    }

    @Override
    public void validar(Lancamento lancamento) {
        if (lancamento.getDescricao() == null || lancamento.getDescricao().isBlank()) {
            throw new RegraDeNegocioException("Informe uma descrição válida.");
        }

        if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraDeNegocioException("Informe um mês válido.");
        }

        if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
            throw new RegraDeNegocioException("Informe um ano válido");
        }

        if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null ) {
            throw new RegraDeNegocioException("Informe um usuário válido");
        }

        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
            throw new RegraDeNegocioException("Informe um valor válido.");
        }

        if (lancamento.getTipo() == null) {
            throw new RegraDeNegocioException("Informe um tipo de lançamento");
        }
    }
}
