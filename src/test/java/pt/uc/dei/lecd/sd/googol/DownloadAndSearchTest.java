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
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.time.Duration;
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
        downloader.connect("//localhost:1090/googoltest/barrels/barrel_1");

        searchModule = new RmiSearchModule("search");
        searchModule.connect("//localhost:1090/googoltest/barrels/barrel_1");
    }

    @Test
    void Should_find_document_After_index() throws RemoteException {
        assertTrue(downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));

        List<String> results = searchModule.searchResults("Stranger");

        log.debug("Received results for {} are {}", "Stranger", String.join(", ", results));

        assertTrue(results.size() > 0);
    }

    @Test
    void Should_index_When_url_is_queued() throws RemoteException, AlreadyBoundException {
        RMIQueue queue = new RMIQueue("testQueue");
        registry.bind("googoltest/queue", queue);
        
        queue.enqueue("https://en.wikipedia.org/wiki/Prison_Break");
        queue.enqueue("https://en.wikipedia.org/wiki/Star_Trek");
        queue.enqueue("https://en.wikipedia.org/wiki/The_Vampire_Diaries");

        downloader.connectQueue("//localhost:1090/googoltest/queue");
        downloader.start();

        // The downloader shall dequeue the url and the queue will become empty.
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            while (!queue.isEmpty()) {
                Thread.sleep(1000); // Wait for 1 second before trying again
            }
        });
        downloader.stop();
    }
}
