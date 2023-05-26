package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Esta é uma classe utilitária para lidar com as entradas do registo.
 */
@Slf4j
public class GoogolRegistry {

    private List<String> entries;
    private Registry registry;
    private String lastBarrelNameReturnedForRoundRobin = null;
    private String host;
    private int port;


    /**
     * Cria uma instância de RegistryEntries com base numa lista de entries de registry.
     * 
     * @param entries   O array de entradas no registry
     */
    protected GoogolRegistry(String[] entries) {
        this.entries = Arrays.asList(entries);
    }

    public GoogolRegistry(String host, int port) throws RemoteException {
        this.host = host;
        this.port = port;
        this.registry = LocateRegistry.getRegistry(host, port);
        updateEntries();
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

    public static String getBarrelUri(String name, String host, int port) {
        return "rmi://" + host + ":" + port + "/" + name;
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

    private void updateEntries() throws AccessException, RemoteException {
        if (registry != null) {
            this.entries = Arrays.asList(registry.list());
        }
    }

    public void bind(Barrel barrel) throws AccessException, RemoteException, AlreadyBoundException {
        updateEntries();
        barrel.setName(getNextBarrelName());
        registry.bind(barrel.getName(), barrel);
    }

    public void bind(Search search) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind("googol/search", search);
    }

    public void bind(Downloader downloader) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(downloader.getName(), downloader);
    }

    public void bind(Queue queue) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind("googol/queue", queue);
    }

    public void bind(AdminConsole adminConsole) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind("googol/admin", adminConsole);
    }
 
    public boolean unbind(Barrel barrel) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(barrel.getName());
        return UnicastRemoteObject.unexportObject(barrel, false);
    }

    public boolean unbind(Search search) throws AccessException, RemoteException, NotBoundException {
        registry.unbind("googol/search");
        return UnicastRemoteObject.unexportObject(search, false);
    }

    public boolean unbind(Downloader downloader) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(downloader.getName());
        return UnicastRemoteObject.unexportObject(downloader, false);
    }

    public boolean unbind(Queue remoteQueue) throws AccessException, RemoteException, NotBoundException {
        registry.unbind("googol/queue");
        return UnicastRemoteObject.unexportObject(remoteQueue, false);
    }

    public InterfaceBarrel getBarrelInRoundRobin() throws AccessException, RemoteException, MalformedURLException, NotBoundException {
        updateEntries();
        String barrelName = getNextBarrelNameInRoundRobin();
        return (InterfaceBarrel) Naming.lookup(getBarrelUri(barrelName, host, port));
    }

    protected String getNextBarrelNameInRoundRobin() {
        List<String> barrels = getListOfBarrels();

        if (barrels.isEmpty()) {
            return null;
        }

        if (lastBarrelNameReturnedForRoundRobin == null) {
            lastBarrelNameReturnedForRoundRobin = barrels.get(0);
        } else {
            int index = Integer.parseInt(lastBarrelNameReturnedForRoundRobin.split("_")[1]);
            if (index < barrels.size()) {
                lastBarrelNameReturnedForRoundRobin = barrels.get(index);
            } else {
                lastBarrelNameReturnedForRoundRobin = barrels.get(0);
            }
        }

        return lastBarrelNameReturnedForRoundRobin;
    }

    public InterfaceQueue getQueue() {
        try {
            updateEntries();
            return (InterfaceQueue) Naming.lookup(getQueueUri(host, port));
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            log.error("Couldn't connect to queue. Is there a queue running?", e);
        }
        return null;
    }

    public List<InterfaceBarrel> getBarrels() {
        ArrayList<InterfaceBarrel> barrels = new ArrayList<>();
        List<String> barrelNames = getListOfBarrels();

        for (String barrelName: barrelNames) {
            InterfaceBarrel barrel;
            try {
                barrel = (InterfaceBarrel) Naming.lookup(getBarrelUri(barrelName, host, port));
                barrels.add(barrel);
            } catch (MalformedURLException | RemoteException | NotBoundException e) {
                log.error("Error when looking up for barrel {}", barrelName, e);
            }
        }

        return barrels;
    }

    public void notifyAdmin(Downloader downloader, String url) throws RemoteException, MalformedURLException, NotBoundException {
        if (adminConsoleExists()) {
            InterfaceAdminConsole admin = getAdminConsole();
            admin.onDownloaderIndex(downloader.getName(), url);    
        }
    }

    private InterfaceAdminConsole getAdminConsole() throws MalformedURLException, RemoteException, NotBoundException {
        return (InterfaceAdminConsole) Naming.lookup(getAdminUri());
    }

    private String getAdminUri() {
        return "rmi://" + host + ":" + port + "/googol/admin";
    }

    private boolean adminConsoleExists() throws AccessException, RemoteException {
        updateEntries();
        return entries.contains("googol/admin");
    }


    
}
