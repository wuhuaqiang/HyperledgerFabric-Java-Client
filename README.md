# Fabric-Java-Client(Work in progress :) )
A Java based fabric client.

<b>Prerequsite:</b>

<b>1. Connection profile:</b> The application will load a network <b>connection profile </b>(/src/com/fabric/config/network-config.json) file and then it will be used by fabric java client to simplify the steps needed to setup and use the network. The connection profile has specific addresses and settings of network items.
A sample connection profile has been provided under directory /src/com/fabric/config/.
A detailed documenation on how to create a connection profile for your network is availble at:
https://hyperledger.github.io/composer/latest/reference/connectionprofile

<b>2. Buildpath dependencies:</b>Access to public maven repo is blocked in my organisation. Downlaod fabric-sdk-java-1.1.0-jar-with-dependencies.jar from http://central.maven.org/maven2/org/hyperledger/fabric-sdk-java/fabric-sdk-java/1.1.0/ and add it to your build path.
