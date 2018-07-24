package com.fabric.network;


import org.hyperledger.fabric.sdk.NetworkConfig;

import java.io.File;

/**
 *@author Vishal Y
 * Singleton implementation
 * Load the network config from connection profile file.
 */
public class LoadConnectionProfile {

    private static final Integer lock = 0;
    private static LoadConnectionProfile loadConnectionProfile = null;
    private static NetworkConfig config;

    /**
     * Constructor
     *
     * @throws Exception
     */
    private LoadConnectionProfile() throws Exception {
        config = NetworkConfig.fromJsonFile(new File("src/com/fabric/config/network-config.json"));

    }

    /**
     * Get Certificate Authority config for an organization
     * @param org
     * @return CAInfo
     * @throws Exception
     */
    public static NetworkConfig.CAInfo getCaInfo(String org) throws Exception {
        if (config == null) {
            getInstance();
        }
        return config.getOrganizationInfo(org).getCertificateAuthorities().get(0);
    }

    /**
     * Get organization config
     * @param org
     * @return OrgInfo
     * @throws Exception
     */
    public static NetworkConfig.OrgInfo getOrgInfo(String org) throws Exception {
        if (config == null) {
            getInstance();
        }
        return config.getOrganizationInfo(org);
    }

    /**
     * Return class instance
     * @return LoadConnectionProfile
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
     * Return the complete configuartion info
     * @return NetworkConfig
     * @throws Exception
     */
    public static NetworkConfig getConfig() throws Exception {
        if (config == null) {
            getInstance();
        }
        return config;
    }


}
