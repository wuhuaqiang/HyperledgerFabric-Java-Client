# Fabric-Java-Client(Work in progress :) )
A Java based fabric client.

<b><u>Prerequsite:</u></b>

<b>1. Fabric network:</b> A running fabric network. See [this](https://hyperledger-fabric.readthedocs.io/en/release-1.2/build_network.html){:target="_blank"} tutorial to quickly bootstap a network. To practice on this 
client, my recommendation would be to start a network on <b>IBM Blockchain Platform</b>. It enables fast and easy setup of a multi org blockchain network and yes, it is free of cost under Starter plan.
[Here](https://console.bluemix.net/docs/services/blockchain/starter_plan.html#overview){:target="_blank"} is a how you can setup a blockchain network in minutes.

<b>2. A chaincode:</b> A sample chaincode(written in Golang) is provided under config/chaincode/ directory. You have optioon to write your own.

<b>3. Connection profile:</b> The application will load a network <b>connection profile </b>(/src/com/fabric/config/network-config.json) file and then it will be used by fabric java client to simplify the steps needed to setup and use the network. The connection profile has specific addresses and settings of network items.
If your network runs on IBM Blockchain platform, then <b>Connection Profile</b> can be downloaded as shown here:

![alt text](https://github.com/vishal3152/HyperledgerFabric-Java-Client/blob/master/images/image.png)

A sample connection profile has been provided under directory /src/com/fabric/config/.
A detailed documenation on how to create a connection profile for your network is availble at:
https://hyperledger.github.io/composer/latest/reference/connectionprofile

<b>4. Buildpath dependencies:</b> Access to public maven repo is blocked in my organisation. Downlaod <b>fabric-sdk-java-1.1.0-jar-with-dependencies.jar</b> from http://central.maven.org/maven2/org/hyperledger/fabric-sdk-java/fabric-sdk-java/1.1.0/ and add it to your build path.
