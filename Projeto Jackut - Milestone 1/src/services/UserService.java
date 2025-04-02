package services;

import entities.*;
import exceptions.*;
import java.util.*;
import java.io.*;

/**
 * Serviço responsável por gerenciar tudo que envolve usuários:
 * sessões, amizades e mensagens enviadas e recebidas do sistema Jackut.
 */
public class UserService {

    // Mapa que armazena todos os usuários do sistema (login -> User)
    private Map<String, User> usuarios;

    // Mapa que controla as sessões ativas (idSessao -> login)
    private Map<String, String> sessoesAtivas;

    /**
     * Construtor que inicializa o serviço carregando dados persistentes.
     * Se não existirem dados, inicia com coleções vazias.
     */
    public UserService() {
        Object loadedData = Jackut.load();
        if (loadedData instanceof Map) {
            this.usuarios = (Map<String, User>) loadedData;
            this.sessoesAtivas = new HashMap<>();
        } else {
            this.usuarios = new HashMap<>();
            this.sessoesAtivas = new HashMap<>();
        }
    }

    /**
     * Cria um novo usuário no sistema após validar os parâmetros.
     * Lança exceções específicas para casos de erro.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null || login.isBlank()) {
            throw new RuntimeException("Login inválido.");
        }
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Senha inválida.");
        }
        if (usuarios.containsKey(login)) {
            throw new UserAlreadyExistsException("Conta com esse nome já existe.");
        }

        usuarios.put(login, new User(login, senha, nome));
        salvarDados();
    }

    /**
     * Autentica um usuário e cria uma nova sessão.
     * Retorna um ID único de sessão para uso futuro.
     */
    public String abrirSessao(String login, String senha) {
        User usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Login ou senha inválidos.");
        }
        String idSessao = UUID.randomUUID().toString();
        sessoesAtivas.put(idSessao, login);
        return idSessao;
    }

    /**
     * Recupera um atributo específico do perfil do usuário.
     * Trata separadamente o atributo especial 'nome'.
     */
    public String getAtributoUsuario(String login, String atributo) {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        if ("nome".equalsIgnoreCase(atributo)) {
            return usuario.getNome();
        }

        String valor = usuario.getProfile().getAttribute(atributo);
        if (valor.isEmpty()) {
            throw new AttributeNotFilledException("Atributo não preenchido.");
        }
        return valor;
    }

    /**
     * Edita um atributo do perfil do usuário autenticado.
     * Requer sessão válida e persiste as alterações.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        User usuario = getUsuarioPorSessao(idSessao);
        usuario.getProfile().setAttribute(atributo, valor);
        salvarDados();
    }

    /**
     * Método interno para obter usuário a partir de um ID de sessão.
     * Valida se a sessão existe antes de retornar.
     */
    private User getUsuarioPorSessao(String idSessao) {
        String login = sessoesAtivas.get(idSessao);
        if (login == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        return usuarios.get(login);
    }

    /**
     * Retorna a lista de amigos de um usuário.
     * Mantém a ordem de confirmação das amizades.
     */
    public LinkedHashSet<String> getAmigos(String login) {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        return usuario.getAmigos();
    }

    /**
     * Gerencia o processo de adição de amigos com todas as validações necessárias.
     * Trata tanto solicitações novas quanto confirmações de amizade.
     */
    public void adicionarAmigo(String idSessao, String amigoLogin) {
        try {
            User usuario = getUsuarioPorSessao(idSessao);
            User amigo = usuarios.get(amigoLogin);

            if (amigo == null) throw new UserNotFoundException("Usuário não cadastrado.");
            if (usuario.getLogin().equals(amigoLogin))
                throw new FriendshipException("Usuário não pode adicionar a si mesmo como amigo.");

            if (usuario.isAmigo(amigoLogin)) {
                throw new FriendshipException("Usuário já está adicionado como amigo.");
            }

            if (amigo.getSolicitacoesPendentes().contains(usuario.getLogin())) {
                throw new FriendshipException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
            }

            if (usuario.getSolicitacoesPendentes().contains(amigoLogin)) {
                usuario.confirmarAmizade(amigoLogin);
                amigo.confirmarAmizade(usuario.getLogin());
                usuario.getSolicitacoesPendentes().remove(amigoLogin);
            } else {
                amigo.adicionarSolicitacao(usuario.getLogin());
            }

            salvarDados();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        } catch (FriendshipException e) {
            throw e;
        }
    }

    /**
     * Verifica se dois usuários são amigos mútuos.
     * Retorna true apenas se ambos estiverem na lista de amigos do outro.
     */
    public boolean ehAmigo(String login1, String login2) {
        User user1 = usuarios.get(login1);
        User user2 = usuarios.get(login2);
        if (user1 == null || user2 == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        return user1.isAmigo(login2) && user2.isAmigo(login1);
    }

    /**
     * Permite que um usuário envie uma mensagem para outro.
     * Valida se o destinatário existe e não é o próprio remetente.
     */
    public void enviarRecado(String idSessao, String destinatarioLogin, String mensagem) {
        User remetente = getUsuarioPorSessao(idSessao);
        User destinatario = usuarios.get(destinatarioLogin);

        if (destinatario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        if (remetente.getLogin().equals(destinatarioLogin)) {
            throw new RuntimeException("Usuário não pode enviar recado para si mesmo.");
        }

        destinatario.adicionarRecado(new Message(remetente.getLogin(), mensagem));
        salvarDados();
    }

    /**
     * Recupera a próxima mensagem não lida do usuário.
     * Retorna apenas o conteúdo textual da mensagem.
     */
    public String lerRecado(String idSessao) {
        User usuario = getUsuarioPorSessao(idSessao);
        Message recado = usuario.lerRecado();

        if (recado == null) {
            throw new RuntimeException("Não há recados.");
        }

        return recado.toString();
    }

    /**
     * Persiste o estado atual dos usuários em arquivo.
     * Lança exceção em caso de falha na operação.
     */
    public void salvarDados() {
        try {
            Jackut.save(usuarios);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar dados.");
        }
    }

    /**
     * Reseta completamente o sistema, limpando todos os dados.
     * Remove tanto as estruturas em memória quanto o arquivo de persistência.
     */
    public void zerarSistema() {
        usuarios.clear();
        sessoesAtivas.clear();
        new File(Jackut.DATA_FILE).delete();
    }
}