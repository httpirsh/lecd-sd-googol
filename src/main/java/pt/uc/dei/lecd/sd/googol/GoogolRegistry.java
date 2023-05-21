package pt.uc.dei.lecd.sd.googol;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Esta é uma classe utilitária para lidar com as entradas do registo.
 */
public class GoogolRegistry {

    private List<String> entries;
    private Registry registry;

    /**
     * Cria uma instância de RegistryEntries com base numa lista de entries de registry.
     * 
     * @param entries   O array de entradas no registry
     */
    public GoogolRegistry(String[] entries) {
        this.entries = Arrays.asList(entries);
    }

    public GoogolRegistry(String host, int port) throws RemoteException {
        registry = LocateRegistry.getRegistry(host, port);
    }

    /**
     * Devolve a lista de downloaders que estão no registry.
     * 
     * @return a lista de downloaders.
     */
    public List<String> getListOfDownloaders() {
        return getListStartsWith("googol/downloaders/downloader_");
    }

    /**
     * Calcula o nome do próximo downloader com base nos existentes.
     * 
     * @return o nome do downloader
     */
    public String getNextDownloaderName() {

        List<String> downloaders = getListOfDownloaders();
        if (downloaders.isEmpty()) {
            return "googol/downloaders/downloader_1";
        }
        
        Collections.sort(downloaders, Collections.reverseOrder());
        String highest = downloaders.get(0).split("_")[1];
        int number = Integer.parseInt(highest);

        return "googol/downloaders/downloader_" + (number + 1);
    }

    /**
     * Indica se a queue existe no registo.
     * 
     * @return True se existe.
     * @throws RemoteException
     * @throws AccessException
     */
    public boolean hasQueue() throws AccessException, RemoteException {
        updateEntries();
        return entries.contains("googol/queue");
    }

    public static String getQueueUri(String host, int port) {
        return "rmi://" + host + ":" + port + "/googol/queue";
    }

    public static String getBarrelUri(String host, int port) {
        return "rmi://" + host + ":" + port + "/googol/barrels/barrel_1"; //TODO: Still missing support for multiple barrels
    }

    public String getNextBarrelName() {
        List<String> barrels = getListOfBarrels();
        if (barrels.isEmpty()) {
            return "googol/barrels/barrel_1";
        }
        
        Collections.sort(barrels, Collections.reverseOrder());
        String highest = barrels.get(0).split("_")[1];
        int number = Integer.parseInt(highest);

        return "googol/barrels/barrel_" + (number + 1);
    }

    private List<String> getListOfBarrels() {
        return getListStartsWith("googol/barrels/barrel_");
    }

    private List<String> getListStartsWith(String start) {
        ArrayList<String> itemStrings = new ArrayList<>();

        for (String entry: entries) {
            if (entry.startsWith(start)) {
                itemStrings.add(entry);
            }
        }

        return itemStrings;
    }

    public void bind(Barrel barrel) throws AccessException, RemoteException, AlreadyBoundException {
        updateEntries();
        barrel.setName(getNextBarrelName());
        registry.bind(barrel.getName(), barrel);
    }

    private void updateEntries() throws AccessException, RemoteException {
        if (registry != null) {
            this.entries = Arrays.asList(registry.list());
        }
    }

    public boolean unbind(Barrel barrel) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(barrel.getName());
        return UnicastRemoteObject.unexportObject(barrel, false);
    }

    public void bind(RemoteQueue queue) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind("googol/queue", queue);
    }

    public boolean unbind(RemoteQueue remoteQueue) throws AccessException, RemoteException, NotBoundException {
        registry.unbind("googol/queue");
        return UnicastRemoteObject.unexportObject(remoteQueue, false);
    }
}
