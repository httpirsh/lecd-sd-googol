package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RemoteQueueTest {
    private static Registry registry;

    @BeforeAll
    static void init() throws RemoteException{
        registry = TestUtils.startLocalRegistry(1090);
    }


    /**
     * Quando a RMIQueue é iniciada, deve registar-se no registry no uri /googol/queue.
     * 
     * @throws RemoteException quando ocorrem erros de rmi
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
    @Test
    void shouldHaveProperNameAfterConnect() throws RemoteException, AlreadyBoundException, NotBoundException {
        String host = "localhost";
        int port = 1090;
        RemoteQueue queue = new RemoteQueue();
        queue.start(host, port);

        GoogolRegistry entries = new GoogolRegistry(registry.list());

        assertTrue(entries.hasQueue());
        queue.stop();
    }
}
