package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdminModuleTests {
    
    private static final String host = "localhost";
    private static final int port = 1090;

    @Test
    void Should_get_called_When_barrels_change() throws RemoteException, AlreadyBoundException, MalformedURLException, NotBoundException {
        TestUtils.startLocalRegistry(port);

        AdminConsole admin = new AdminConsole();
        admin.connect(host, port);

        Barrel barrel = new Barrel();
        barrel.start(host, port);

        Downloader downloader = new Downloader();
        downloader.connect(host, port);
        downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things");

        //admin.getBarrelsNames().contains("barrel_1");
        Assertions.assertTrue(admin.getDownloadersNames().contains(downloader.getName()));
        //admin.getBarrelPages("barrel_1").contains("Stranger_Things");

    }
}
