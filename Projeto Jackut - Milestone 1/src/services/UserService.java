package services;

import entities.*;
import exceptions.*;
import java.util.*;
import java.io.*;

/**
 * Servi�o respons�vel por gerenciar tudo que envolve usu�rios:
 * sess�es, amizades e mensagens enviadas e recebidas do sistema Jackut.
 */
public class UserService {

    // Mapa que armazena todos os usu�rios do sistema (login -> User)
    private Map<String, User> usuarios;

    // Mapa que controla as sess�es ativas (idSessao -> login)
    private Map<String, String> sessoesAtivas;

    /**
     * Construtor que inicializa o servi�o carregando dados persistentes.
     * Se n�o existirem dados, inicia com cole��es vazias.
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
     * Cria um novo usu�rio no sistema ap�s validar os par�metros.
     * Lan�a exce��es espec�ficas para casos de erro.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null || login.isBlank()) {
            throw new RuntimeException("Login inv�lido.");
        }
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Senha inv�lida.");
        }
        if (usuarios.containsKey(login)) {
            throw new UserAlreadyExistsException("Conta com esse nome j� existe.");
        }

        usuarios.put(login, new User(login, senha, nome));
        salvarDados();
    }

    /**
     * Autentica um usu�rio e cria uma nova sess�o.
     * Retorna um ID �nico de sess�o para uso futuro.
     */
    public String abrirSessao(String login, String senha) {
        User usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Login ou senha inv�lidos.");
        }
        String idSessao = UUID.randomUUID().toString();
        sessoesAtivas.put(idSessao, login);
        return idSessao;
    }

    /**
     * Recupera um atributo espec�fico do perfil do usu�rio.
     * Trata separadamente o atributo especial 'nome'.
     */
    public String getAtributoUsuario(String login, String atributo) {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        if ("nome".equalsIgnoreCase(atributo)) {
            return usuario.getNome();
        }

        String valor = usuario.getProfile().getAttribute(atributo);
        if (valor.isEmpty()) {
            throw new AttributeNotFilledException("Atributo n�o preenchido.");
        }
        return valor;
    }

    /**
     * Edita um atributo do perfil do usu�rio autenticado.
     * Requer sess�o v�lida e persiste as altera��es.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        User usuario = getUsuarioPorSessao(idSessao);
        usuario.getProfile().setAttribute(atributo, valor);
        salvarDados();
    }

    /**
     * M�todo interno para obter usu�rio a partir de um ID de sess�o.
     * Valida se a sess�o existe antes de retornar.
     */
    private User getUsuarioPorSessao(String idSessao) {
        String login = sessoesAtivas.get(idSessao);
        if (login == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }
        return usuarios.get(login);
    }

    /**
     * Retorna a lista de amigos de um usu�rio.
     * Mant�m a ordem de confirma��o das amizades.
     */
    public LinkedHashSet<String> getAmigos(String login) {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }
        return usuario.getAmigos();
    }

    /**
     * Gerencia o processo de adi��o de amigos com todas as valida��es necess�rias.
     * Trata tanto solicita��es novas quanto confirma��es de amizade.
     */
    public void adicionarAmigo(String idSessao, String amigoLogin) {
        try {
            User usuario = getUsuarioPorSessao(idSessao);
            User amigo = usuarios.get(amigoLogin);

            if (amigo == null) throw new UserNotFoundException("Usu�rio n�o cadastrado.");
            if (usuario.getLogin().equals(amigoLogin))
                throw new FriendshipException("Usu�rio n�o pode adicionar a si mesmo como amigo.");

            if (usuario.isAmigo(amigoLogin)) {
                throw new FriendshipException("Usu�rio j� est� adicionado como amigo.");
            }

            if (amigo.getSolicitacoesPendentes().contains(usuario.getLogin())) {
                throw new FriendshipException("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
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
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        } catch (FriendshipException e) {
            throw e;
        }
    }

    /**
     * Verifica se dois usu�rios s�o amigos m�tuos.
     * Retorna true apenas se ambos estiverem na lista de amigos do outro.
     */
    public boolean ehAmigo(String login1, String login2) {
        User user1 = usuarios.get(login1);
        User user2 = usuarios.get(login2);
        if (user1 == null || user2 == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }
        return user1.isAmigo(login2) && user2.isAmigo(login1);
    }

    /**
     * Permite que um usu�rio envie uma mensagem para outro.
     * Valida se o destinat�rio existe e n�o � o pr�prio remetente.
     */
    public void enviarRecado(String idSessao, String destinatarioLogin, String mensagem) {
        User remetente = getUsuarioPorSessao(idSessao);
        User destinatario = usuarios.get(destinatarioLogin);

        if (destinatario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }
        if (remetente.getLogin().equals(destinatarioLogin)) {
            throw new RuntimeException("Usu�rio n�o pode enviar recado para si mesmo.");
        }

        destinatario.adicionarRecado(new Message(remetente.getLogin(), mensagem));
        salvarDados();
    }

    /**
     * Recupera a pr�xima mensagem n�o lida do usu�rio.
     * Retorna apenas o conte�do textual da mensagem.
     */
    public String lerRecado(String idSessao) {
        User usuario = getUsuarioPorSessao(idSessao);
        Message recado = usuario.lerRecado();

        if (recado == null) {
            throw new RuntimeException("N�o h� recados.");
        }

        return recado.toString();
    }

    /**
     * Persiste o estado atual dos usu�rios em arquivo.
     * Lan�a exce��o em caso de falha na opera��o.
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
     * Remove tanto as estruturas em mem�ria quanto o arquivo de persist�ncia.
     */
    public void zerarSistema() {
        usuarios.clear();
        sessoesAtivas.clear();
        new File(Jackut.DATA_FILE).delete();
    }
}