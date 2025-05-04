package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa um perfil de usu�rio com atributos din�micos.
 * Implementa Serializable para permitir a serializa��o dos objetos.
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;  // Vers�o para controle de serializa��o

    // Mapa que armazena os atributos do perfil (chave-valor)
    private Map<String, String> attributes;

    /**
     * Construtor que inicializa um perfil vazio.
     * Cria um novo HashMap para armazenar os atributos.
     */
    public Profile() {
        this.attributes = new HashMap<>();
    }

    /**
     * Adiciona ou atualiza um atributo no perfil.
     * A chave � convertida para lowercase para padroniza��o.
     *
     * @param key   Nome do atributo (ser� convertido para min�sculas)
     * @param value Valor do atributo a ser armazenado
     */
    public void setAttribute(String key, String value) {
        attributes.put(key.toLowerCase(), value);
    }

    /**
     * Recupera o valor de um atributo espec�fico.
     * Retorna string vazia se o atributo n�o existir.
     *
     * @param key Nome do atributo a ser buscado (convertido para min�sculas)
     * @return Valor do atributo ou string vazia ("") se n�o existir
     */
    public String getAttribute(String key) {
        return attributes.getOrDefault(key.toLowerCase(), "");
    }

}