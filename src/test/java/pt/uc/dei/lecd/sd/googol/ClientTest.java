package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ClientTest {

    private Client cliente;

    @BeforeEach
    void init() throws RemoteException, MalformedURLException, NotBoundException {
        cliente = new Client(new IndexStorageBarrel());
        //cliente = new Cliente((InterfaceBarrel) Naming.lookup("//localhost/barrel"));
    }

    @Test
    void testWhenPingIsCalledOnBarrelShouldReceivePong() throws RemoteException {
        String response = cliente.callPing();
        Assertions.assertEquals("pong", response);
    }


}
