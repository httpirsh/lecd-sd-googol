package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MonitorTest {
    
    private static final String host = "localhost";
    private static final int port = 1090;

    @BeforeAll
    static void init() throws RemoteException {
        TestUtils.startLocalRegistry(port);
    }

    @Test
    void adminIsNotifiedWhenDownloaderConnectsAndStops() throws RemoteException, AlreadyBoundException, MalformedURLException, NotBoundException, UnknownHostException, InterruptedException {
        Monitor monitor = new Monitor();
        monitor.connect(host, port);

        Downloader downloader = new Downloader();
        downloader.connect(host, port);

        Assertions.assertTrue(monitor.getDownloadersNames().contains(downloader.getName()));

        downloader.stop();

        Assertions.assertFalse(monitor.getDownloadersNames().contains(downloader.getName()));

        monitor.disconnect();

    }

    @Test
    void adminGetsDownloaderWhenConnectsAndStops() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException, InterruptedException, UnknownHostException {
        Downloader downloader = new Downloader();
        downloader.connect(host, port);

        Monitor monitor = new Monitor();
        monitor.connect(host, port);

        Assertions.assertTrue(monitor.getDownloadersNames().contains(downloader.getName()));

        downloader.stop();
        Assertions.assertFalse(monitor.getDownloadersNames().contains(downloader.getName()));

        monitor.disconnect();
    }

    @Test
    void adminIsNotifiedWhenBarrelStartsAndStops() throws RemoteException, AlreadyBoundException, NotBoundException, MalformedURLException {
        Monitor monitor = new Monitor();
        monitor.connect(host, port);
        
        Barrel barrel = new Barrel();
        barrel.start(host, port);

        Assertions.assertTrue(monitor.getBarrelsNames().contains(barrel.getName()));

        barrel.stop();
        Assertions.assertFalse(monitor.getBarrelsNames().contains(barrel.getName()));

        monitor.disconnect();
    }

    @Test
    void adminGetsBarrelsWhenBarrelConnects() throws RemoteException, AlreadyBoundException, NotBoundException, MalformedURLException {
        Barrel barrel = new Barrel();
        barrel.start(host, port);

        Monitor monitor = new Monitor();
        monitor.connect(host, port);
        
        Assertions.assertTrue(monitor.getBarrelsNames().contains(barrel.getName()));

        monitor.disconnect();
        barrel.stop();
    }
    
    @Test
    void adminIsNotifiedWhenTopSearchs() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        Barrel barrel = new Barrel();
        barrel.start(host, port);

        Search search = new Search();
        search.start(host, port);

        Monitor monitor = new Monitor();
        monitor.connect(host, port);

        search.search("hello");
        Assertions.assertTrue(monitor.getTopSearches().contains("hello"));

        search.search("other");
        Assertions.assertTrue(monitor.getTopSearches().contains("other"));

        monitor.disconnect();
    }
}
