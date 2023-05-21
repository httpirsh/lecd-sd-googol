package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IndexStorageBarrelTest {

    private static Registry registry;
    private static final int port = 1090;

    @BeforeAll
    static void init() throws RemoteException{
        registry = TestUtils.getRegistryInstance(port);
    }
    
    @Test
    void Should_True_When_StartedAndStoppedMultipleBarrels() throws RemoteException {

        IndexStorageBarrel barrel1 = new IndexStorageBarrel();
        assertTrue(barrel1.start("localhost", port, "/googol"));

        IndexStorageBarrel barrel2 = new IndexStorageBarrel();
        assertTrue(barrel2.start("localhost", port, "/googol"));

        assertTrue(barrel1.stop());

        assertTrue(barrel2.stop());
    }
}
