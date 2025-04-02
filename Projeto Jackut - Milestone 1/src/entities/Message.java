package entities;

import java.io.Serializable;

/**
 * Classe que representa uma mensagem no sistema Jackut.
 * Implementa Serializable para permitir a serializa��o dos objetos.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;  // Vers�o para controle de serializa��o

    private final String sender;   // Remetente da mensagem (final - imut�vel ap�s cria��o)
    private final String content;  // Conte�do da mensagem (final - imut�vel ap�s cria��o)

    /**
     * Construtor que cria uma nova mensagem.
     *
     * @param sender Remetente da mensagem (n�o pode ser nulo)
     * @param content Conte�do da mensagem (n�o pode ser nulo)
     */
    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    /**
     * Retorna o remetente da mensagem.
     *
     * @return String com o login/nome do remetente
     */
    public String getSender() {
        return sender;
    }

    /**
     * Retorna o conte�do da mensagem.
     *
     * @return String com o texto da mensagem
     */
    public String getContent() {
        return content;
    }

    /**
     * Representa��o textual da mensagem (usada pelos testes).
     * Retorna apenas o conte�do, sem informa��es do remetente.
     *
     * @return String contendo apenas o conte�do da mensagem
     */
    @Override
    public String toString() {
        return content; // Formato simplificado para atender aos requisitos dos testes
    }
}