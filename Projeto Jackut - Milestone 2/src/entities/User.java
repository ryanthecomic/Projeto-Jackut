package entities;

import exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Esta entidade representa um usuário do sistema Jackut, contendo todas as informações
 * e funcionalidades relacionadas ao perfil, amizades e mensagens.
 *
 * <p>A classe é serializável para permitir persistência dos dados.</p>
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Dados básicos do usuário
    private final String login;
    private final String senha;
    private final String nome;
    private Profile profile;

    // Relacionamentos avançados (US8_1)
    private Set<String> idolos = new HashSet<>();   // Quem eu sigo (sou fã)
    private Set<String> fas = new HashSet<>();      // Meus fãs
    private Set<String> paqueras = new HashSet<>(); // Minhas paqueras (privado)
    private Set<String> inimigos = new HashSet<>(); // Meus inimigos

    /**
     * Adiciona um ídolo à lista do usuário.
     * @param idolo Login do usuário a ser adicionado como ídolo
     */
    public void adicionarIdolo(String idolo) {
        idolos.add(idolo);
    }

    /**
     * Adiciona um fã à lista do usuário.
     * @param fa Login do usuário a ser adicionado como fã
     */
    public void adicionarFa(String fa) {
        fas.add(fa);
    }

    /**
     * Verifica se o usuário é fã de outro usuário.
     * @param idolo Login do usuário a verificar
     * @return true se for fã, false caso contrário
     */
    public boolean ehFaDe(String idolo) {
        return idolos.contains(idolo);
    }

    /**
     * Adiciona uma paquera à lista do usuário.
     * @param paquera Login do usuário a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        paqueras.add(paquera);
    }

    /**
     * Verifica se o usuário tem outro usuário como paquera.
     * @param paquera Login do usuário a verificar
     * @return true se for paquera, false caso contrário
     */
    public boolean ehPaquera(String paquera) {
        return paqueras.contains(paquera);
    }

    /**
     * Adiciona um inimigo à lista do usuário.
     * @param inimigo Login do usuário a ser adicionado como inimigo
     */
    public void adicionarInimigo(String inimigo) {
        inimigos.add(inimigo);
    }

    /**
     * Verifica se o usuário tem outro usuário como inimigo.
     * @param inimigo Login do usuário a verificar
     * @return true se for inimigo, false caso contrário
     */
    public boolean ehInimigo(String inimigo) {
        return inimigos.contains(inimigo);
    }

    /**
     * @return Cópia defensiva da lista de ídolos do usuário
     */
    public Set<String> getIdolos() {
        return new HashSet<>(idolos);
    }

    /**
     * @return Cópia defensiva da lista de fãs do usuário
     */
    public Set<String> getFas() {
        return new HashSet<>(fas);
    }

    /**
     * @return Cópia defensiva da lista de paqueras do usuário
     */
    public Set<String> getPaqueras() {
        return new HashSet<>(paqueras);
    }

    /**
     * @return Cópia defensiva da lista de inimigos do usuário
     */
    public Set<String> getInimigos() {
        return new HashSet<>(inimigos);
    }

    // Relacionamentos básicos
    private final LinkedHashSet<String> amigos = new LinkedHashSet<>();
    private final LinkedHashSet<String> solicitacoesPendentes = new LinkedHashSet<>();
    private Queue<Message> recados = new LinkedList<>();
    private List<String> comunidadesParticipando = new ArrayList<>();

    /**
     * Adiciona o usuário a uma comunidade.
     * @param nomeComunidade Nome da comunidade a ser adicionada
     */
    public void adicionarComunidade(String nomeComunidade) {
        if (!comunidadesParticipando.contains(nomeComunidade)) {
            comunidadesParticipando.add(nomeComunidade);
        }
    }

    /**
     * @return Cópia defensiva da lista de comunidades do usuário
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
     * Visualiza a próxima mensagem sem removê-la da fila.
     * @return Próxima mensagem ou null se não houver
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
     * Lê e remove a próxima mensagem da fila.
     * @return Mensagem lida ou null se não houver
     */
    public CommunityMessage lerMensagem() {
        return mensagens.poll();
    }

    /**
     * Constrói um novo usuário com os dados básicos.
     * @param login Identificador único do usuário
     * @param senha Senha de acesso
     * @param nome Nome de exibição do usuário
     */
    public User(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.profile = new Profile();
    }

    // Getters básicos
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
    public Profile getProfile() { return profile; }

    /**
     * Adiciona uma solicitação de amizade pendente.
     * @param loginAmigo Login do usuário solicitante
     */
    public void adicionarSolicitacao(String loginAmigo) {
        solicitacoesPendentes.add(loginAmigo);
    }

    /**
     * Confirma uma amizade pendente.
     * @param loginAmigo Login do usuário a ser confirmado
     */
    public void confirmarAmizade(String loginAmigo) {
        solicitacoesPendentes.remove(loginAmigo);
        amigos.add(loginAmigo);
    }

    /**
     * Verifica se um usuário é amigo.
     * @param loginAmigo Login do usuário a verificar
     * @return true se for amigo, false caso contrário
     */
    public boolean isAmigo(String loginAmigo) {
        return amigos.contains(loginAmigo);
    }

    /**
     * @return Cópia defensiva da lista de amigos
     */
    public LinkedHashSet<String> getAmigos() {
        return new LinkedHashSet<>(amigos);
    }

    /**
     * Adiciona um novo recado à fila.
     * @param recado Mensagem a ser adicionada
     */
    public void adicionarRecado(Message recado) {
        recados.add(recado);
    }

    /**
     * Lê e remove o próximo recado da fila.
     * @return Recado lido
     * @throws NoMessagesException Se não houver recados
     */
    public Message lerRecado() throws NoMessagesException {
        if (recados.isEmpty()) {
            throw new NoMessagesException("Não há recados.");
        }
        return recados.poll();
    }

    /**
     * @return Cópia defensiva das solicitações de amizade pendentes
     */
    public Set<String> getSolicitacoesPendentes() {
        return new LinkedHashSet<>(solicitacoesPendentes);
    }

    /**
     * @return Cópia defensiva da fila de recados
     */
    public Queue<Message> getRecados() {
        return new LinkedList<>(recados);
    }
}