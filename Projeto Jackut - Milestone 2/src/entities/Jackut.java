package entities;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe respons�vel pela persist�ncia dos dados do sistema Jackut.
 * Oferece m�todos para salvar e carregar os dados em arquivo.
 */
public class Jackut {
    public static final String DATA_FILE = "jackut_data.ser";  // Nome do arquivo de dados

    /**
     * Salva os dados do sistema em arquivo de forma serializada.
     * Agora inclui usu�rios e comunidades.
     *
     * @param dados Mapa contendo:
     *              - "usuarios": Map<String, User>
     *              - "comunidades": Map<String, Community>
     * @throws RuntimeException Se ocorrer erro durante o processo de salvamento
     */
    public static void save(Map<String, Object> dados) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {

            // Limpeza de refer�ncias antes de salvar
            Map<String, User> usuarios = (Map<String, User>) dados.get("usuarios");
            Map<String, Community> comunidades = (Map<String, Community>) dados.get("comunidades");

            // Remove usu�rios inexistentes das comunidades
            for (Community comunidade : comunidades.values()) {
                comunidade.getMembers().removeIf(m -> !usuarios.containsKey(m.getLogin()));

                // Verifica se o dono ainda existe
                if (!usuarios.containsKey(comunidade.getOwner().getLogin())) {
                    comunidade.getMembers().clear(); // Limpa membros se dono n�o existir
                }
            }

            // Cria c�pia defensiva para serializa��o
            Map<String, Object> dadosParaSalvar = new HashMap<>();
            dadosParaSalvar.put("usuarios", new HashMap<>(usuarios));
            dadosParaSalvar.put("comunidades", new HashMap<>(comunidades));

            oos.writeObject(dadosParaSalvar);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar dados", e);
        }
    }

    /**
     * Carrega os dados a partir do arquivo serializado.
     * Agora carrega usu�rios e comunidades.
     *
     * @return Mapa contendo:
     *         - "usuarios": Map<String, User>
     *         - "comunidades": Map<String, Community>
     *         ou null se arquivo n�o existir/estiver corrompido
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {

            Map<String, Object> dados = (Map<String, Object>) ois.readObject();

            // Verifica integridade dos dados carregados
            Map<String, User> usuarios = (Map<String, User>) dados.get("usuarios");
            Map<String, Community> comunidades = (Map<String, Community>) dados.get("comunidades");

            // Reconstroi refer�ncias entre usu�rios e comunidades
            for (Community comunidade : comunidades.values()) {
                // Atualiza refer�ncia ao dono
                User dono = usuarios.get(comunidade.getOwner().getLogin());
                if (dono != null) {
                    // Atualiza a refer�ncia do dono na comunidade
                    comunidade.getMembers().removeIf(m -> !usuarios.containsKey(m.getLogin()));
                    comunidade.getMembers().add(dono); // Garante que o dono est� na lista
                } else {
                    // Se dono n�o existe, a comunidade � inv�lida
                    comunidades.remove(comunidade.getName());
                }
            }

            return dados;
        } catch (Exception e) {
            // Cria backup do arquivo corrompido
            file.renameTo(new File(DATA_FILE + ".backup_" + System.currentTimeMillis()));
            return null;
        }
    }
}