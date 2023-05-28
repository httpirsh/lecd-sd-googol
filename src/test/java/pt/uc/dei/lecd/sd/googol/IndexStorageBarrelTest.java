package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IndexStorageBarrelTest {

    private static final int port = 1090;

    @BeforeAll
    static void init() throws RemoteException{
        TestUtils.startLocalRegistry(port);
    }
    
    @Test
    void Should_True_When_StartedAndStoppedMultipleBarrels() throws RemoteException, NotBoundException, MalformedURLException {

        Barrel barrel1 = new Barrel();
        assertTrue(barrel1.start("localhost", port));

        Barrel barrel2 = new Barrel();
        assertTrue(barrel2.start("localhost", port));

        assertTrue(barrel1.stop());

        assertTrue(barrel2.stop());
    }
}
