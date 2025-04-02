package facade;

import services.UserService;
import java.util.*;

/**
 * Fachada principal do sistema Jackut que expõe as operações para o EasyAccept.
 * Traduz chamadas dos testes para operações internas do sistema.
 */
public class JackutFacade {
    private final UserService userService = new UserService();

    /**
     * Reinicia o sistema, removendo todos os usuários e dados persistentes.
     * Corresponde ao comando ##zerarSistema nos testes.
     */
    public void zerarSistema() {
        userService.zerarSistema();
    }

    /**
     * Cria um novo usuário no sistema.
     * @param login Identificador único do usuário
     * @param senha Senha de acesso
     * @param nome Nome de exibição do usuário
     * Corresponde ao comando ##criarUsuario nos testes.
     */
    public void criarUsuario(String login, String senha, String nome) {
        userService.criarUsuario(login, senha, nome);
    }

    /**
     * Autentica um usuário e inicia uma sessão.
     * @return ID da sessão criada
     * Corresponde ao comando ##abrirSessao nos testes.
     */
    public String abrirSessao(String login, String senha) {
        return userService.abrirSessao(login, senha);
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
        userService.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Obtém o valor de um atributo do perfil de um usuário.
     * @return Valor do atributo ou string vazia se não existir
     * Corresponde ao comando ##getAtributoUsuario nos testes.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return userService.getAtributoUsuario(login, atributo);
    }

    /**
     * Retorna a lista de amigos de um usuário no formato {amigo1,amigo2}.
     * @return String formatada com a lista de amigos
     * Corresponde ao comando ##getAmigos nos testes.
     */
    public String getAmigos(String login) {
        Set<String> amigos = userService.getAmigos(login);
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
        userService.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * Lê a próxima mensagem não lida do usuário autenticado.
     * @return Conteúdo da mensagem
     * Corresponde ao comando ##lerRecado nos testes.
     */
    public String lerRecado(String idSessao) {
        return userService.lerRecado(idSessao);
    }

    /**
     * Adiciona ou confirma uma amizade entre usuários.
     * @param idSessao ID da sessão do usuário que está executando a ação
     * @param amigo Login do amigo a ser adicionado/confirmado
     * Corresponde ao comando ##adicionarAmigo nos testes.
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        userService.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usuários são amigos mútuos.
     * @return true se forem amigos, false caso contrário
     * Corresponde ao comando ##ehAmigo nos testes.
     */
    public boolean ehAmigo(String login, String amigo) {
        return userService.ehAmigo(login, amigo);
    }

    /**
     * Persiste todos os dados do sistema antes de encerrar.
     * Corresponde ao comando ##encerrarSistema nos testes.
     */
    public void encerrarSistema() {
        userService.salvarDados();
    }
}