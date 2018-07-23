package com.fabric.client;

import com.fabric.participant.UserContext;
import org.hyperledger.fabric.sdk.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;


public class ChannelWrapper {
    private FabricClient fc;
    private HFClient hfClient;
    private String userName;
    private String org;


    private ChannelWrapper(String userName, String org) throws Exception {
        this.fc = FabricClient.getFabricClient(userName, org);
        this.hfClient = fc.getHfClient();
        this.userName = userName;
        this.org = org;
    }

    public static ChannelWrapper getChannelWrapperInstance(String userName, String org) throws Exception {
        return new ChannelWrapper(userName, org);
    }

    public Collection<ProposalResponse> queryChaincode(String channelName, String chaincodeName, String fcn, String... args) throws Exception {
        Channel channel = fc.getChannelClient(channelName);
        QueryByChaincodeRequest queryReq = QueryByChaincodeRequest.newInstance(CAClient.getUserContext(this.userName, this.org));
        queryReq.setChaincodeID(ChaincodeID.newBuilder().setName(chaincodeName).build());
        queryReq.setFcn(fcn);
        if (args != null) {
            queryReq.setArgs(args);
        }
        Collection<ProposalResponse> queryResponse = channel.queryByChaincode(queryReq);
        return queryResponse;


    }

    public CompletableFuture<BlockEvent.TransactionEvent> invokeChainCode(String channelName, String chaincodeName, String fcn, String[] args) throws Exception {
        final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
        final String EXPECTED_EVENT_NAME = "event";

        Channel channel = fc.getChannelClient(channelName);
        UserContext userContext = CAClient.getUserContext(this.userName, this.org);
        TransactionProposalRequest transactionProposalRequest = TransactionProposalRequest.newInstance(userContext);
        transactionProposalRequest.setChaincodeID(ChaincodeID.newBuilder().setName(chaincodeName).build());
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(110000);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", ":)".getBytes(UTF_8));
        tm.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
        transactionProposalRequest.setTransientMap(tm);

        Collection<ProposalResponse> response = channel.sendTransactionProposal(transactionProposalRequest);
        for (ProposalResponse resp : response) {
            ChaincodeResponse.Status status = resp.getStatus();
            //Logger.getLogger(ChannelWrapper.class.getName()).log(Level.INFO, "Invoked chaincode " + chaincodeName + " - " + fcn + ". Status - " + status);

            if (status.getStatus() != 200) {
                throw new Exception(resp.getMessage());
            }
        }
        CompletableFuture<BlockEvent.TransactionEvent> commitResp = channel.sendTransaction(response, userContext);
        Logger.getLogger(ChannelWrapper.class.getName()).log(Level.INFO, "Invoked chaincode " + chaincodeName + " - " + fcn + ". Status - " + commitResp.toString());
        return commitResp;
    }

    public TransactionInfo queryByTransactionId(String txnId, String channelName) throws Exception {
        Channel channel = fc.getChannelClient(channelName);
        TransactionInfo info = null;
        Collection<Peer> peers = channel.getPeers();
        for (Peer peer : peers) {
            info = channel.queryTransactionByID(peer, txnId);
            System.out.println(info);

        }
        return info;
    }


}
