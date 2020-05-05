package br.eti.jcp.minhasfinancas.services.impl;

import br.eti.jcp.minhasfinancas.exceptions.AutenticacaoException;
import br.eti.jcp.minhasfinancas.exceptions.RegraDeNegocioException;
import br.eti.jcp.minhasfinancas.model.entity.Usuario;
import br.eti.jcp.minhasfinancas.model.repositories.UsuarioRepository;
import br.eti.jcp.minhasfinancas.services.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if (usuario.isEmpty() || !usuario.get().getSenha().equals(senha)) {
                throw new AutenticacaoException("Usuário ou senha invalidos.");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com esse email");
        }
    }

    @Override
    public Optional<Usuario> carregarPorId(Long id) {
        return repository.findById(id);
    }
}
