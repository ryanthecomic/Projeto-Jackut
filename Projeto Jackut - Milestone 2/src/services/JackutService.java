package services;

import entities.*;
import exceptions.*;
import java.util.*;
import java.io.*;

/**
 * Servi�o respons�vel por gerenciar servi�os oferecidos pelo Jackut:
 * comunidades, realcionamentos, persist�ncia de arquivos,
 * sess�es, amizades e mensagens enviadas e recebidas do sistema Jackut.
 */
public class JackutService {

    // Mapa que armazena todos os usu�rios do sistema (login -> User)
    private Map<String, User> usuarios;

    // Mapa que controla as sess�es ativas (idSessao -> login)
    private Map<String, String> sessoesAtivas;

    // Mapa que armazena todas as comunidades do sistema (nome -> Community)
    private Map<String, Community> comunidades = new HashMap<>();

    /**
     * Construtor que inicializa o servi�o carregando dados persistentes.
     * Se n�o existirem dados, inicia com cole��es vazias.
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
     * @param idSessao ID da sess�o do usu�rio criador
     * @param nome Nome �nico da comunidade
     * @param descricao Descri��o da comunidade
     * @throws InvalidSessionException Se a sess�o n�o for v�lida
     * @throws CommunityAlreadyExistsException Se j� existir comunidade com esse nome
     */
    public void criarComunidade(String idSessao, String nome, String descricao)
            throws InvalidSessionException, CommunityAlreadyExistsException {

        // Verifica sess�o v�lida
        User dono = getUsuarioPorSessao(idSessao);
        if (dono == null) {
            throw new InvalidSessionException("Sess�o inv�lida ou expirada.");
        }

        // Verifica se comunidade j� existe
        if (comunidades.containsKey(nome)) {
            throw new CommunityAlreadyExistsException("Comunidade com esse nome j� existe.");
        }

        Community novaComunidade = new Community(nome, descricao, dono);
        comunidades.put(nome, novaComunidade);

        // Adiciona automaticamente ao dono
        dono.adicionarComunidade(nome);
        salvarDados();
    }

    /**
     * Obt�m a descri��o de uma comunidade
     * @param nome Nome da comunidade
     * @return Descri��o da comunidade
     * @throws CommunityNotFoundException Se a comunidade n�o existir
     */
    public String getDescricaoComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade n�o existe.");
        }
        return comunidade.getDescription();
    }

    /**
     * Obt�m o dono de uma comunidade
     * @param nome Nome da comunidade
     * @return Login do dono
     * @throws CommunityNotFoundException Se a comunidade n�o existir
     */
    public String getDonoComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade n�o existe.");
        }
        return comunidade.getOwner().getLogin();
    }

    /**
     * Obt�m os membros de uma comunidade
     * @param nome Nome da comunidade
     * @return Set com logins dos membros
     * @throws CommunityNotFoundException Se a comunidade n�o existir
     */
    public String getMembrosComunidade(String nome) throws CommunityNotFoundException {
        Community comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade n�o existe.");
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
     * Adiciona um usu�rio a uma comunidade existente
     * @param idSessao ID da sess�o do usu�rio
     * @param nomeComunidade Nome da comunidade
     * @throws InvalidSessionException Se a sess�o n�o for v�lida
     * @throws CommunityNotFoundException Se a comunidade n�o existir
     * @throws UserAlreadyInCommunityException Se o usu�rio j� for membro
     */
    public void adicionarUsuarioAComunidade(String idSessao, String nomeComunidade)
            throws InvalidSessionException, CommunityNotFoundException, UserAlreadyInCommunityException {

        User usuario = getUsuarioPorSessao(idSessao);
        if (usuario == null) {
            throw new InvalidSessionException("Sess�o inv�lida ou expirada.");
        }

        Community comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade n�o existe.");
        }

        if (comunidade.getMembers().contains(usuario)) {
            throw new UserAlreadyInCommunityException("Usuario j� faz parte dessa comunidade.");
        }

        // Adiciona o usu�rio � comunidade
        comunidade.addMember(usuario);
        usuario.adicionarComunidade(nomeComunidade);
        salvarDados();
    }

    /**
     * Obt�m todas as comunidades de um usu�rio
     * @param login Login do usu�rio
     * @return Set de nomes de comunidades
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getComunidadesDoUsuario(String login) throws UserNotFoundException {
        User usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        return "{" + String.join(",", usuario.getComunidadesParticipando()) + "}";
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

            // Inimigos n�o podem ser amigos
            if (amigo.ehInimigo(usuario.getLogin())) {
                throw new RelationshipException("Fun��o inv�lida: " + amigo.getNome() + " � seu inimigo.");
            }

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


    public void enviarMensagem(String idSessao, String nomeComunidade, String mensagem)
            throws InvalidSessionException, CommunityNotFoundException {

        User remetente = getUsuarioPorSessao(idSessao);
        if (remetente == null) {
            throw new InvalidSessionException("Sess�o inv�lida ou expirada.");
        }

        Community comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new CommunityNotFoundException("Comunidade n�o existe.");
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
            throw new InvalidSessionException("Sess�o inv�lida ou expirada.");
        }

        CommunityMessage mensagem = usuario.getProximaMensagem();
        if (mensagem == null) {
            throw new NoMessagesException("N�o h� mensagens.");
        }

        usuario.confirmarLeituraMensagem(); // L�gica movida para c�
        return mensagem.toString();
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

        if (destinatario.ehInimigo(remetente.getLogin())) {
            throw new RelationshipException("Fun��o inv�lida: " + destinatario.getNome() + " � seu inimigo.");
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
     * Adiciona um �dolo � lista de um usu�rio (rela��o f�-�dolo).
     * @param idSessao ID da sess�o do usu�rio que est� adicionando o �dolo
     * @param idolo Login do usu�rio a ser adicionado como �dolo
     * @throws InvalidSessionException Se a sess�o for inv�lida
     * @throws UserNotFoundException Se o �dolo n�o for encontrado
     * @throws RelationshipException Se j� existir a rela��o ou for inv�lida
     */
    public void adicionarIdolo(String idSessao, String idolo)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User f� = getUsuarioPorSessao(idSessao);
        User �dolo = usuarios.get(idolo);

        validarRelacionamento(f�, �dolo, "f�-�dolo");

        if (f�.ehFaDe(idolo)) {
            throw new RelationshipException("Usu�rio j� est� adicionado como �dolo.");
        }

        f�.adicionarIdolo(idolo);
        �dolo.adicionarFa(f�.getLogin());
        salvarDados();
    }

    /**
     * Adiciona uma paquera � lista do usu�rio (rela��o unilateral).
     * Envia notifica��o autom�tica se a paquera for m�tua.
     * @param idSessao ID da sess�o do usu�rio
     * @param paquera Login do usu�rio a ser adicionado como paquera
     * @throws InvalidSessionException Se a sess�o for inv�lida
     * @throws UserNotFoundException Se a paquera n�o for encontrada
     * @throws RelationshipException Se j� existir a rela��o ou for inv�lida
     */
    public void adicionarPaquera(String idSessao, String paquera)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User usuario = getUsuarioPorSessao(idSessao);
        User alvo = usuarios.get(paquera);

        validarRelacionamento(usuario, alvo, "paquera");

        if (usuario.ehPaquera(paquera)) {
            throw new RelationshipException("Usu�rio j� est� adicionado como paquera.");
        }

        usuario.adicionarPaquera(paquera);

        // Verifica paquera m�tua
        if (alvo.ehPaquera(usuario.getLogin())) {
            enviarRecadoAutom�tico(usuario, alvo);
        }
        salvarDados();
    }

    /**
     * Adiciona um inimigo � lista do usu�rio (bloqueia rela��es futuras).
     * @param idSessao ID da sess�o do usu�rio
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     * @throws InvalidSessionException Se a sess�o for inv�lida
     * @throws UserNotFoundException Se o inimigo n�o for encontrado
     * @throws RelationshipException Se j� existir a rela��o ou for inv�lida
     */
    public void adicionarInimigo(String idSessao, String inimigo)
            throws InvalidSessionException, UserNotFoundException, RelationshipException {
        User usuario = getUsuarioPorSessao(idSessao);
        User alvo = usuarios.get(inimigo);

        validarRelacionamento(usuario, alvo, "inimigo");

        if (usuario.ehInimigo(inimigo)) {
            throw new RelationshipException("Usu�rio j� est� adicionado como inimigo.");
        }

        usuario.adicionarInimigo(inimigo);
        salvarDados();
    }

    /**
     * Valida se um relacionamento entre usu�rios pode ser estabelecido.
     * @param origem Usu�rio que est� iniciando o relacionamento
     * @param destino Usu�rio alvo do relacionamento
     * @param tipo Tipo de relacionamento ("f�-�dolo", "paquera" ou "inimigo")
     * @throws UserNotFoundException Se o usu�rio destino n�o existir
     * @throws RelationshipException Se o relacionamento for inv�lido (auto-rela��o ou inimigo)
     */
    private void validarRelacionamento(User origem, User destino, String tipo)
            throws UserNotFoundException, RelationshipException {
        if (destino == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        if (origem.getLogin().equals(destino.getLogin())) {
            if (tipo.equals("f�-�dolo")) {
                throw new RelationshipException("Usu�rio n�o pode ser f� de si mesmo.");
            } else {
                throw new RelationshipException("Usu�rio n�o pode ser " + tipo + " de si mesmo.");
            }
        }

        if (destino.ehInimigo(origem.getLogin())) {
            throw new RelationshipException("Fun��o inv�lida: " + destino.getNome() + " � seu inimigo.");
        }
    }

    /**
     * Envia recados autom�ticos para ambos os usu�rios quando h� paquera m�tua.
     * @param usuario1 Primeiro usu�rio na rela��o
     * @param usuario2 Segundo usu�rio na rela��o
     */
    private void enviarRecadoAutom�tico(User usuario1, User usuario2) {
        String msg1 = usuario2.getNome() + " � seu paquera - Recado do Jackut.";
        String msg2 = usuario1.getNome() + " � seu paquera - Recado do Jackut.";

        usuario1.adicionarRecado(new Message("Sistema", msg1));
        usuario2.adicionarRecado(new Message("Sistema", msg2));
    }

    /**
     * Verifica se um usu�rio � f� de outro.
     * @param f� Login do usu�rio f�
     * @param �dolo Login do usu�rio �dolo
     * @return true se a rela��o existir, false caso contr�rio
     */
    public boolean ehFa(String f�, String �dolo) {
        User user = usuarios.get(f�);
        return user != null && user.ehFaDe(�dolo);
    }

    /**
     * Verifica se um usu�rio tem outro como paquera.
     * @param idSessao ID da sess�o do usu�rio
     * @param paquera Login do usu�rio a verificar
     * @return true se for paquera, false caso contr�rio
     * @throws InvalidSessionException Se a sess�o for inv�lida
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
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        String login = usuario.getLogin();

        // 1. Remover de comunidades (completo)
        List<String> comunidadesParaRemover = new ArrayList<>();

        // Identifica comunidades para remover e limpa refer�ncias
        for (Community comunidade : new ArrayList<>(comunidades.values())) {
            if (comunidade.getOwner().getLogin().equals(login)) {
                // Marca comunidades onde � dono para remo��o completa
                comunidadesParaRemover.add(comunidade.getName());

                // Remove a comunidade da lista de todos os membros
                for (User membro : comunidade.getMembers()) {
                    membro.getComunidadesParticipando().remove(comunidade.getName());
                }
            } else {
                // Remove o usu�rio da lista de membros
                comunidade.getMembers().removeIf(m -> m.getLogin().equals(login));

                // Remove a comunidade da lista do usu�rio
                usuario.getComunidadesParticipando().remove(comunidade.getName());
            }
        }

        // Remove as comunidades do mapa principal
        for (String nomeComunidade : comunidadesParaRemover) {
            comunidades.remove(nomeComunidade);
        }

        // 2. Remover relacionamentos com outros usu�rios
        for (User outroUsuario : usuarios.values()) {
            // Remove de amigos
            outroUsuario.getAmigos().remove(login);

            // Remove de f�s/�dolos
            outroUsuario.getIdolos().remove(login);
            outroUsuario.getFas().remove(login);

            // Remove de paqueras
            outroUsuario.getPaqueras().remove(login);

            // Remove de inimigos
            outroUsuario.getInimigos().remove(login);

            // Remove mensagens recebidas
            outroUsuario.getRecados().removeIf(msg -> msg.getRemetente().equals(login));
        }

        // 3. Remover sess�es ativas
        sessoesAtivas.values().removeIf(loginSessao -> loginSessao.equals(login));

        // 4. Remover o usu�rio
        usuarios.remove(login);

        salvarDados();
    }

    //9_1


    /**
     * Reseta completamente o sistema, limpando todos os dados.
     * Remove tanto as estruturas em mem�ria quanto o arquivo de persist�ncia.
     */
    public void zerarSistema() {
        usuarios.clear();
        sessoesAtivas.clear();
        comunidades.clear();
        new File(Jackut.DATA_FILE).delete();
    }
}