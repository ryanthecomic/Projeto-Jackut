package services;

import entities.*;
import exceptions.*;
import java.util.*;
import java.io.*;

/**
 * Serviço responsável por gerenciar serviços oferecidos pelo Jackut:
 * comunidades, realcionamentos, persistência de arquivos,
 * sessões, amizades e mensagens enviadas e recebidas do sistema Jackut.
 */
public class JackutService {

    // Mapa que armazena todos os usuários do sistema (login -> User)
    private Map<String, User> usuarios;

    // Mapa que controla as sessões ativas (idSessao -> login)
    private Map<String, String> sessoesAtivas;

    // Mapa que armazena todas as comunidades do sistema (nome -> Community)
    private Map<String, Community> comunidades = new HashMap<>();

    /**
     * Construtor que inicializa o serviço carregando dados persistentes.
     * Se não existirem dados, inicia com coleções vazias.
     */
    public JackutService() {
        Object loadedData = Jackut.load();
        if (loadedData instanceof Map) {
            Map<String, Object> dados = (Map<String, Object>) loadedData;
            this.usuarios = (Map<String, User>) dados.get("usuarios");
            this.comunidades = (Map<String, Community>) dados.get("comunidades");
            this.sessoesAtivas = new HashMap<>();
        } else {
            this.usuarios = new HashMap<>();
            this.comunidades = new HashMap<>();
            this.sessoesAtivas = new HashMap<>();
        }
    }

    /**
     * Cria uma nova comunidade no sistema.
     * @param idSessao ID da sessão do usuário criador
     * @param nome Nome único da comunidade
     * @param descricao Descrição da comunidade
     * @throws InvalidSessionException Se a sessão não for válida
     * @throws CommunityAlreadyExistsException Se já existir comunidade com esse nome
     */
    public void criarComunidade(String idSessao, String nome, String descricao)
            throws InvalidSessionException, CommunityAlreadyExistsException {

        // Verifica sessão válida
        User dono = getUsuarioPorSessao(idSessao);
        if (dono == null) {
            throw new InvalidSessionException("Sessão inválida ou expirada.");
        }

        // Verifica se comunidade já existe
        if (comunidades.containsKey(nome)) {
            throw new CommunityAlreadyExistsException("Comunidade com esse nome já existe.");
        }

        Community novaComunidade = new Community(nome, descricao, dono);
        comunidades.put(nome, novaComunidade);

        // Adiciona automaticamente ao dono
        dono.adicionarComunidade(nome);
        salvarDados();
    }

    /**
     * Obtém a descrição de uma comunidade
     * @param nome Nome da comunidade
     * @return Descrição da comunidade
     * @throws CommunityNotFoundException Se a comunidade não existir
     */
    public String getDescricaoComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade não existe.");
        }
        return comunidade.getDescription();
    }

    /**
     * Obtém o dono de uma comunidade
     * @param nome Nome da comunidade
     * @return Login do dono
     * @throws CommunityNotFoundException Se a comunidade não existir
     */
    public String getDonoComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade não existe.");
        }
        return comunidade.getOwner().getLogin();
    }

    /**
     * Obtém os membros de uma comunidade
     * @param nome Nome da comunidade
     * @return Set com logins dos membros
     * @throws CommunityNotFoundException Se a comunidade não existir
     */
    public String getMembrosComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade não existe.");
        }

        List<String> membros = new ArrayList<>();

        membros.add(comunidade.getOwner().getLogin());

        for (User membro : comunidade.getMembers()) {
            if (!membro.getLogin().equals(comunidade.getOwner().getLogin())) {
                membros.add(membro.getLogin());
            }
        }

        return "{" + String.join(",", membros) + "}";
    }

    /**
     * Adiciona um usuário a uma comunidade existente
     * @param idSessao ID da sessão do usuário
     * @param nomeComunidade Nome da comunidade
     * @throws InvalidSessionException Se a sessão não for válida
     * @throws CommunityNotFoundException Se a comunidade não existir
     * @throws UserAlreadyInCommunityException Se o usuário já for membro
     */
    public void adicionarUsuarioAComunidade(String idSessao, String nomeComunidade)
            throws InvalidSessionException, CommunityNotFoundException, UserAlreadyInCommunityException {

        User usuario = getUsuarioPorSessao(idSessao);
        if (usuario == null) {
            throw new InvalidSessionException("Sessão inválida ou expirada.");
        }

        Community comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade não existe.");
        }

        if (comunidade.getMembers().contains(usuario)) {
            throw new UserAlreadyInCommunityException("Usuario já faz parte dessa comunidade.");
        }

        // Adiciona o usuário à comunidade
        comunidade.addMember(usuario);
        usuario.adicionarComunidade(nomeComunidade);
        salvarDados();
    }

    /**
     * Obtém todas as comunidades de um usuário
     * @param login Login do usuário
     * @return Set de nomes de comunidades
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getComunidadesDoUsuario(String login) throws UserNotFoundException {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        return "{" + String.join(",", usuario.getComunidadesParticipando()) + "}";
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

            // Inimigos não podem ser amigos
            if (amigo.ehInimigo(usuario.getLogin())) {
                throw new RelationshipException("Função inválida: " + amigo.getNome() + " é seu inimigo.");
            }

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


    public void enviarMensagem(String idSessao, String nomeComunidade, String mensagem)
            throws InvalidSessionException, CommunityNotFoundException {

        User remetente = getUsuarioPorSessao(idSessao);
        if (remetente == null) {
            throw new InvalidSessionException("Sessão inválida ou expirada.");
        }

        Community comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade não existe.");
        }

        CommunityMessage msg = new CommunityMessage(
                nomeComunidade,
                remetente.getLogin(),
                mensagem
        );

        // Envia para todos os membros (incluindo o remetente)
        for (User membro : comunidade.getMembers()) {
            membro.receberMensagem(msg);
        }

        salvarDados();
    }


    public String lerMensagem(String idSessao) throws InvalidSessionException, NoMessagesException {
        User usuario = getUsuarioPorSessao(idSessao);
        if (usuario == null) {
            throw new InvalidSessionException("Sessão inválida ou expirada.");
        }

        CommunityMessage mensagem = usuario.getProximaMensagem();
        if (mensagem == null) {
            throw new NoMessagesException("Não há mensagens.");
        }

        usuario.confirmarLeituraMensagem(); // Lógica movida para cá
        return mensagem.toString();
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

        if (destinatario.ehInimigo(remetente.getLogin())) {
            throw new RelationshipException("Função inválida: " + destinatario.getNome() + " é seu inimigo.");
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
            Map<String, Object> dados = new HashMap<>();
            dados.put("usuarios", usuarios);
            dados.put("comunidades", comunidades);
            Jackut.save(dados);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar dados.");
        }
    }

    //US8_1

    /**
     * Adiciona um ídolo à lista de um usuário (relação fã-ídolo).
     * @param idSessao ID da sessão do usuário que está adicionando o ídolo
     * @param idolo Login do usuário a ser adicionado como ídolo
     * @throws InvalidSessionException Se a sessão for inválida
     * @throws UserNotFoundException Se o ídolo não for encontrado
     * @throws RelationshipException Se já existir a relação ou for inválida
     */
    public void adicionarIdolo(String idSessao, String idolo)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User fã = getUsuarioPorSessao(idSessao);
        User ídolo = usuarios.get(idolo);

        validarRelacionamento(fã, ídolo, "fã-ídolo");

        if (fã.ehFaDe(idolo)) {
            throw new RelationshipException("Usuário já está adicionado como ídolo.");
        }

        fã.adicionarIdolo(idolo);
        ídolo.adicionarFa(fã.getLogin());
        salvarDados();
    }

    /**
     * Adiciona uma paquera à lista do usuário (relação unilateral).
     * Envia notificação automática se a paquera for mútua.
     * @param idSessao ID da sessão do usuário
     * @param paquera Login do usuário a ser adicionado como paquera
     * @throws InvalidSessionException Se a sessão for inválida
     * @throws UserNotFoundException Se a paquera não for encontrada
     * @throws RelationshipException Se já existir a relação ou for inválida
     */
    public void adicionarPaquera(String idSessao, String paquera)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User usuario = getUsuarioPorSessao(idSessao);
        User alvo = usuarios.get(paquera);

        validarRelacionamento(usuario, alvo, "paquera");

        if (usuario.ehPaquera(paquera)) {
            throw new RelationshipException("Usuário já está adicionado como paquera.");
        }

        usuario.adicionarPaquera(paquera);

        // Verifica paquera mútua
        if (alvo.ehPaquera(usuario.getLogin())) {
            enviarRecadoAutomático(usuario, alvo);
        }
        salvarDados();
    }

    /**
     * Adiciona um inimigo à lista do usuário (bloqueia relações futuras).
     * @param idSessao ID da sessão do usuário
     * @param inimigo Login do usuário a ser adicionado como inimigo
     * @throws InvalidSessionException Se a sessão for inválida
     * @throws UserNotFoundException Se o inimigo não for encontrado
     * @throws RelationshipException Se já existir a relação ou for inválida
     */
    public void adicionarInimigo(String idSessao, String inimigo)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User usuario = getUsuarioPorSessao(idSessao);
        User alvo = usuarios.get(inimigo);

        validarRelacionamento(usuario, alvo, "inimigo");

        if (usuario.ehInimigo(inimigo)) {
            throw new RelationshipException("Usuário já está adicionado como inimigo.");
        }

        usuario.adicionarInimigo(inimigo);
        salvarDados();
    }

    /**
     * Valida se um relacionamento entre usuários pode ser estabelecido.
     * @param origem Usuário que está iniciando o relacionamento
     * @param destino Usuário alvo do relacionamento
     * @param tipo Tipo de relacionamento ("fã-ídolo", "paquera" ou "inimigo")
     * @throws UserNotFoundException Se o usuário destino não existir
     * @throws RelationshipException Se o relacionamento for inválido (auto-relação ou inimigo)
     */
    private void validarRelacionamento(User origem, User destino, String tipo)
            throws UserNotFoundException, RelationshipException {
        if (destino == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        if (origem.getLogin().equals(destino.getLogin())) {
            if (tipo.equals("fã-ídolo")) {
                throw new RelationshipException("Usuário não pode ser fã de si mesmo.");
            } else {
                throw new RelationshipException("Usuário não pode ser " + tipo + " de si mesmo.");
            }
        }

        if (destino.ehInimigo(origem.getLogin())) {
            throw new RelationshipException("Função inválida: " + destino.getNome() + " é seu inimigo.");
        }
    }

    /**
     * Envia recados automáticos para ambos os usuários quando há paquera mútua.
     * @param usuario1 Primeiro usuário na relação
     * @param usuario2 Segundo usuário na relação
     */
    private void enviarRecadoAutomático(User usuario1, User usuario2) {
        String msg1 = usuario2.getNome() + " é seu paquera - Recado do Jackut.";
        String msg2 = usuario1.getNome() + " é seu paquera - Recado do Jackut.";

        usuario1.adicionarRecado(new Message("Sistema", msg1));
        usuario2.adicionarRecado(new Message("Sistema", msg2));
    }

    /**
     * Verifica se um usuário é fã de outro.
     * @param fã Login do usuário fã
     * @param ídolo Login do usuário ídolo
     * @return true se a relação existir, false caso contrário
     */
    public boolean ehFa(String fã, String ídolo) {
        User user = usuarios.get(fã);
        return user != null && user.ehFaDe(ídolo);
    }

    /**
     * Verifica se um usuário tem outro como paquera.
     * @param idSessao ID da sessão do usuário
     * @param paquera Login do usuário a verificar
     * @return true se for paquera, false caso contrário
     * @throws InvalidSessionException Se a sessão for inválida
     */
    public boolean ehPaquera(String idSessao, String paquera) throws InvalidSessionException {
        User user = getUsuarioPorSessao(idSessao);
        return user.ehPaquera(paquera);
    }

    public String getFas(String login) {
        User user = usuarios.get(login);
        return user != null ? "{" + String.join(",", user.getFas()) + "}" : "{}";
    }

    public String getPaqueras(String idSessao) throws InvalidSessionException {
        User user = getUsuarioPorSessao(idSessao);
        return "{" + String.join(",", user.getPaqueras()) + "}";
    }

    public void removerUsuario(String idSessao) throws InvalidSessionException, UserNotFoundException {
        User usuario = getUsuarioPorSessao(idSessao);
        if (usuario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String login = usuario.getLogin();

        // 1. Remover de comunidades (completo)
        List<String> comunidadesParaRemover = new ArrayList<>();

        // Identifica comunidades para remover e limpa referências
        for (Community comunidade : new ArrayList<>(comunidades.values())) {
            if (comunidade.getOwner().getLogin().equals(login)) {
                // Marca comunidades onde é dono para remoção completa
                comunidadesParaRemover.add(comunidade.getName());

                // Remove a comunidade da lista de todos os membros
                for (User membro : comunidade.getMembers()) {
                    membro.getComunidadesParticipando().remove(comunidade.getName());
                }
            } else {
                // Remove o usuário da lista de membros
                comunidade.getMembers().removeIf(m -> m.getLogin().equals(login));

                // Remove a comunidade da lista do usuário
                usuario.getComunidadesParticipando().remove(comunidade.getName());
            }
        }

        // Remove as comunidades do mapa principal
        for (String nomeComunidade : comunidadesParaRemover) {
            comunidades.remove(nomeComunidade);
        }

        // 2. Remover relacionamentos com outros usuários
        for (User outroUsuario : usuarios.values()) {
            // Remove de amigos
            outroUsuario.getAmigos().remove(login);

            // Remove de fãs/ídolos
            outroUsuario.getIdolos().remove(login);
            outroUsuario.getFas().remove(login);

            // Remove de paqueras
            outroUsuario.getPaqueras().remove(login);

            // Remove de inimigos
            outroUsuario.getInimigos().remove(login);

            // Remove mensagens recebidas
            outroUsuario.getRecados().removeIf(msg -> msg.getRemetente().equals(login));
        }

        // 3. Remover sessões ativas
        sessoesAtivas.values().removeIf(loginSessao -> loginSessao.equals(login));

        // 4. Remover o usuário
        usuarios.remove(login);

        salvarDados();
    }

    //9_1


    /**
     * Reseta completamente o sistema, limpando todos os dados.
     * Remove tanto as estruturas em memória quanto o arquivo de persistência.
     */
    public void zerarSistema() {
        usuarios.clear();
        sessoesAtivas.clear();
        comunidades.clear();
        new File(Jackut.DATA_FILE).delete();
    }
}