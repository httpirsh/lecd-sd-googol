package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

/**
 * O código apresentado implementa a classe GoogolCluster que é responsável por iniciar e configurar um cluster para a aplicação Googol.
 *
 * O método init recebe uma porta como parâmetro e inicializa um registro de serviços RMI nessa porta.
 * Em seguida, registra vários objetos RMI no registro, incluindo:
 * o módulo de busca RmiSearchModule, dois objetos IndexStorageBarrel (usados para armazenar índices de páginas), dois objetos Downloader (usados para baixar páginas) e um objeto RMIQueue (usado para gerenciar as tarefas de busca).
 * O método main inicializa um novo objeto GoogolCluster e chama o método init passando a porta 1099.
 * Em seguida, o código espera a entrada do usuário para encerrar o cluster.
*/

@Slf4j
public class GoogolCluster {

    public void init(int port) throws MalformedURLException, NotBoundException, RemoteException {
        log.info("Registry initiating in port {} ...", port);
        Registry registry = LocateRegistry.createRegistry(port);
        log.info("Registry ok");

        registry.rebind("googol/search", new RmiSearchModule("search"));
        log.info("RmiSearchModel {} bound at {}", "search", "googol/search");

        registry.rebind("googol/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));
        log.info("IndexStorageBarrel {} bound at {}", "barrel_1", "googol/barrels/barrel_1");

        registry.rebind("googol/barrels/barrel_2", new IndexStorageBarrel("barrel_2"));
        log.info("IndexStorageBarrel {} bound at {}", "barrel_2", "googol/barrels/barrel_2");

        registry.rebind("googol/downloaders/downloader_1", new Downloader("downloader_1"));
        log.info("Downloader {} bound at {}", "downloader_1", "googol/downloaders/downloader_1");

        registry.rebind("googol/downloaders/downloader_2", new Downloader("downloader_2"));
        log.info("Downloader {} bound at {}", "downloader_2", "googol/downloaders/downloader_2");

        registry.rebind("googol/queue", new RMIQueue("queue"));
        log.info("Queue {} bound at {}", "queue", "googol/queue");
    }
    public static void main(String[] args) {
        System.out.println("Googol cluster.... initialising.");
        GoogolCluster cluster = new GoogolCluster();
        try {
            cluster.init(1099);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            log.error("Failure initiating Googol Cluster.", e);
        }
        System.out.println("Googol cluster initialized successfully.");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Press any key to stop cluster...");
        scanner.nextLine();
        scanner.close();
    }
}