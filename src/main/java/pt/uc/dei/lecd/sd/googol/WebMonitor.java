package pt.uc.dei.lecd.sd.googol;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebMonitor {

    private static Monitor monitor;

    public static Monitor getMonitor() {
        return monitor;
    }

    public static void main(String[] args) {

        ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            monitor = new Monitor();
            monitor.connect(host, port);
            SpringApplication.run(WebMonitor.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    @PreDestroy
    public void preDestroy() throws AccessException, RemoteException, NotBoundException {
        monitor.disconnect();
    }
}
