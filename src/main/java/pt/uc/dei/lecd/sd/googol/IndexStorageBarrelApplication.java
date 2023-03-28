package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexStorageBarrelApplication {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        InterfaceBarrel barrelService = (InterfaceBarrel) new IndexStorageBarrel();
        Naming.rebind("//localhost/barrel", barrelService);
        log.info("IndexStorageBarrelApplication binded at //localhost/barrel");
    }
}
