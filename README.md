# lecd-sd-googol
Googol: Motor de pesquisa de páginas Web.

Como executar o projeto:
* Para correr as várias funcionalidades é necessário escrever no terminal "java -jar ", seguido da funcionalidade desejada. Para correr qualquer outra, sejam barrels ou downloaders, é necessário abrir outro terminal de modo a correrem em paralelo.

A aplicação Googol primeiro (Googol Cluster), de modo a inicializar e configurar os componentes da aplicação, os quais 2 downloaders e 2 barrels.
Numa fase seguinte, basta correr a classe GoogolClient, que inicia um RmiClient e connecta-o com o cluster. 
No RmiClient será pedido ao utilizador as suas informações e respetivas intenções, através de um menu.


Este projeto teve como objetivo desenvolver um motor de pesquisa de páginas web, capaz de oferecer diversas 
funcionalidades aos seus utilizadores. 
* O motor de pesquisa implementado permite a pesquisa de páginas que contenham um conjunto de termos, utilizando para
isso um índice invertido. A lista de páginas que contêm todos os termos da pesquisa é apresentada ordenada por
importância, considerando que uma página é mais relevante se tiver mais ligações de outras páginas.
* Cada resultado da pesquisa apresenta o título da página, o URL completo e uma citação curta composta por texto da 
página. Para além dessas informações, se o utilizador estiver registado e com login efetuado, são também apresentadas 
todas as ligações conhecidas que apontem para essa página.

* Além disso, os utilizadores podem introduzir manualmente um novo URL para ser indexado, e a partir desse URL, o
sistema indexa recursivamente todas as ligações encontradas na página.

* Por último, o utilizador pode aceder a uma opção de consulta de informações gerais sobre o sistema, incluindo a lista
de Downloaders e Barrels ativos e as 10 pesquisas mais comuns realizadas pelos utilizadores.

* Para implementar estas funcionalidades, foram criados cinco programas: Downloaders, Index Storage Barrels, RMI Search
Module, RMI Client e URL Queue.
* Os Downloaders são os componentes que obtêm as páginas Web, analisam-nas utilizando o jsoup e atualizam o índice
invertido através de chamadas RMI aos Barrels.
* Os Index Storage Barrels são os componentes que armazenam todos os dados da aplicação, recebendo os dados através de
Java RMI, enviados pelos Downloaders.
* O RMI Search Module é o componente visível pelos clientes e comunica com os Storage Barrels usando RMI.
* O RMI Client é o cliente RMI usado pelos utilizadores para aceder às funcionalidades do
motor de pesquisa.
* Por fim, o URL Queue é o componente que guarda os URLs encontrados pelos Downloaders na forma de uma fila.
* Para lidar com exceções que poderam ocorrer no funcionamento da aplicação, realizamos retries sempre que necessário.
(falar sobre callbacks e failover).
* Descrição dos teste realizados (tabela com descrição e pass/fail de cada teste).

Para a realização do projeto, as tarefas foram distibuídas entre os membros do grupo.

* Daniel Monteiro:
Síntese das classes Donwloader e respetivos métodos
(Indexar novo URL, Pesquisar páginas que contenham um conjunto de termos, Resultados de pesquisa ordenados por importância);
Síntese das classes IndexStorageBarrel, RMISearchModule e respetivas interfaces;

* Gustavo Sousa:
Síntese da classe RMI Cliente e respetivos métodos (registo dos vários utilizadores, iniciar e síntese do menu);
Tratamento de exceções e FailOver;

* Íris Sousa:
Página de administração atualizada em tempo real e respetivos métodos;
Criação das classes Queue, URLQueue e respetivos métodos para a sua conexão (connectToQueue(), enqueue(), dequeue());
Criação dos testes para testagem (Test);
Criação dos objetos (downloaders e barrels) e respetiva consola para os inicializar;
