# Service for testing Apache Camel with ActiveMQ Artemis
Library name: camel-artemis

  Для примера работы с очередью по протоколу AMQP была использована библиотеке apache camel и брокер сообщений ActiveMQ Artemis.  
В примере создается одна входящая очередь (inputqueue) и одна исходящая (outputorder).
Для тестов используется JMeter. Необходимя конфигурация для JMeter выложена в соответсвующей папке: Artemis_Camel.jmx
По умолчанию, в JMeter создается 5 сообщений из 10 потоков.
В параметрах входящей очереди максимальное количество потоков ConcurrentConsumers = 10.

Для проведения тестирования необходимо сначала запустить ActiveMQ Artemis.
Затем запустить сервис camel-artemis. После этого открыть приложенную конфигурацию в JMeter и запутить выполнение.

После обработки сообщений в логе выводиться информация:
route1 - ******** ROUTING FROM INPUT QUEUE TO OUTPUT
route2 - ******** PROCESS MESSAGE FROM OUTPUT QUEUE
CamelConsumer - START CONSUME MESSAGE, docId: 1 docType: order
CamelConsumer - FINISH CONSUME MESSAGE, docId: 1 docType: order. Total consumed: 50

Ссылки на использованную документацию:

Зависимости:
https://camel.apache.org/camel-spring-boot/latest/activemq-starter.html

Запуск Artemis
https://activemq.apache.org/components/artemis/documentation/latest/using-server.html

JMeter:
Установка плагина Artemis в Jmeter:
https://github.com/apache/activemq-artemis/tree/master/examples/perf/jmeter
Запуск:
https://jmeter.apache.org/usermanual/get-started.html

Трудности при реализации:
Нет стандартной библиотеки camel-artemis-starter. 
Например такая как org.apache.camel.springboot:camel-rabbitmq-starter.
В spring есть библиотека spring-boot-starter-artemis, но это не camel, и там нет camel контекста для маршрутизации.
Есть аналогия camel-jms-starter.

## Example
java -jar camel-artemis-1.0.0.jar

## Build
mvn clean install
