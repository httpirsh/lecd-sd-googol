package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@Slf4j
public class DownloadAndSearchTest {

    private static Downloader downloader;
    private static RmiSearchModule searchModule;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException {
        Registry registry = TestUtils.getRegistryInstance(1090);

        registry.rebind("googoltest/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));

        downloader = new Downloader("downloader_1");
        downloader.connect("//localhost:1090/googoltest/barrels/barrel_1");

        searchModule = new RmiSearchModule("search");
        searchModule.connect("//localhost:1090/googoltest/barrels/barrel_1");
    }

    @Test
    void Should_find_document_After_index() throws RemoteException {
        Assertions.assertTrue(downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));

       List<String> results = searchModule.searchResults("Stranger");

       log.debug("Received results for {} are {}", "Stranger", String.join(", ", results));

       Assertions.assertTrue(results.size() > 0);
    }
}
