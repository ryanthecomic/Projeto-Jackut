package entities;

import java.io.Serializable;

/**
 * Classe que representa uma mensagem no sistema Jackut.
 * Implementa Serializable para permitir a serialização dos objetos.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;  // Versão para controle de serialização

    private final String sender;   // Remetente da mensagem (final - imutável após criação)
    private final String content;  // Conteúdo da mensagem (final - imutável após criação)

    /**
     * Construtor que cria uma nova mensagem.
     *
     * @param sender Remetente da mensagem (não pode ser nulo)
     * @param content Conteúdo da mensagem (não pode ser nulo)
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
     * Retorna o conteúdo da mensagem.
     *
     * @return String com o texto da mensagem
     */
    public String getContent() {
        return content;
    }

    /**
     * Representação textual da mensagem (usada pelos testes).
     * Retorna apenas o conteúdo, sem informações do remetente.
     *
     * @return String contendo apenas o conteúdo da mensagem
     */
    @Override
    public String toString() {
        return content; // Formato simplificado para atender aos requisitos dos testes
    }
}