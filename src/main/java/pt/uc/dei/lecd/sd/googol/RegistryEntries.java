package pt.uc.dei.lecd.sd.googol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Esta é uma classe utilitária para lidar com as entradas do registo.
 */
public class RegistryEntries {

    private List<String> entries;

    /**
     * Cria uma instância de RegistryEntries com base numa lista de entries de registry.
     * 
     * @param entries   O array de entradas no registry
     */
    public RegistryEntries(String[] entries) {
        this.entries = Arrays.asList(entries);
    }

    /**
     * Devolve a lista de downloaders que estão no registry.
     * 
     * @return a lista de downloaders.
     */
    public List<String> getListOfDownloaders() {
        ArrayList<String> downloaders = new ArrayList<>();

        for (String entry: entries) {
            if (entry.startsWith("/googol/downloaders/downloader_")) {
                downloaders.add(entry);
            }
        }

        return downloaders;
    }

    /**
     * Calcula o nome do próximo downloader com base nos existentes.
     * 
     * @return o nome do downloader
     */
    public String getNextDownloaderName() {

        List<String> downloaders = getListOfDownloaders();
        if (downloaders.isEmpty()) {
            return "downloader_1";
        }
        
        Collections.sort(downloaders, Collections.reverseOrder());
        String highest = downloaders.get(0).split("_")[1];
        int number = Integer.parseInt(highest);

        return "downloader_" + (number + 1);
    }

    /**
     * Indica se a queue existe no registo.
     * 
     * @return True se existe.
     */
    public boolean hasQueue() {
        return entries.contains("/googol/queue");
    }

    public static String getQueueUri(String host, int port) {
        return "rmi://" + host + ":" + port + "/googol/queue";
    }

    public static String getBarrelUri(String host, int port) {
        return "rmi://" + host + ":" + port + "/googol/barrels/barrel_1";
    }

}
