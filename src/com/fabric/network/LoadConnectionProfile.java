package com.fabric.network;


import org.hyperledger.fabric.sdk.NetworkConfig;

import java.io.File;

/**
 *
 */
public class LoadConnectionProfile {

    private static final Integer lock = 0;
    private static LoadConnectionProfile loadConnectionProfile = null;
    private static NetworkConfig config;

    /**
     *
     * @throws Exception
     */
    private LoadConnectionProfile() throws Exception {

        config = NetworkConfig.fromJsonFile(new File("D:\\Fabric-Java-Client\\src\\com\\fabric\\config\\network-config.json"));

    }

    /**
     *
     * @param org
     * @return
     * @throws Exception
     */
    public static NetworkConfig.CAInfo getCaInfo(String org) throws Exception {
        if (config == null) {
            getInstance();
        }
        return config.getOrganizationInfo(org).getCertificateAuthorities().get(0);
    }

    /**
     *
     * @param org
     * @return
     * @throws Exception
     */
    public static NetworkConfig.OrgInfo getOrgInfo(String org) throws Exception {
        if (config == null) {
            getInstance();
        }
        return config.getOrganizationInfo(org);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static LoadConnectionProfile getInstance() throws Exception {

        synchronized (lock) {
            if (loadConnectionProfile == null) {
                loadConnectionProfile = new LoadConnectionProfile();
            }
        }
        return loadConnectionProfile;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static NetworkConfig getConfig() throws Exception {
        if (config == null) {
            getInstance();
        }
        return config;
    }


}