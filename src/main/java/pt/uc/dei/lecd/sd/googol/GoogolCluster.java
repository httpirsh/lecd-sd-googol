package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

/**
 * Este projeto teve como objetivo desenvolver um motor de pesquisa de páginas web, capaz de oferecer diversas
 * funcionalidades aos seus utilizadores.
 * O motor de pesquisa implementado permite a pesquisa de páginas que contenham um conjunto de termos, utilizando para
 * isso um índice invertido. A lista de páginas que contêm todos os termos da pesquisa é apresentada ordenada por
 * importância, considerando que uma página é mais relevante se tiver mais ligações de outras páginas.
 * Cada resultado da pesquisa apresenta o título da página, o URL completo e uma citação curta composta por texto da
 * página. Para além dessas informações, se o utilizador estiver registado e com login efetuado, são também apresentadas
 * todas as ligações conhecidas que apontem para essa página.
 *
 * Além disso, os utilizadores podem introduzir manualmente um novo URL para ser indexado, e a partir desse URL, o
 * sistema indexa recursivamente todas as ligações encontradas na página.
 *
 * Por último, o utilizador pode aceder a uma opção de consulta de informações gerais sobre o sistema, incluindo a lista
 * de Downloaders e Barrels ativos e as 10 pesquisas mais comuns realizadas pelos utilizadores.
 *
 * Para implementar estas funcionalidades, foram criados cinco programas: Downloaders, Index Storage Barrels, RMI Search
 * Module, RMI Client e URL Queue.
 * Os Downloaders são os componentes que obtêm as páginas Web, analisam-nas utilizando o jsoup e atualizam o índice
 * invertido através de chamadas RMI aos Barrels.
 * Os Index Storage Barrels são os componentes que armazenam todos os dados da aplicação, recebendo os dados através de
 * Java RMI, enviados pelos Downloaders.
 * O RMI Search Module é o componente visível pelos clientes e comunica com os Storage Barrels usando RMI.
 * O RMI Client é o cliente RMI usado pelos utilizadores para aceder às funcionalidades do
 * motor de pesquisa.
 * Por fim, o URL Queue é o componente que guarda os URLs encontrados pelos Downloaders na forma de uma fila.
 *
 * Para lidar com exceções que poderam ocorrer no funcionamento da aplicação, realizamos retries sempre que necessário.
 * (falar sobre callbacks e failover).
 *
 * Descrição dos teste realizados (tabela com descrição e pass/fail de cada teste).
 *
 * Para a realização do projeto, as tarefas foram distibuídas entre os membros do grupo.
 * Daniel Monteiro (nº 2021248878): responsável pelos Downloaders, Barrels, SearchModule e pela escrita do relatório.
 * Gustavo Sousa (nº): responsável pelo Client, pelo tratamento de exceções e failover...
 * Íris Sousa (nº): responsável pela página de admistração, pela URL Queue e pelos testes realizados...
 */

@Slf4j
public class GoogolCluster {

    public void init(int port) throws MalformedURLException, NotBoundException, RemoteException {
        log.info("Registry initiating in port {} ...", port);
        Registry registry = LocateRegistry.createRegistry(port);
        log.info("Registry ok");

        IndexStorageBarrel barrel1 = new IndexStorageBarrel("barrel_1");
        IndexStorageBarrel barrel2 = new IndexStorageBarrel("barrel_2");

        RmiSearchModule search = new RmiSearchModule("search");

        Downloader downloader1 = new Downloader("downloader_1");
        Downloader downloader2 = new Downloader("downloader_2");
        RMIQueue queue = new RMIQueue("queue");

        registry.rebind("googol/search", search);
        log.info("RmiSearchModule {} bound at {}", "search", "googol/search");

        registry.rebind("googol/barrels/barrel_1", barrel1);
        log.info("IndexStorageBarrel {} bound at {}", "barrel_1", "googol/barrels/barrel_1");

        registry.rebind("googol/barrels/barrel_2", barrel2);
        log.info("IndexStorageBarrel {} bound at {}", "barrel_2", "googol/barrels/barrel_2");

        registry.rebind("googol/downloaders/downloader_1", downloader1);
        log.info("Downloader {} bound at {}", "downloader_1", "googol/downloaders/downloader_1");

        registry.rebind("googol/downloaders/downloader_2", downloader2);
        log.info("Downloader {} bound at {}", "downloader_2", "googol/downloaders/downloader_2");

        registry.rebind("googol/queue", queue);
        log.info("Queue {} bound at {}", "queue", "googol/queue");

        downloader1.connectToBarrel("rmi://localhost/googol/barrels/barrel_1");
        downloader1.connectToQueue("rmi://localhost/googol/queue");
        downloader1.start();
        search.connectToBarrel("rmi://localhost/googol/barrels/barrel_1");
        search.connectToQueue("rmi://localhost/googol/queue");
    }
    public static void main(String[] args) {
        System.out.println("Googol cluster.... initialising.");
        GoogolCluster cluster = new GoogolCluster();
        try {
            cluster.init(1099);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            log.error("Failure initiating Googol Cluster.", e);
        }
        System.out.println("Googol cluster initialized successfully.");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Press any key to stop cluster...");
        scanner.nextLine();
        scanner.close();
    }
}