package entities;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe respons�vel pela persist�ncia dos dados do sistema Jackut.
 * Oferece m�todos para salvar e carregar os usu�rios em arquivo.
 */
public class Jackut {
    public static final String DATA_FILE = "jackut_data.ser";  // Nome do arquivo de dados

    /**
     * Salva o mapa de usu�rios em arquivo de forma serializada.
     * Utiliza c�pia defensiva do mapa para evitar modifica��es durante a serializa��o.
     *
     * @param usuarios Mapa de usu�rios a ser salvo (chave: login, valor: objeto User)
     * @throws RuntimeException Se ocorrer erro durante o processo de salvamento
     */
    public static void save(Map<String, User> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            // Salva uma c�pia defensiva do mapa para evitar problemas de concorr�ncia
            oos.writeObject(new HashMap<>(usuarios));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar dados", e);
        }
    }

    /**
     * Carrega os usu�rios a partir do arquivo serializado.
     * Se o arquivo n�o existir ou estiver corrompido, retorna null.
     * Em caso de arquivo corrompido, cria um backup antes de retornar.
     *
     * @return Mapa de usu�rios carregado ou null se n�o existir/estiver corrompido
     */
    @SuppressWarnings("unchecked")
    public static Map<String, User> load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;  // Retorna null se arquivo n�o existir

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
