package com.fabric.network;


import org.hyperledger.fabric.sdk.NetworkConfig;

import java.io.File;
import java.util.logging.Logger;

public class LoadConnectionProfile {

    private NetworkConfig config;
    private static LoadConnectionProfile loadConnectionProfile = null;
    private  static final Integer lock=0;

    private LoadConnectionProfile() throws Exception {

        this.config = NetworkConfig.fromJsonFile(new File("D:\\Fabric_Java_Client\\src\\com\\fabric\\config\\network-config.json"));

    }

    public NetworkConfig.CAInfo getCaInfo(String org) {
        return config.getOrganizationInfo(org).getCertificateAuthorities().get(0);
    }

    public NetworkConfig.OrgInfo getOrgInfo(String org) {
        return config.getOrganizationInfo(org);
    }

    public static LoadConnectionProfile getInstance() throws Exception {
        synchronized (lock){
        if (loadConnectionProfile == null) {
             loadConnectionProfile=new LoadConnectionProfile();
        }
        }
        return loadConnectionProfile;
    }

    public NetworkConfig getConfig() {
        return config;
    }


}
