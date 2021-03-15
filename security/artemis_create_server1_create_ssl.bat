
set PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_241\bin

rem:#Create keystore for broker
keytool -genkey -alias broker -keystore broker_ks.jks -storepass user555 -keypass user555 -keyalg RSA -validity 360 -keysize 2048 -dname "CN=ArtemisBroker, OU=Artemis, O=AMQ, L=AMQ, S=AMQ, C=RU" -ext san=ip:127.0.0.1
keytool -export -alias broker -keystore broker_ks.jks -file broker_cert.cer -storepass user555
keytool -importkeystore -srckeystore broker_ks.jks -destkeystore broker_ks.p12 -deststoretype pkcs12 -srcstorepass user555 -deststorepass user555

rem:#Create keystore for client consumer
keytool -import -alias consumer -keystore client_consumer_ts.p12 -file broker_cert.cer -storepass user555 -keypass user555 -deststoretype pkcs12 -noprompt
keytool -genkey -alias CA -keystore client_consumer_ks.jks -storepass user555 -keypass user555 -keyalg RSA -validity 360 -keysize 2048 -dname "CN=ArtemisClientConsumer, OU=AMQ, O=AMQ, L=AMQ, S=AMQ, C=RU" -ext san=ip:127.0.0.1
keytool -export -alias CA -keystore client_consumer_ks.jks -file CA_cert.cer -storepass user555
keytool -importkeystore -srckeystore client_consumer_ks.jks -destkeystore client_consumer_ks.p12 -deststoretype pkcs12 -srcstorepass user555 -deststorepass user555

rem:#Create keystore for client sender
keytool -import -alias sender -keystore client_sender_ts.p12 -file broker_cert.cer -storepass user555 -keypass user555 -deststoretype pkcs12 -noprompt
keytool -genkey -alias CA -keystore client_sender_ks.jks -storepass user555 -keypass user555 -keyalg RSA -validity 360 -keysize 2048 -dname "CN=ArtemisClientSender, OU=AMQ, O=AMQ, L=AMQ, S=AMQ, C=RU" -ext san=ip:127.0.0.1
keytool -export -alias CA -keystore client_sender_ks.jks -file CA_cert.cer -storepass user555
keytool -importkeystore -srckeystore client_sender_ks.jks -destkeystore client_sender_ks.p12 -deststoretype pkcs12 -srcstorepass user555 -deststorepass user555

rem:#Create keystore for broker node
keytool -import -alias node -keystore broker_node_ts.p12 -file broker_cert.cer -storepass user555 -keypass user555 -deststoretype pkcs12 -noprompt
keytool -genkey -alias CA -keystore broker_node_ks.jks -storepass user555 -keypass user555 -keyalg RSA -validity 360 -keysize 2048 -dname "CN=ArtemisBrokerNode, OU=AMQ, O=AMQ, L=AMQ, S=AMQ, C=RU" -ext san=ip:127.0.0.1
keytool -export -alias CA -keystore broker_node_ks.jks -file CA_cert.cer -storepass user555
keytool -importkeystore -srckeystore broker_node_ks.jks -destkeystore broker_node_ks.p12 -deststoretype pkcs12 -srcstorepass user555 -deststorepass user555

rem:#Create keystore for browser
keytool -import -alias browser -keystore client_browser_ts.p12 -file broker_cert.cer -storepass user555 -keypass user555 -deststoretype pkcs12 -noprompt
keytool -genkey -alias CA -keystore client_browser_ks.jks -storepass user555 -keypass user555 -keyalg RSA -validity 360 -keysize 2048 -dname "CN=ArtemisBrowser, OU=AMQ, O=AMQ, L=AMQ, S=AMQ, C=RU" -ext san=ip:127.0.0.1
keytool -export -alias CA -keystore client_browser_ks.jks -file CA_cert.cer -storepass user555
keytool -importkeystore -srckeystore client_browser_ks.jks -destkeystore client_browser_ks.p12 -deststoretype pkcs12 -srcstorepass user555 -deststorepass user555

keytool -import -alias CA -keystore broker_ts.p12 -file CA_cert.cer -storepass user555 -keypass user555 -deststoretype pkcs12 -noprompt