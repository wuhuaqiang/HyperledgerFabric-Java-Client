package com.fabric.client;

import com.fabric.network.LoadConnectionProfile;
import com.fabric.participant.UserContext;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 * @author Vishal Y
 * HFClient Wrapper
 */
public class FabricClientWrapper {

    //private FabricClientWrapper fabricClient;
    private HFClient hfClient;

    /**
     *Constructor, intantiate an object HFClient class
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
     *Return instance of FabricClientWrapper
     * @param userName
     * @param org
     * @return FabricClientWrapper
     * @throws Exception
     */
    public static FabricClientWrapper getFabricClient(String userName, String org) throws Exception {
        return new FabricClientWrapper(userName, org);
    }

    /**
     * Return an instance of Channel. The channel client provide various transaction functions
     * @param channelName
     * @return Channel
     * @throws Exception
     */
    public Channel getChannelClient(String channelName) throws Exception {
        Channel channel = hfClient.loadChannelFromConfig(channelName, LoadConnectionProfile.getConfig());
        channel.initialize();
        return channel;
    }

    /**
     *Return HFClient object
     * @return HFClient
     */
    public HFClient getHfClient() {
        return hfClient;
    }

}
