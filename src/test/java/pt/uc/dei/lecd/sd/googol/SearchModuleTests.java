package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.Arrays;
import java.util.List;

/**
 * Esta é uma classe de teste JUnit chamada DownloadAndSearchTest que testa a funcionalidade de um mecanismo de busca.
 */
@Slf4j
public class SearchModuleTests {

    private static Downloader downloader;
    private static RmiSearchModule searchModule;
    private static Registry registry;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        registry = TestUtils.getRegistryInstance(1090);

        registry.bind("SearchModuleTests/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));

        downloader = new Downloader("downloader_1");
        downloader.connectToBarrel("//localhost:1090/SearchModuleTests/barrels/barrel_1");

        searchModule = new RmiSearchModule("search");
        searchModule.connectToBarrel("//localhost:1090/SearchModuleTests/barrels/barrel_1");
    }

    @Test
    void Should_return_top10_After_many_searches() throws RemoteException {
        // Fazer 20 pesquisas
        for (int i = 1; i <= 20; i++) {
            searchModule.searchResults(Integer.toString(i));
            if (i % 2 == 0) { // Se for par, pesquisa outra vez para ficar no top
                searchModule.searchResults(Integer.toString(i));
            }
        }

        List<String> topSearches = searchModule.getTopSearches(10);
        log.info("SearchModule getTopSearches returned {}", topSearches);

        assertAll("Os mais pesquisados devem ser os pares.",
            // os pares estão
            () -> assertTrue(topSearches.contains("2")),
            () -> assertTrue(topSearches.contains("4")),
            () -> assertTrue(topSearches.contains("6")),
            () -> assertTrue(topSearches.contains("8")),
            () -> assertTrue(topSearches.contains("10")),
            () -> assertTrue(topSearches.contains("12")),
            () -> assertTrue(topSearches.contains("14")),
            () -> assertTrue(topSearches.contains("16")),
            () -> assertTrue(topSearches.contains("18")),
            () -> assertTrue(topSearches.contains("20")),
            // e os impares não
            () -> assertFalse(topSearches.contains("1")),
            () -> assertFalse(topSearches.contains("3")),
            () -> assertFalse(topSearches.contains("5")),
            () -> assertFalse(topSearches.contains("7")),
            () -> assertFalse(topSearches.contains("9")),
            () -> assertFalse(topSearches.contains("11")),
            () -> assertFalse(topSearches.contains("13")),
            () -> assertFalse(topSearches.contains("15")),
            () -> assertFalse(topSearches.contains("17")),
            () -> assertFalse(topSearches.contains("19"))

        );
    }

    @Test
    void Should_get_admin_stats() throws AccessException, RemoteException, NotBoundException, ServerNotActiveException, MalformedURLException {
        // top ten searches
        // lista de downloaders (IP:porto)
        // lista de barrels (IP:porto)
         // Get the list of all registered object names
         String[] objectNames = registry.list();
        
         // Iterate over the list of object names and retrieve each object
         for (String objectName : objectNames) {
             // Get the remote object reference
             Remote remoteObject = (Remote) Naming.lookup(objectName);
             
             // Cast the remote object to RemoteServer to get IP address and port
             RemoteServer remoteServer = (RemoteServer) remoteObject;
             String ipAddress = RemoteServer.getClientHost();
             int port = remoteServer.getRef().remoteToString().indexOf(":");
             
             // Print the IP address and port to the console
             log.debug("Object " + objectName + " is located at " + ipAddress + ":" + port);
         }
    }

  
}