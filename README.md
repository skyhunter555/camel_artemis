# Service for testing Apache Camel with ActiveMQ Artemis
Library name: camel-artemis

  Для примера работы с очередью по протоколу AMQP была использована библиотеке apache camel и брокер сообщений ActiveMQ Artemis.  
В примере создается одна входящая очередь inputToOutputQueue.</br>

Для работы в безопасном режиме с шифрованием SSL, были созданы самоподписанные сертификаты и настроена конфигурация</br>
в брокере и клиенте. Так же добавлен сертификат и настройки для использования web-консоли через SSL.</br>
Настройка конфигурации брокера находится в папке artemis-tls-config: broker.xml.</br>
Для подключения режима ssl необходимо скопировать сертификаты из security в указанную в конфиге папку.</br>  

Для генерации самоподписанных сертификатов необходимо запустить</br>
security\artemis_create_server1_create_ssl.bat

После обработки сообщений в логе выводиться информация:

route1 - ******** ROUTING FROM INPUT QUEUE TO OUTPUT

route2 - ******** PROCESS MESSAGE FROM OUTPUT QUEUE

CamelConsumer - START CONSUME MESSAGE, docId: 1 docType: order

CamelConsumer - FINISH CONSUME MESSAGE, docId: 1 docType: order. Total consumed: 50

Ссылки на использованную документацию:

Зависимости:</br>
https://camel.apache.org/camel-spring-boot/latest/activemq-starter.html

Запуск Artemis</br>
https://activemq.apache.org/components/artemis/documentation/latest/using-server.html

JMeter:</br>
Установка плагина Artemis в Jmeter:</br>
https://github.com/apache/activemq-artemis/tree/master/examples/perf/jmeter</br>
Запуск:</br>
https://jmeter.apache.org/usermanual/get-started.html

## Example
java -jar camel-artemis-1.0.0.jar

## Build
mvn clean install
