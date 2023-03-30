package pt.uc.dei.lecd.sd.googol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DownloadersTest {

    private static Downloader victim;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException {
        Registry registry = LocateRegistry.createRegistry(1090);
        registry.rebind("googoltest/barrels/barrel_1", new IndexStorageBarrel("barrel_1"));
        victim = new Downloader("downloader_1");
        victim.connect("//localhost:1090/googoltest/barrels/barrel_1");
    }

    @Test
    void Should_receive_pong_When_Ping_Is_Called() throws RemoteException {
        victim.indexURL("https://en.wikipedia.org/wiki/Stranger_Things");
    }

}
