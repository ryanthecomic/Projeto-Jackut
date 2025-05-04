package applications;

import easyaccept.EasyAccept;
import facade.JackutFacade;
import services.*;

import java.util.Scanner;

/**
 * Classe principal que inicia a aplica��o Jackut.
 * Oferece um menu para execu��o de testes via EasyAccept ou intera��o manual.
 */
public class Main {
    /**
     * M�todo principal que inicia a aplica��o.
     *
     * @param args Argumentos de linha de comando (n�o utilizado)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        JackutFacade facade = new JackutFacade(); // Inst�ncia da fachada
        int opcao;

        while(true){

        System.out.println("=== MENU EASYACCEPT JACKUT ===");
        System.out.println("1 - Executar testes do EasyAccept");
        System.out.println("2 - Executar teste espec�fico do EasyAccept");
        System.out.println("3 - Sair");
        System.out.print("Escolha uma op��o: ");

        opcao = sc.nextInt();

        sc.nextLine();

        switch (opcao) {
            case 2:
                System.out.println("Qual teste deseja executar?");
                int opcao2 = sc.nextInt();
                sc.nextLine();

                executarTestesEspecificoEasyAccept(opcao2);

                break;
            case 1:
                executarTestesEasyAccept();
                break;
            case 3:
                System.out.println("Saindo...");
                System.exit(0);
                break;
            default:
                System.out.println("Op��o inv�lida!");
            }
        }
    }
    /**
     * Executa um todos os testes do EasyAccept baseado.
     *
     */
    private static void executarTestesEasyAccept() {
        String[][] argsTestes = {
                {"facade.JackutFacade", "src/scripts/us1_1.txt"},
                {"facade.JackutFacade", "src/scripts/us1_2.txt"},
                {"facade.JackutFacade", "src/scripts/us2_1.txt"},
                {"facade.JackutFacade", "src/scripts/us2_2.txt"},
                {"facade.JackutFacade", "src/scripts/us3_1.txt"},
                {"facade.JackutFacade", "src/scripts/us3_2.txt"},
                {"facade.JackutFacade", "src/scripts/us4_1.txt"},
                {"facade.JackutFacade", "src/scripts/us4_2.txt"},
                {"facade.JackutFacade", "src/scripts/us5_1.txt"},
                {"facade.JackutFacade", "src/scripts/us5_2.txt"},
                {"facade.JackutFacade", "src/scripts/us6_1.txt"},
                {"facade.JackutFacade", "src/scripts/us6_2.txt"},
                {"facade.JackutFacade", "src/scripts/us7_1.txt"},
                {"facade.JackutFacade", "src/scripts/us7_2.txt"},
                {"facade.JackutFacade", "src/scripts/us8_1.txt"},
                {"facade.JackutFacade", "src/scripts/us8_2.txt"},
                {"facade.JackutFacade", "src/scripts/us9_1.txt"},
                {"facade.JackutFacade", "src/scripts/us9_2.txt"}
        };

        for (String[] args : argsTestes) {
            EasyAccept.main(args);
        }
    }

    /**
     * Executa um teste espec�fico do EasyAccept baseado na sele��o do usu�rio.
     *
     * @param x N�mero do teste a ser executado (1-8)
     */
    private static void executarTestesEspecificoEasyAccept(int x) {
        String[][] argsTestes = {
                {"facade.JackutFacade", "src/scripts/us1_1.txt"},
                {"facade.JackutFacade", "src/scripts/us1_2.txt"},
                {"facade.JackutFacade", "src/scripts/us2_1.txt"},
                {"facade.JackutFacade", "src/scripts/us2_2.txt"},
                {"facade.JackutFacade", "src/scripts/us3_1.txt"},
                {"facade.JackutFacade", "src/scripts/us3_2.txt"},
                {"facade.JackutFacade", "src/scripts/us4_1.txt"},
                {"facade.JackutFacade", "src/scripts/us4_2.txt"},
                {"facade.JackutFacade", "src/scripts/us5_1.txt"},
                {"facade.JackutFacade", "src/scripts/us6_1.txt"},
                {"facade.JackutFacade", "src/scripts/us6_2.txt"},
                {"facade.JackutFacade", "src/scripts/us7_1.txt"},
                {"facade.JackutFacade", "src/scripts/us7_2.txt"},
                {"facade.JackutFacade", "src/scripts/us8_1.txt"},
                {"facade.JackutFacade", "src/scripts/us8_2.txt"},
                {"facade.JackutFacade", "src/scripts/us9_1.txt"},
                {"facade.JackutFacade", "src/scripts/us9_2.txt"}
        };

            EasyAccept.main(argsTestes[x-1]);

    }

}