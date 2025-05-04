package entities;

import exceptions.NoMessagesException;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

/**
 * Classe responsável pelas comunidades do Jackut.
 */
public class Community implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private User owner;
    private Set<User> members;

    public Community(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner); // dono é automaticamente o primeiro membro
    }

    // Getters básicos
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public User getOwner() {
        return owner;
    }
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Adicionar um usuário a Comunidade
     * @param member Usuário a ser adicionado
     * @return Membro Adicionado
     *
     */
    public boolean addMember(User member) {
        return members.add(member);
    }

}