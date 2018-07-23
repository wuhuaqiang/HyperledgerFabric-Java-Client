package com.fabric.client;

import com.fabric.network.LoadConnectionProfile;
import com.fabric.participant.UserContext;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 *
 */
public class FabricClientWrapper {

    //private FabricClientWrapper fabricClient;
    private HFClient hfClient;

    /**
     *
     * @param userName
     * @param org
     * @throws Exception
     */
    private FabricClientWrapper(String userName, String org) throws Exception {
        this.hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        this.hfClient.setCryptoSuite(cryptoSuite);
        UserContext userContext = CAClientWrapper.getUserContext(userName, org);
        this.hfClient.setUserContext(userContext);
    }

    /**
     *
     * @param userName
     * @param org
     * @return
     * @throws Exception
     */
    public static FabricClientWrapper getFabricClient(String userName, String org) throws Exception {
        return new FabricClientWrapper(userName, org);
    }

    /**
     *
     * @param channelName
     * @return
     * @throws Exception
     */
    public Channel getChannelClient(String channelName) throws Exception {
        Channel channel = hfClient.loadChannelFromConfig(channelName, LoadConnectionProfile.getConfig());
        channel.initialize();
        return channel;
    }

    /**
     *
     * @return
     */
    public HFClient getHfClient() {
        return hfClient;
    }

}