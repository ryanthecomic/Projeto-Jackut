package entities;

import java.io.Serializable;

/**
 * Classe que representa uma mensagem no sistema Jackut.
 * Implementa Serializable para permitir a serialização dos objetos.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;  // Versão para controle de serialização

    private final String remetente;   // Remetente da mensagem (final - imutável após criação)
    private final String conteudo;  // Conteúdo da mensagem (final - imutável após criação)

    /**
     * Construtor que cria uma nova mensagem.
     *
     * @param remetente Remetente da mensagem (não pode ser nulo)
     * @param conteudo Conteúdo da mensagem (não pode ser nulo)
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
     * Retorna o conteúdo da mensagem.
     *
     * @return String com o texto da mensagem
     */
    public String getContent() {
        return conteudo;
    }

    /**
     * Representação textual da mensagem (usada pelos testes).
     * Retorna apenas o conteúdo, sem informações do remetente.
     *
     * @return String contendo apenas o conteúdo da mensagem
     */
    @Override
    public String toString() {
        return conteudo; // Formato simplificado para atender aos requisitos dos testes
    }
}