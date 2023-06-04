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
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * Esta é uma classe utilitária para lidar com as entradas do registo.
 */
@Slf4j
public class GoogolRegistry {

    private static final String MONITOR_NAME = "googol/monitor";
    private static final String QUEUE_NAME = "googol/queue";
    private static final String SEARCH_NAME = "googol/search";
    private static final String DOWNLOADER_NAME_PREFIX = "googol/downloaders/downloader_";
    private static final String BARREL_NAME_PREFIX = "googol/barrels/barrel_";
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
     * Devolve a lista de nomes dos downloaders que estão no registry.
     * 
     * @return a lista de nomes dos downloaders.
     * @throws RemoteException
     * @throws AccessException
     */
    public List<String> getListOfDownloaders() throws AccessException, RemoteException {
        return getListStartsWith(DOWNLOADER_NAME_PREFIX);
    }

     /**
     * Devolve a lista de nomes dos barrels que estão no registry.
     * 
     * @return a lista de nomes dos barrels.
     * @throws RemoteException
     * @throws AccessException
     */
    public List<String> getListOfBarrels() throws AccessException, RemoteException {
        return getListStartsWith(BARREL_NAME_PREFIX);
    }

    /**
     * Calcula o nome do próximo downloader com base nos existentes.
     * 
     * @return o nome do downloader
     * @throws RemoteException
     * @throws AccessException
     */
    public String getNextDownloaderName() throws AccessException, RemoteException {

        List<String> downloaders = getListOfDownloaders();
        if (downloaders.isEmpty()) {
            return DOWNLOADER_NAME_PREFIX + "1";
        }
        
        Collections.sort(downloaders, Collections.reverseOrder());
        String highest = downloaders.get(0).split("_")[1];
        int number = Integer.parseInt(highest);

        return DOWNLOADER_NAME_PREFIX + (number + 1);
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
        return entries.contains(QUEUE_NAME);
    }

    public static String getQueueUri(String host, int port) {
        return "rmi://" + host + ":" + port + "/" + QUEUE_NAME;
    }

    public static String getBarrelUri(String name, String host, int port) {
        return "rmi://" + host + ":" + port + "/" + name;
    }

    public String getNextBarrelName() throws AccessException, RemoteException {
        List<String> barrels = getListOfBarrels();
        if (barrels.isEmpty()) {
            return BARREL_NAME_PREFIX + "1";
        }
        
        Collections.sort(barrels, Collections.reverseOrder());
        String highest = barrels.get(0).split("_")[1];
        int number = Integer.parseInt(highest);

        return BARREL_NAME_PREFIX + (number + 1);
    }

    private List<String> getListStartsWith(String start) throws AccessException, RemoteException {
        updateEntries();
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
        barrel.setName(getNextBarrelName());
        registry.bind(barrel.getName(), barrel);
    }

    public void bind(Search search) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(SEARCH_NAME, search);
    }

    public void bind(Downloader downloader) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(downloader.getName(), downloader);
    }

    public void bind(Queue queue) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(QUEUE_NAME, queue);
    }

    public void bind(Monitor monitor) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(MONITOR_NAME, monitor);
    }
 
    public boolean unbind(Barrel barrel) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(barrel.getName());
        return UnicastRemoteObject.unexportObject(barrel, false);
    }

    public boolean unbind(Search search) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(SEARCH_NAME);
        return UnicastRemoteObject.unexportObject(search, false);
    }

    public boolean unbind(Downloader downloader) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(downloader.getName());
        return UnicastRemoteObject.unexportObject(downloader, true);
    }

    public boolean unbind(Queue remoteQueue) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(QUEUE_NAME);
        return UnicastRemoteObject.unexportObject(remoteQueue, false);
    }

    public boolean unbind(Monitor monitor) throws AccessException, RemoteException, NotBoundException {
        registry.unbind(MONITOR_NAME);
        return UnicastRemoteObject.unexportObject(monitor, false);
    }

    public InterfaceBarrel lookupBarrelInRoundRobin() throws AccessException, RemoteException, MalformedURLException, NotBoundException {
        String barrelName = getNextBarrelNameInRoundRobin();
        return (InterfaceBarrel) Naming.lookup(getBarrelUri(barrelName, host, port));
    }

    protected String getNextBarrelNameInRoundRobin() throws AccessException, RemoteException {
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

    public InterfaceQueue lookupQueue() {
        try {
            updateEntries();
            return (InterfaceQueue) Naming.lookup(getQueueUri(host, port));
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            log.error("Couldn't connect to queue. Is there a queue running?", e);
        }
        return null;
    }

    public List<InterfaceBarrel> getBarrels() throws AccessException, RemoteException {
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

    public void downloaderNotification(String downloader) throws RemoteException, MalformedURLException, NotBoundException {
        if (monitorIsActive()) {
            InterfaceAdminConsole monitor = lookupMonitor();
            monitor.downloaderNotification(downloader);    
        }
    }

    public void barrelNotification(String name) throws AccessException, RemoteException, MalformedURLException, NotBoundException {
        if (monitorIsActive()) {
            InterfaceAdminConsole monitor = lookupMonitor();
            monitor.barrelNotification(name);
        }
    }

    private InterfaceAdminConsole lookupMonitor() throws MalformedURLException, RemoteException, NotBoundException {
        return (InterfaceAdminConsole) Naming.lookup(getMonitorUri());
    }

    private String getMonitorUri() {
        return "rmi://" + host + ":" + port + "/" + MONITOR_NAME;
    }

    private boolean monitorIsActive() throws AccessException, RemoteException {
        updateEntries();
        return entries.contains(MONITOR_NAME);
    }

    public void topSearchChangedNotification(Set<String> topSearches) throws MalformedURLException, RemoteException, NotBoundException {
        if (monitorIsActive()) {
            InterfaceAdminConsole admin = lookupMonitor();
            admin.topSearchChangedNotification(topSearches);
        }
    }

    public InterfaceSearchModule lookupSearch() throws MalformedURLException, RemoteException, NotBoundException {
        return (InterfaceSearchModule) Naming.lookup(getSearchUri());
    }

    private String getSearchUri() {
        return "rmi://" + host + ":" + port + "/" + SEARCH_NAME;
    }

}
