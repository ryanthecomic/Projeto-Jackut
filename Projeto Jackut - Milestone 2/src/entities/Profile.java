package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa um perfil de usuário com atributos dinâmicos.
 * Implementa Serializable para permitir a serialização dos objetos.
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;  // Versão para controle de serialização

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
     * A chave é convertida para lowercase para padronização.
     *
     * @param key   Nome do atributo (será convertido para minúsculas)
     * @param value Valor do atributo a ser armazenado
     */
    public void setAttribute(String key, String value) {
        attributes.put(key.toLowerCase(), value);
    }

    /**
     * Recupera o valor de um atributo específico.
     * Retorna string vazia se o atributo não existir.
     *
     * @param key Nome do atributo a ser buscado (convertido para minúsculas)
     * @return Valor do atributo ou string vazia ("") se não existir
     */
    public String getAttribute(String key) {
        return attributes.getOrDefault(key.toLowerCase(), "");
    }

}