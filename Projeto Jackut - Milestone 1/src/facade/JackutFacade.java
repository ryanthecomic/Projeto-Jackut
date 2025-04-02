package facade;

import services.UserService;
import java.util.*;

/**
 * Fachada principal do sistema Jackut que exp�e as opera��es para o EasyAccept.
 * Traduz chamadas dos testes para opera��es internas do sistema.
 */
public class JackutFacade {
    private final UserService userService = new UserService();

    /**
     * Reinicia o sistema, removendo todos os usu�rios e dados persistentes.
     * Corresponde ao comando ##zerarSistema nos testes.
     */
    public void zerarSistema() {
        userService.zerarSistema();
    }

    /**
     * Cria um novo usu�rio no sistema.
     * @param login Identificador �nico do usu�rio
     * @param senha Senha de acesso
     * @param nome Nome de exibi��o do usu�rio
     * Corresponde ao comando ##criarUsuario nos testes.
     */
    public void criarUsuario(String login, String senha, String nome) {
        userService.criarUsuario(login, senha, nome);
    }

    /**
     * Autentica um usu�rio e inicia uma sess�o.
     * @return ID da sess�o criada
     * Corresponde ao comando ##abrirSessao nos testes.
     */
    public String abrirSessao(String login, String senha) {
        return userService.abrirSessao(login, senha);
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
        userService.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Obt�m o valor de um atributo do perfil de um usu�rio.
     * @return Valor do atributo ou string vazia se n�o existir
     * Corresponde ao comando ##getAtributoUsuario nos testes.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return userService.getAtributoUsuario(login, atributo);
    }

    /**
     * Retorna a lista de amigos de um usu�rio no formato {amigo1,amigo2}.
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
     * Envia uma mensagem de um usu�rio autenticado para outro.
     * @param idSessao ID da sess�o do remetente
     * @param destinatario Login do usu�rio destinat�rio
     * @param mensagem Conte�do da mensagem
     * Corresponde ao comando ##enviarRecado nos testes.
     */
    public void enviarRecado(String idSessao, String destinatario, String mensagem) {
        userService.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * L� a pr�xima mensagem n�o lida do usu�rio autenticado.
     * @return Conte�do da mensagem
     * Corresponde ao comando ##lerRecado nos testes.
     */
    public String lerRecado(String idSessao) {
        return userService.lerRecado(idSessao);
    }

    /**
     * Adiciona ou confirma uma amizade entre usu�rios.
     * @param idSessao ID da sess�o do usu�rio que est� executando a a��o
     * @param amigo Login do amigo a ser adicionado/confirmado
     * Corresponde ao comando ##adicionarAmigo nos testes.
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        userService.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usu�rios s�o amigos m�tuos.
     * @return true se forem amigos, false caso contr�rio
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