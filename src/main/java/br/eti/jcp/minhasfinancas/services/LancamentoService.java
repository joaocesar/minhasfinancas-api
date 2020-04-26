package br.eti.jcp.minhasfinancas.services;

import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void excluir(Lancamento lancamento);
    List<Lancamento> buscar(Lancamento lancamentoFiltro);
    void atualizarStatus(Lancamento lancamento, StatusLancamento status);
    void validar(Lancamento lancamento);

}
