package pt.uc.dei.lecd.sd.googol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class DownloadersTest {

    private static Downloader downloader;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException {
        Registry registry = TestUtils.getRegistryInstance(1090);
        registry.rebind("googoltest/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));
        downloader = new Downloader("downloader_1");
        downloader.connectToBarrel("//localhost:1090/googoltest/barrels/barrel_1");
    }

    @Test
    void Should_return_true_When_downloader_indexURL() throws RemoteException {
        Assertions.assertTrue(downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));
    }
}
