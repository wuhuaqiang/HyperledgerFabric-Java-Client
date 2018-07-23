package com.fabric.client;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionInfo;

import java.util.Collection;

public class ChannelWrapper {
    FabricClient fc;
    private HFClient hfClient;

    private ChannelWrapper(String userName, String org) throws Exception {
        this.fc = FabricClient.getFabricClient(userName, org);
        this.hfClient = fc.getHfClient();
    }

    public static ChannelWrapper getChannelWrapperInstance(String userName, String org) throws Exception {
        return new ChannelWrapper(userName, org);
    }

    public void queryByTransactionId(String txnId, String channelName) throws Exception {
        Channel channel = fc.getChannelClient(channelName);
        Collection<Peer> peers = channel.getPeers();
        for (Peer peer : peers) {
            TransactionInfo info = channel.queryTransactionByID(peer, txnId);
            System.out.println(info);
        }
    }
}
