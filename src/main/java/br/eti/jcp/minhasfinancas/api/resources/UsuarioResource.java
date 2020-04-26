package br.eti.jcp.minhasfinancas.api.resources;

import br.eti.jcp.minhasfinancas.api.dto.UsuarioDTO;
import br.eti.jcp.minhasfinancas.exceptions.AutenticacaoException;
import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    public UsuarioResource(UsuarioService service) {
        this.service = service;
    }

    private UsuarioService service;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (AutenticacaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
