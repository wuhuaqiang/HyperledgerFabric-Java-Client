# Fabric-Java-Client(Work in progress :) )
A Java-based fabric client.

<b><u>Prerequsite:</u></b>

<b>1. Fabric network:</b> A running fabric network. See [this](https://hyperledger-fabric.readthedocs.io/en/release-1.2/build_network.html) tutorial to quickly bootstrap a network. To practice on this client, my recommendation would be to start a network on <b>IBM Blockchain Platform</b>. It enables fast and easy setup of a multi-org blockchain network and yes, it is free of cost under Starter plan.
[Here](https://console.bluemix.net/docs/services/blockchain/starter_plan.html#overview) is a how you can set up a blockchain network in minutes.

<b>2. A chain code:</b> A sample chain code(written in Golang) is provided under config/chaincode/ directory. You have options to write your own.

<b>3. Connection profile:</b> The application will load a network <b>connection profile </b>(/src/com/fabric/config/network-config.json) file and then it will be used by fabric java client to simplify the steps needed to set up and use the network. The connection profile has specific addresses and settings of network items.
If your network runs on IBM Blockchain platform, then <b>Connection Profile</b> can be downloaded as shown here:

![pic](https://github.com/vishal3152/HyperledgerFabric-Java-Client/blob/master/images/image.png)

A sample connection profile has been provided under directory /src/com/fabric/config/.
A detailed documentation on how to create a connection profile for your network is available [here.](
https://hyperledger.github.io/composer/latest/reference/connectionprofile)

<b>4. Buildpath dependencies:</b> I am learning Maven, so expect a pom.xml in future :). For now, download <b>fabric-sdk-java-1.1.0-jar-with-dependencies.jar</b> from [maven repo](http://central.maven.org/maven2/org/hyperledger/fabric-sdk-java/fabric-sdk-java/1.1.0/) and add it to your build path.


<b>Setting up client:</b>

<b>Step 1: Enrolling admin:</b>

When we bootstrapped our blockchain network, an admin user was <b>registered</b> with the MSP provider, fabric-ca server in our case.
Now we need user-context of the admin user for our client to perform operations on blockchain network. We need to send an enrol call to CA server and retrieve the enrollment certificate(long-term identity) for the admin. Later on,
we will use this enrollment certificate to construct admin's user-context.
To make the enrol call, we require an identity(username), admin in this case, and its secret key. If you are using one of the samples, then the secret key would be adminpw. The secret key for network running on IBM cloud platform can be found inside the connection profile :- under 'registrar' object, find 'enrollId' and 'enrollSecret' property.

```Java
   public void enrollAdmin(String name, String secret) throws Exception {
        UserContext adminContext;

        //read admin context from client's msp folder
        adminContext = Util.readUserContext(org, name);
        if (adminContext != null) {

            //admin context found; admin is already enrolled.
            return;
        }

        //make an enroll request to ca server.
        Enrollment enrollment = hfcaClient.enroll(name, secret);

        //construct admin context
        adminContext = new UserContext();
        adminContext.setName(name);
        adminContext.setEnrollment(enrollment);
        adminContext.setAffiliation(LoadConnectionProfile.getOrgInfo(org).getName());
        adminContext.setMspId(LoadConnectionProfile.getOrgInfo(org).getMspId());

        //store admin context in msp folder
        Util.writeUserContext(adminContext);

    }
```

On successful enrolment, admin user context will be saved under msp folder.

<b>Step 1: Registering and enrolling user:</b>
Now, of course, we would not like to use this admin user to invoke all transaction on blockchain network. We may require access control decisions in our chaincode based on the attributes of the identity of the client (i.e. the invoker of the chain code), called [ABAC](https://hyperledger-fabric-ca.readthedocs.io/en/release-1.1/users-guide.html#attribute-based-access-control) in short. so the whole point here is to enable other users to interact with the network and this is a two-step process:
- 1. <b>Register the user with CA:</b>

     The identity performing the registration, admin in our case, must be already enrolled and have proper access rights. As we have already enrolled admin in step 1, we would use admin(registrar) user-context to register new users.

```Java
     public void registerUser(String userName, String registrarAdmin){
        UserContext userContext;

        //read user context from msp folder
        userContext = Util.readUserContext(org, userName);
        if (userContext != null) {
            //user is already registered and enrolled, do nothing.
            return;
        }

        //User is not registered, construct a registeration request
        RegistrationRequest regRequest = new RegistrationRequest(userName, org);

        //Reterive registrar(admin) usercontext from msp folder
        UserContext registrarContext = Util.readUserContext(org, registrarAdmin);

        // send a registeration request for user with admin as registrar
        String enrollSecret = hfcaClient.register(regRequest, registrarContext);
        .
        .
        //If registration is successful, in return we will get a secret key for registered user. We will use the secret key to enrol TJ with the client in the next step.

```

- 2. <b>Enrol user:</b>
```Java

        // make enrol call to ca server
        Enrollment enrollment = hfcaClient.enroll(userName, enrollSecret);

        // create user context for enrolled user
        userContext = new UserContext();
        userContext.setMspId(LoadConnectionProfile.getOrgInfo(org).getMspId());
        userContext.setAffiliation(org); //organization user belongs to
        userContext.setEnrollment(enrollment);
        userContext.setName(userName);

        // Save user context in msp folder.
        Util.writeUserContext(userContext);
        }
```

On successful enrolment, user-context for TJ will be saved under msp folder.


