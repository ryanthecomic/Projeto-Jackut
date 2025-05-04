package facade;

import exceptions.*;
import services.JackutService;
import java.util.*;

/**
 * Fachada principal do sistema Jackut que expõe as operações para o EasyAccept.
 * Traduz chamadas dos testes para operações internas do sistema.
 */
public class JackutFacade {
    private final JackutService jackutService = new JackutService();

    /**
     * Reinicia o sistema, removendo todos os usuários e dados persistentes.
     * Corresponde ao comando ##zerarSistema nos testes.
     */
    public void zerarSistema() {
        jackutService.zerarSistema();
    }

    /**
     * Cria um novo usuário no sistema.
     * @param login Identificador único do usuário
     * @param senha Senha de acesso
     * @param nome Nome de exibição do usuário
     * Corresponde ao comando ##criarUsuario nos testes.
     */
    public void criarUsuario(String login, String senha, String nome) {
        jackutService.criarUsuario(login, senha, nome);
    }

    public void criarComunidade(String idSessao, String nome, String descricao) {
        try {
            jackutService.criarComunidade(idSessao, nome, descricao);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (CommunityAlreadyExistsException e) {
            throw new RuntimeException("Comunidade com esse nome já existe.");
        }
    }

    /**
     * Obtém a descrição de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        try {
            return jackutService.getDescricaoComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade não existe.");
        }
    }

    /**
     * Obtém o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        try {
            return jackutService.getDonoComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade não existe.");
        }
    }

    /**
     * Adiciona o usuário atual a uma comunidade
     * @param idSessao ID da sessão
     * @param nomeComunidade Nome da comunidade
     * @return Mensagem de sucesso ou erro
     */
    public void adicionarComunidade(String idSessao, String nomeComunidade) {
        try {
            jackutService.adicionarUsuarioAComunidade(idSessao, nomeComunidade);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade não existe.");
        } catch (UserAlreadyInCommunityException e) {
            throw new RuntimeException("Usuario já faz parte dessa comunidade.");
        }
    }

    /**
     * Obtém as comunidades de um usuário
     * @param login Login do usuário
     * @return String formatada com as comunidades
     */
    public String getComunidades(String login) {
        try {
            return jackutService.getComunidadesDoUsuario(login);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usuário não cadastrado.");
        }
    }

    public String getMembrosComunidade(String nome) {
        try {
            return jackutService.getMembrosComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade não existe.");
        }
    }

    /**
     * Autentica um usuário e inicia uma sessão.
     * @return ID da sessão criada
     * Corresponde ao comando ##abrirSessao nos testes.
     */
    public String abrirSessao(String login, String senha) {
        return jackutService.abrirSessao(login, senha);
    }

    /**
     * Modifica um atributo do perfil do usuário autenticado.
     * @param idSessao ID da sessão válida
     * @param atributo Atributo a ser modificado
     * @param valor Novo valor do atributo
     * Corresponde ao comando ##editarPerfil nos testes.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        if (idSessao == null || idSessao.isEmpty()) {
            throw new RuntimeException("Usuário não cadastrado.");
        }
        jackutService.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Obtém o valor de um atributo do perfil de um usuário.
     * @return Valor do atributo ou string vazia se não existir
     * Corresponde ao comando ##getAtributoUsuario nos testes.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return jackutService.getAtributoUsuario(login, atributo);
    }

    /**
     * Retorna a lista de amigos de um usuário no formato {amigo1,amigo2}.
     * @return String formatada com a lista de amigos
     * Corresponde ao comando ##getAmigos nos testes.
     */
    public String getAmigos(String login) {
        Set<String> amigos = jackutService.getAmigos(login);
        if (amigos.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(",", amigos) + "}";
    }

    /**
     * Envia uma mensagem de um usuário autenticado para outro.
     * @param idSessao ID da sessão do remetente
     * @param destinatario Login do usuário destinatário
     * @param mensagem Conteúdo da mensagem
     * Corresponde ao comando ##enviarRecado nos testes.
     */
    public void enviarRecado(String idSessao, String destinatario, String mensagem) {
        jackutService.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * Lê a próxima mensagem não lida do usuário autenticado.
     * @return Conteúdo da mensagem
     * Corresponde ao comando ##lerRecado nos testes.
     */
    public String lerRecado(String idSessao) {
        return jackutService.lerRecado(idSessao);
    }


    public void enviarMensagem(String idSessao, String comunidade, String mensagem) {
        try {
            jackutService.enviarMensagem(idSessao, comunidade, mensagem);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String lerMensagem(String idSessao) {
        try {
            return jackutService.lerMensagem(idSessao); // Toda a lógica movida para o Service
        } catch (InvalidSessionException | NoMessagesException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Adiciona ou confirma uma amizade entre usuários.
     * @param idSessao ID da sessão do usuário que está executando a ação
     * @param amigo Login do amigo a ser adicionado/confirmado
     * Corresponde ao comando ##adicionarAmigo nos testes.
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        jackutService.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usuários são amigos mútuos.
     * @return true se forem amigos, false caso contrário
     * Corresponde ao comando ##ehAmigo nos testes.
     */
    public boolean ehAmigo(String login, String amigo) {
        return jackutService.ehAmigo(login, amigo);
    }

    //US8_1

    // Fã-Ídolo
    public void adicionarIdolo(String idSessao, String idolo) {
        try {
            jackutService.adicionarIdolo(idSessao, idolo);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usuário não cadastrado.");
        } catch (RelationshipException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean ehFa(String fã, String ídolo) {
        return jackutService.ehFa(fã, ídolo);
    }

    public String getFas(String login) {
        return jackutService.getFas(login);
    }

    // Paquera
    public void adicionarPaquera(String idSessao, String paquera) {
        try {
            jackutService.adicionarPaquera(idSessao, paquera);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usuário não cadastrado.");
        } catch (RelationshipException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean ehPaquera(String idSessao, String paquera) {
        try {
            return jackutService.ehPaquera(idSessao, paquera);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        }
    }

    public String getPaqueras(String idSessao) {
        try {
            return jackutService.getPaqueras(idSessao);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        }
    }

    // Inimigo
    public void adicionarInimigo(String idSessao, String inimigo) {
        try {
            jackutService.adicionarInimigo(idSessao, inimigo);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usuário não cadastrado.");
        } catch (RelationshipException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //US8_1

    //US8_1

    public void removerUsuario(String idSessao) {
        try {
            jackutService.removerUsuario(idSessao);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sessão inválida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usuário não cadastrado.");
        }
    }

    //US9_1

    /**
     * Persiste todos os dados do sistema antes de encerrar.
     * Corresponde ao comando ##encerrarSistema nos testes.
     */
    public void encerrarSistema() {
        jackutService.salvarDados();
    }
}