package entities;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável pela persistência dos dados do sistema Jackut.
 * Oferece métodos para salvar e carregar os usuários em arquivo.
 */
public class Jackut {
    public static final String DATA_FILE = "jackut_data.ser";  // Nome do arquivo de dados

    /**
     * Salva o mapa de usuários em arquivo de forma serializada.
     * Utiliza cópia defensiva do mapa para evitar modificações durante a serialização.
     *
     * @param usuarios Mapa de usuários a ser salvo (chave: login, valor: objeto User)
     * @throws RuntimeException Se ocorrer erro durante o processo de salvamento
     */
    public static void save(Map<String, User> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            // Salva uma cópia defensiva do mapa para evitar problemas de concorrência
            oos.writeObject(new HashMap<>(usuarios));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar dados", e);
        }
    }

    /**
     * Carrega os usuários a partir do arquivo serializado.
     * Se o arquivo não existir ou estiver corrompido, retorna null.
     * Em caso de arquivo corrompido, cria um backup antes de retornar.
     *
     * @return Mapa de usuários carregado ou null se não existir/estiver corrompido
     */
    @SuppressWarnings("unchecked")
    public static Map<String, User> load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;  // Retorna null se arquivo não existir

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (Map<String, User>) ois.readObject();  // Faz o cast do objeto lido
        } catch (Exception e) {
            // Se corrompido, renomeia o arquivo criando um backup com timestamp
            file.renameTo(new File(DATA_FILE + ".backup_" + System.currentTimeMillis()));
            return null;
        }
    }
}
