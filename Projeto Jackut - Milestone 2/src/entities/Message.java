package entities;

import java.io.Serializable;

/**
 * Classe que representa uma mensagem no sistema Jackut.
 * Implementa Serializable para permitir a serializa��o dos objetos.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;  // Vers�o para controle de serializa��o

    private final String remetente;   // Remetente da mensagem (final - imut�vel ap�s cria��o)
    private final String conteudo;  // Conte�do da mensagem (final - imut�vel ap�s cria��o)

    /**
     * Construtor que cria uma nova mensagem.
     *
     * @param remetente Remetente da mensagem (n�o pode ser nulo)
     * @param conteudo Conte�do da mensagem (n�o pode ser nulo)
     */
    public Message(String remetente, String conteudo) {
        this.remetente = remetente;
        this.conteudo = conteudo;
    }

    /**
     * Retorna o remetente da mensagem.
     *
     * @return String com o login/nome do remetente
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Retorna o conte�do da mensagem.
     *
     * @return String com o texto da mensagem
     */
    public String getContent() {
        return conteudo;
    }

    /**
     * Representa��o textual da mensagem (usada pelos testes).
     * Retorna apenas o conte�do, sem informa��es do remetente.
     *
     * @return String contendo apenas o conte�do da mensagem
     */
    @Override
    public String toString() {
        return conteudo; // Formato simplificado para atender aos requisitos dos testes
    }
}