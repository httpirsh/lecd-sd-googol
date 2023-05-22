package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

/**
 * O objetivo da classe GoogolCluster é inicializar e configurar os componentes da aplicação Googol.
 * A classe define um método init() que registra os diferentes módulos e componentes do cluster no registro RMI do Java,
 * permitindo o acesso a outras classes.
 * Além disso, a classe contém um método main() que instancia um objeto GoogolCluster e
 * chama o método init() para inicializar o cluster, bem como aguarda a entrada do usuário para encerrar o cluster.
 */
@Slf4j
public class GoogolCluster {

    public void init(int port) throws MalformedURLException, NotBoundException, RemoteException {
        log.info("Registry initiating in port {} ...", port);
        Registry registry = LocateRegistry.createRegistry(port);
        log.info("Registry ok");

        Barrel barrel1 = new Barrel();
        Barrel barrel2 = new Barrel();

        Search search = new Search();

        Downloader downloader1 = new Downloader();
        Downloader downloader2 = new Downloader();
        RemoteQueue queue = new RemoteQueue();

        registry.rebind("googol/search", search);
        log.info("RmiSearchModule {} bound at {}", "search", "googol/search");

        registry.rebind("googol/barrels/barrel_1", barrel1);
        log.info("IndexStorageBarrel {} bound at {}", "barrel_1", "googol/barrels/barrel_1");

        registry.rebind("googol/barrels/barrel_2", barrel2);
        log.info("IndexStorageBarrel {} bound at {}", "barrel_2", "googol/barrels/barrel_2");

        registry.rebind("googol/downloaders/downloader_1", downloader1);
        log.info("Downloader {} bound at {}", "downloader_1", "googol/downloaders/downloader_1");

        registry.rebind("googol/downloaders/downloader_2", downloader2);
        log.info("Downloader {} bound at {}", "downloader_2", "googol/downloaders/downloader_2");

        registry.rebind("googol/queue", queue);
        log.info("Queue {} bound at {}", "queue", "googol/queue");

        downloader1.connectToBarrel("rmi://localhost/googol/barrels/barrel_1");
        downloader1.connectToQueue("rmi://localhost/googol/queue");
        downloader1.start();
        search.start("localhost", port);
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
