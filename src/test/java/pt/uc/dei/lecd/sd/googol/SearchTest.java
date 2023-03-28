package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchTest {


    void testWhenPingIsCalledOnBarrelShouldReceivePong() throws MalformedURLException, RemoteException, NotBoundException {

        IndexStorageBarrel barrel = (IndexStorageBarrel) Naming.lookup("//localhost/barrel");

        String response = barrel.ping();

        Assertions.assertEquals("pong", response);
    }
}
