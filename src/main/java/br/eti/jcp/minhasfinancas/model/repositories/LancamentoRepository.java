package br.eti.jcp.minhasfinancas.model.repositories;

import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
