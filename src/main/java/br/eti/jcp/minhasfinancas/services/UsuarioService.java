package br.eti.jcp.minhasfinancas.services;

import br.eti.jcp.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);

    Optional<Usuario> carregarPorId(Long id);

}
