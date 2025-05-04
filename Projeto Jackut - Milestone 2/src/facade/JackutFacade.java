package facade;

import exceptions.*;
import services.JackutService;
import java.util.*;

/**
 * Fachada principal do sistema Jackut que exp�e as opera��es para o EasyAccept.
 * Traduz chamadas dos testes para opera��es internas do sistema.
 */
public class JackutFacade {
    private final JackutService jackutService = new JackutService();

    /**
     * Reinicia o sistema, removendo todos os usu�rios e dados persistentes.
     * Corresponde ao comando ##zerarSistema nos testes.
     */
    public void zerarSistema() {
        jackutService.zerarSistema();
    }

    /**
     * Cria um novo usu�rio no sistema.
     * @param login Identificador �nico do usu�rio
     * @param senha Senha de acesso
     * @param nome Nome de exibi��o do usu�rio
     * Corresponde ao comando ##criarUsuario nos testes.
     */
    public void criarUsuario(String login, String senha, String nome) {
        jackutService.criarUsuario(login, senha, nome);
    }

    public void criarComunidade(String idSessao, String nome, String descricao) {
        try {
            jackutService.criarComunidade(idSessao, nome, descricao);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (CommunityAlreadyExistsException e) {
            throw new RuntimeException("Comunidade com esse nome j� existe.");
        }
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        try {
            return jackutService.getDescricaoComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade n�o existe.");
        }
    }

    /**
     * Obt�m o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        try {
            return jackutService.getDonoComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade n�o existe.");
        }
    }

    /**
     * Adiciona o usu�rio atual a uma comunidade
     * @param idSessao ID da sess�o
     * @param nomeComunidade Nome da comunidade
     * @return Mensagem de sucesso ou erro
     */
    public void adicionarComunidade(String idSessao, String nomeComunidade) {
        try {
            jackutService.adicionarUsuarioAComunidade(idSessao, nomeComunidade);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade n�o existe.");
        } catch (UserAlreadyInCommunityException e) {
            throw new RuntimeException("Usuario j� faz parte dessa comunidade.");
        }
    }

    /**
     * Obt�m as comunidades de um usu�rio
     * @param login Login do usu�rio
     * @return String formatada com as comunidades
     */
    public String getComunidades(String login) {
        try {
            return jackutService.getComunidadesDoUsuario(login);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
        }
    }

    public String getMembrosComunidade(String nome) {
        try {
            return jackutService.getMembrosComunidade(nome);
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException("Comunidade n�o existe.");
        }
    }

    /**
     * Autentica um usu�rio e inicia uma sess�o.
     * @return ID da sess�o criada
     * Corresponde ao comando ##abrirSessao nos testes.
     */
    public String abrirSessao(String login, String senha) {
        return jackutService.abrirSessao(login, senha);
    }

    /**
     * Modifica um atributo do perfil do usu�rio autenticado.
     * @param idSessao ID da sess�o v�lida
     * @param atributo Atributo a ser modificado
     * @param valor Novo valor do atributo
     * Corresponde ao comando ##editarPerfil nos testes.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        if (idSessao == null || idSessao.isEmpty()) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
        }
        jackutService.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Obt�m o valor de um atributo do perfil de um usu�rio.
     * @return Valor do atributo ou string vazia se n�o existir
     * Corresponde ao comando ##getAtributoUsuario nos testes.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return jackutService.getAtributoUsuario(login, atributo);
    }

    /**
     * Retorna a lista de amigos de um usu�rio no formato {amigo1,amigo2}.
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
     * Envia uma mensagem de um usu�rio autenticado para outro.
     * @param idSessao ID da sess�o do remetente
     * @param destinatario Login do usu�rio destinat�rio
     * @param mensagem Conte�do da mensagem
     * Corresponde ao comando ##enviarRecado nos testes.
     */
    public void enviarRecado(String idSessao, String destinatario, String mensagem) {
        jackutService.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * L� a pr�xima mensagem n�o lida do usu�rio autenticado.
     * @return Conte�do da mensagem
     * Corresponde ao comando ##lerRecado nos testes.
     */
    public String lerRecado(String idSessao) {
        return jackutService.lerRecado(idSessao);
    }


    public void enviarMensagem(String idSessao, String comunidade, String mensagem) {
        try {
            jackutService.enviarMensagem(idSessao, comunidade, mensagem);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (CommunityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String lerMensagem(String idSessao) {
        try {
            return jackutService.lerMensagem(idSessao); // Toda a l�gica movida para o Service
        } catch (InvalidSessionException | NoMessagesException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Adiciona ou confirma uma amizade entre usu�rios.
     * @param idSessao ID da sess�o do usu�rio que est� executando a a��o
     * @param amigo Login do amigo a ser adicionado/confirmado
     * Corresponde ao comando ##adicionarAmigo nos testes.
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        jackutService.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usu�rios s�o amigos m�tuos.
     * @return true se forem amigos, false caso contr�rio
     * Corresponde ao comando ##ehAmigo nos testes.
     */
    public boolean ehAmigo(String login, String amigo) {
        return jackutService.ehAmigo(login, amigo);
    }

    //US8_1

    // F�-�dolo
    public void adicionarIdolo(String idSessao, String idolo) {
        try {
            jackutService.adicionarIdolo(idSessao, idolo);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
        } catch (RelationshipException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean ehFa(String f�, String �dolo) {
        return jackutService.ehFa(f�, �dolo);
    }

    public String getFas(String login) {
        return jackutService.getFas(login);
    }

    // Paquera
    public void adicionarPaquera(String idSessao, String paquera) {
        try {
            jackutService.adicionarPaquera(idSessao, paquera);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
        } catch (RelationshipException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean ehPaquera(String idSessao, String paquera) {
        try {
            return jackutService.ehPaquera(idSessao, paquera);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        }
    }

    public String getPaqueras(String idSessao) {
        try {
            return jackutService.getPaqueras(idSessao);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        }
    }

    // Inimigo
    public void adicionarInimigo(String idSessao, String inimigo) {
        try {
            jackutService.adicionarInimigo(idSessao, inimigo);
        } catch (InvalidSessionException e) {
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
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
            throw new RuntimeException("Sess�o inv�lida ou expirada.");
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Usu�rio n�o cadastrado.");
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