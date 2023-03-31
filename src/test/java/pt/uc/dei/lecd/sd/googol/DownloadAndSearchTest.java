package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;

@Slf4j
public class DownloadAndSearchTest {

    private static Downloader downloader;
    private static RmiSearchModule searchModule;
    private static Registry registry;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        registry = TestUtils.getRegistryInstance(1090);

        registry.bind("googoltest/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));

        downloader = new Downloader("downloader_1");
        downloader.connectToBarrel("//localhost:1090/googoltest/barrels/barrel_1");

        searchModule = new RmiSearchModule("search");
        searchModule.connectToBarrel("//localhost:1090/googoltest/barrels/barrel_1");
    }

    @Test
    void Should_find_document_After_index() throws RemoteException {
        assertTrue(downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));

        List<String> results = searchModule.searchResults("Stranger");

        log.debug("Received results for {} are {}", "Stranger", String.join(", ", results));

        assertTrue(results.size() > 0);
    }

    @Test
    void Should_increase_index_size_When_downloader_running_for_a_while() throws RemoteException, AlreadyBoundException, InterruptedException {
        RMIQueue queue = new RMIQueue("testQueue");
        registry.bind("googoltest/queue", queue);
        downloader.connectToQueue("//localhost:1090/googoltest/queue");

        queue.enqueue("https://en.wikipedia.org/wiki/Prison_Break");

        downloader.start();
        Thread.sleep(5000);
        downloader.stop();

        assertTrue(queue.size() > 1);
    }
}
