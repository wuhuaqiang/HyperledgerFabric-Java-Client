package com.fabric.network;


import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;

import java.io.File;
import java.io.IOException;

public class LoadNetwork {

    private NetworkConfig config;

    public LoadNetwork() throws NetworkConfigurationException, IOException, InvalidArgumentException {

        this.config=NetworkConfig.fromJsonFile(new File("D:\\Fabric_Java_Client\\src\\com\\fabric\\config\\network-config.json"));

    }

    public NetworkConfig.CAInfo getCaInfo(String org) {
        return config.getOrganizationInfo(org).getCertificateAuthorities().get(0);
    }

    public NetworkConfig.OrgInfo getOrgInfo(String org) {
        return config.getOrganizationInfo(org);
    }

    public NetworkConfig getConfig() {
        return config;
    }



}
