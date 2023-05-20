package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RMIQueueTest {
    private static Registry registry;

    @BeforeAll
    static void init() throws RemoteException{
        registry = TestUtils.getRegistryInstance(1090);
    }


    /**
     * Quando a RMIQueue é iniciada, deve registar-se no registry no uri /googol/queue.
     * 
     * @throws RemoteException quando ocorrem erros de rmi
     */
    @Test
    void shouldHaveProperNameAfterConnect() throws RemoteException {
        String host = "localhost";
        int port = 1090;
        RMIQueue queue = new RMIQueue();
        queue.connect(host, port);

        RegistryEntries entries = new RegistryEntries(registry.list());

        assertTrue(entries.hasQueue());
    }
}
