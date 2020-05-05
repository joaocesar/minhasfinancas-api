package br.eti.jcp.minhasfinancas.api.resources;

import br.eti.jcp.minhasfinancas.api.dto.AtualizacaoStatusDTO;
import br.eti.jcp.minhasfinancas.api.dto.LancamentoDTO;
import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Lancamento;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.enums.StatusLancamento;
import br.eti.jcp.minhasfinancas.model.enums.TipoLancamento;
import br.eti.jcp.minhasfinancas.services.LancamentoService;
import br.eti.jcp.minhasfinancas.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        Optional<Usuario> usuario = usuarioService.carregarPorId(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario não encontrado");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }
    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento lancamento = converter(dto);
            lancamento = service.salvar(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.carregarPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraDeNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElse( new ResponseEntity("Lancamento não encontrado.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizacaoStatusDTO dto) {
        return service.carregarPorId(id).map(entity -> {
            StatusLancamento statusLancamento = StatusLancamento.valueOf(dto.getStatus());
            if (statusLancamento == null) {
                return ResponseEntity.badRequest().body("Status invalido");
            }
            try {
                entity.setStatus(statusLancamento);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraDeNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElse( new ResponseEntity("Lancamento não encontrado.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        return service.carregarPorId(id).map(entity -> {
            try {
                service.excluir(entity);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (RegraDeNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElse( new ResponseEntity("Lancamento não encontrado.", HttpStatus.BAD_REQUEST));
    }
    private Lancamento converter(LancamentoDTO dto) {
        Usuario usuario = usuarioService.carregarPorId(dto.getUsuario()).orElseThrow(() -> new RegraDeNegocioException("Usuario não encontrado"));
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        lancamento.setUsuario(usuario);
        if (Objects.nonNull(dto.getTipo())) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if (Objects.nonNull(dto.getStatus())) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }
}
