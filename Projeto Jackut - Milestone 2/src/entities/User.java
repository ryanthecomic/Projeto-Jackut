package entities;

import exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Esta entidade representa um usu�rio do sistema Jackut, contendo todas as informa��es
 * e funcionalidades relacionadas ao perfil, amizades e mensagens.
 *
 * <p>A classe � serializ�vel para permitir persist�ncia dos dados.</p>
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Dados b�sicos do usu�rio
    private final String login;
    private final String senha;
    private final String nome;
    private Profile profile;

    // Relacionamentos avan�ados (US8_1)
    private Set<String> idolos = new HashSet<>();   // Quem eu sigo (sou f�)
    private Set<String> fas = new HashSet<>();      // Meus f�s
    private Set<String> paqueras = new HashSet<>(); // Minhas paqueras (privado)
    private Set<String> inimigos = new HashSet<>(); // Meus inimigos

    /**
     * Adiciona um �dolo � lista do usu�rio.
     * @param idolo Login do usu�rio a ser adicionado como �dolo
     */
    public void adicionarIdolo(String idolo) {
        idolos.add(idolo);
    }

    /**
     * Adiciona um f� � lista do usu�rio.
     * @param fa Login do usu�rio a ser adicionado como f�
     */
    public void adicionarFa(String fa) {
        fas.add(fa);
    }

    /**
     * Verifica se o usu�rio � f� de outro usu�rio.
     * @param idolo Login do usu�rio a verificar
     * @return true se for f�, false caso contr�rio
     */
    public boolean ehFaDe(String idolo) {
        return idolos.contains(idolo);
    }

    /**
     * Adiciona uma paquera � lista do usu�rio.
     * @param paquera Login do usu�rio a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        paqueras.add(paquera);
    }

    /**
     * Verifica se o usu�rio tem outro usu�rio como paquera.
     * @param paquera Login do usu�rio a verificar
     * @return true se for paquera, false caso contr�rio
     */
    public boolean ehPaquera(String paquera) {
        return paqueras.contains(paquera);
    }

    /**
     * Adiciona um inimigo � lista do usu�rio.
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     */
    public void adicionarInimigo(String inimigo) {
        inimigos.add(inimigo);
    }

    /**
     * Verifica se o usu�rio tem outro usu�rio como inimigo.
     * @param inimigo Login do usu�rio a verificar
     * @return true se for inimigo, false caso contr�rio
     */
    public boolean ehInimigo(String inimigo) {
        return inimigos.contains(inimigo);
    }

    /**
     * @return C�pia defensiva da lista de �dolos do usu�rio
     */
    public Set<String> getIdolos() {
        return new HashSet<>(idolos);
    }

    /**
     * @return C�pia defensiva da lista de f�s do usu�rio
     */
    public Set<String> getFas() {
        return new HashSet<>(fas);
    }

    /**
     * @return C�pia defensiva da lista de paqueras do usu�rio
     */
    public Set<String> getPaqueras() {
        return new HashSet<>(paqueras);
    }

    /**
     * @return C�pia defensiva da lista de inimigos do usu�rio
     */
    public Set<String> getInimigos() {
        return new HashSet<>(inimigos);
    }

    // Relacionamentos b�sicos
    private final LinkedHashSet<String> amigos = new LinkedHashSet<>();
    private final LinkedHashSet<String> solicitacoesPendentes = new LinkedHashSet<>();
    private Queue<Message> recados = new LinkedList<>();
    private List<String> comunidadesParticipando = new ArrayList<>();

    /**
     * Adiciona o usu�rio a uma comunidade.
     * @param nomeComunidade Nome da comunidade a ser adicionada
     */
    public void adicionarComunidade(String nomeComunidade) {
        if (!comunidadesParticipando.contains(nomeComunidade)) {
            comunidadesParticipando.add(nomeComunidade);
        }
    }

    /**
     * @return C�pia defensiva da lista de comunidades do usu�rio
     */
    public List<String> getComunidadesParticipando() {
        return new ArrayList<>(comunidadesParticipando);
    }

    // Mensagens de comunidade
    private Queue<CommunityMessage> mensagens = new LinkedList<>();
    private Queue<CommunityMessage> mensagensLidas = new LinkedList<>();

    /**
     * Recebe uma mensagem de comunidade.
     * @param mensagem Mensagem a ser adicionada na fila
     */
    public void receberMensagem(CommunityMessage mensagem) {
        mensagens.add(mensagem);
    }

    /**
     * Visualiza a pr�xima mensagem sem remov�-la da fila.
     * @return Pr�xima mensagem ou null se n�o houver
     */
    public CommunityMessage getProximaMensagem() {
        return mensagens.peek();
    }

    /**
     * Confirma a leitura da mensagem, movendo para a fila de lidas.
     */
    public void confirmarLeituraMensagem() {
        mensagensLidas.add(mensagens.poll());
    }

    /**
     * L� e remove a pr�xima mensagem da fila.
     * @return Mensagem lida ou null se n�o houver
     */
    public CommunityMessage lerMensagem() {
        return mensagens.poll();
    }

    /**
     * Constr�i um novo usu�rio com os dados b�sicos.
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
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
    public Profile getProfile() { return profile; }

    /**
     * Adiciona uma solicita��o de amizade pendente.
     * @param loginAmigo Login do usu�rio solicitante
     */
    public void adicionarSolicitacao(String loginAmigo) {
        solicitacoesPendentes.add(loginAmigo);
    }

    /**
     * Confirma uma amizade pendente.
     * @param loginAmigo Login do usu�rio a ser confirmado
     */
    public void confirmarAmizade(String loginAmigo) {
        solicitacoesPendentes.remove(loginAmigo);
        amigos.add(loginAmigo);
    }

    /**
     * Verifica se um usu�rio � amigo.
     * @param loginAmigo Login do usu�rio a verificar
     * @return true se for amigo, false caso contr�rio
     */
    public boolean isAmigo(String loginAmigo) {
        return amigos.contains(loginAmigo);
    }

    /**
     * @return C�pia defensiva da lista de amigos
     */
    public LinkedHashSet<String> getAmigos() {
        return new LinkedHashSet<>(amigos);
    }

    /**
     * Adiciona um novo recado � fila.
     * @param recado Mensagem a ser adicionada
     */
    public void adicionarRecado(Message recado) {
        recados.add(recado);
    }

    /**
     * L� e remove o pr�ximo recado da fila.
     * @return Recado lido
     * @throws NoMessagesException Se n�o houver recados
     */
    public Message lerRecado() throws NoMessagesException {
        if (recados.isEmpty()) {
            throw new NoMessagesException("N�o h� recados.");
        }
        return recados.poll();
    }

    /**
     * @return C�pia defensiva das solicita��es de amizade pendentes
     */
    public Set<String> getSolicitacoesPendentes() {
        return new LinkedHashSet<>(solicitacoesPendentes);
    }

    /**
     * @return C�pia defensiva da fila de recados
     */
    public Queue<Message> getRecados() {
        return new LinkedList<>(recados);
    }
}