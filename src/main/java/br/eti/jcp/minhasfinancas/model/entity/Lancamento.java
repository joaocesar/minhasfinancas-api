package br.eti.jcp.minhasfinancas.model.entity;

import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.enums.TipoLancamento;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento", schema = "financas")
@Data
@Builder
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "data_cadastro", nullable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;

    @Column(name = "tipo", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;

}
