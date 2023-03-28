package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void init() throws RemoteException {
        cliente = new Cliente(new IndexStorageBarrel());
        //cliente = new Cliente((InterfaceBarrel) Naming.lookup("//localhost/barrel"));
    }

    @Test
    void testWhenPingIsCalledOnBarrelShouldReceivePong() throws RemoteException {
        String response = cliente.callPing();
        Assertions.assertEquals("pong", response);
    }


}
