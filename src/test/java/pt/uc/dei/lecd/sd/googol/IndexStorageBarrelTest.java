package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.junit.jupiter.api.Test;

public class IndexStorageBarrelTest {
    @Test
    void Should_True_When_StartedAndStoppedMultipleBarrels() throws RemoteException {
        int port = 1090;
        LocateRegistry.createRegistry(port);

        IndexStorageBarrel barrel1 = new IndexStorageBarrel("barrel_1");
        assertTrue(barrel1.start(port, "googol"));

        IndexStorageBarrel barrel2 = new IndexStorageBarrel("barrel_2");
        assertTrue(barrel2.start(port, "googol"));

        assertTrue(barrel1.stop());

        assertTrue(barrel2.stop());
    }
}
