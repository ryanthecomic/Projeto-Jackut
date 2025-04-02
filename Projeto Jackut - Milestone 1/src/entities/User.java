package entities;

import java.io.Serializable;
import java.util.LinkedHashSet; // Mapa de strings linkados, para amigos adicionados na ordem correta
import java.util.Queue; // Fila de recados
import java.util.Set; // Mapa de strings para solicita��es
import java.util.LinkedList; // Para lista de recados

/**
 * Esta entidade representa um usu�rio do sistema Jackut, contendo todas as informa��es
 * e funcionalidades relacionadas ao perfil, amizades e mensagens.
 *
 * <p>A classe � serializ�vel para permitir persist�ncia dos dados.</p>
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String senha;
    private final String nome;
    private Profile profile;

    /** Conjunto de amigos do usu�rio, mantendo a ordem de confirma��o */
    private final LinkedHashSet<String> amigos = new LinkedHashSet<>();

    /** Solicita��es de amizade pendentes */
    private final LinkedHashSet<String> solicitacoesPendentes = new LinkedHashSet<>();

    /** Fila de mensagens recebidas (estrutura FIFO) */
    private final Queue<Message> recados = new LinkedList<>();

    /**
     * Constr�i um novo usu�rio com os dados b�sicos.
     *
     * @param login Identificador �nico do usu�rio
     * @param senha Senha de acesso
     * @param nome Nome de exibi��o do usu�rio
     */
    public User(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.profile = new Profile();
    }

    // Getters b�sicos

    /**
     * @return O login do usu�rio
     */
    public String getLogin() { return login; }

    /**
     * @return A senha do usu�rio
     */
    public String getSenha() { return senha; }

    /**
     * @return O nome de exibi��o do usu�rio
     */
    public String getNome() { return nome; }

    /**
     * @return O perfil do usu�rio
     */
    public Profile getProfile() { return profile; }

    // M�todos para gerenciar amizades

    /**
     * Adiciona uma nova solicita��o de amizade.
     *
     * @param loginAmigo Login do usu�rio que solicitou amizade
     */
    public void adicionarSolicitacao(String loginAmigo) {
        solicitacoesPendentes.add(loginAmigo);
    }

    /**
     * Confirma uma amizade, adicionando o usu�rio � lista de amigos.
     *
     * @param loginAmigo Login do usu�rio a ser adicionado como amigo
     */
    public void confirmarAmizade(String loginAmigo) {
        solicitacoesPendentes.remove(loginAmigo);
        amigos.add(loginAmigo); // LinkedHashSet mant�m a ordem de inser��o
    }

    /**
     * Verifica se um usu�rio � amigo.
     *
     * @param loginAmigo Login do usu�rio a verificar
     * @return true se for amigo, false caso contr�rio
     */
    public boolean isAmigo(String loginAmigo) {
        return amigos.contains(loginAmigo);
    }

    /**
     * Retorna a lista de amigos na ordem de confirma��o.
     *
     * @return C�pia defensiva do conjunto de amigos
     */
    public LinkedHashSet<String> getAmigos() {
        return new LinkedHashSet<>(amigos);
    }

    // M�todos para gerenciar recados

    /**
     * Adiciona um novo recado � fila do usu�rio.
     *
     * @param recado Mensagem a ser adicionada
     */
    public void adicionarRecado(Message recado) {
        recados.add(recado);
    }

    /**
     * L� e remove o pr�ximo recado da fila (FIFO).
     *
     * @return O pr�ximo recado ou null se n�o houver
     */
    public Message lerRecado() {
        return recados.poll();
    }

    /**
     * Verifica se existem recados n�o lidos.
     *
     * @return true se houver recados, false caso contr�rio
     */
    public boolean temRecados() {
        return !recados.isEmpty();
    }

    /**
     * Retorna as solicita��es de amizade pendentes.
     *
     * @return C�pia defensiva do conjunto de solicita��es
     */
    public Set<String> getSolicitacoesPendentes() {
        return new LinkedHashSet<>(solicitacoesPendentes);
    }
}