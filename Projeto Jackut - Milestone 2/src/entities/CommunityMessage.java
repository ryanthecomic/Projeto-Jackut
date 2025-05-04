package entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe responsável pelas mensagems (diferente de recados) das comunidades.
 */
public class CommunityMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String comunidade;
    private final String remetente;
    private final String conteudo;
    private final Date data;

    public CommunityMessage(String comunidade, String remetente, String conteudo) {
        this.comunidade = comunidade;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.data = new Date();
    }

    public Date getData() {
        return data;
    }

    public String getRemetente() {
        return remetente;
    }

    public String getComunidade() {
        return comunidade;
    }

    @Override
    public String toString() {
        return conteudo;
    }

}