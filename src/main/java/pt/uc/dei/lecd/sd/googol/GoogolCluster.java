package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;


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