package com.fabric.client;

import com.fabric.network.LoadConnectionProfile;
import com.fabric.participant.UserContext;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

public class FabricClient {

    //private FabricClient fabricClient;
    private HFClient hfClient;


    private FabricClient(String userName, String org) throws Exception {
        this.hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        this.hfClient.setCryptoSuite(cryptoSuite);
        UserContext userContext = CAClient.getUserContext(userName, org);
        this.hfClient.setUserContext(userContext);
    }

    public static FabricClient getFabricClient(String userName, String org) throws Exception {
        return new FabricClient(userName, org);
    }

    public Channel getChannelClient(String channelName) throws Exception {
        Channel channel = hfClient.loadChannelFromConfig(channelName, LoadConnectionProfile.getConfig());
        channel.initialize();
        return channel;
    }

    public HFClient getHfClient() {
        return hfClient;
    }

}