package com.fabric.client;

import com.fabric.participant.UserContext;
import org.hyperledger.fabric.sdk.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Vishal Y
 */

public class ChannelWrapper {
    private FabricClientWrapper fc;
    private String userName;
    private String org;

    /**
     * @param userName
     * @param org
     * @throws Exception
     */

    private ChannelWrapper(String userName, String org) {
        this.userName = userName;
        this.org = org;
        init();
    }

    /**
     * @param userName
     * @param org
     * @return
     */
    public static ChannelWrapper getChannelWrapperInstance(String userName, String org) {
        return new ChannelWrapper(userName, org);
    }

    /**
     *
     */
    void init() {

        this.fc = FabricClientWrapper.getFabricClient(userName, org);
    }

    /**
     * @param channelName
     * @param chaincodeName
     * @param fcn
     * @param args
     * @return Collection<ProposalResponse>
     */
    public Collection<ProposalResponse> queryChaincode(String channelName, String chaincodeName, String fcn, String... args) {
        Collection<ProposalResponse> queryResponse = null;
        Channel channel = null;
        try {
            channel = fc.getChannelClient(channelName);
            QueryByChaincodeRequest queryReq = QueryByChaincodeRequest.newInstance(CAClientWrapper.getUserContext(this.userName, this.org));
            queryReq.setChaincodeID(ChaincodeID.newBuilder().setName(chaincodeName).build());
            queryReq.setFcn(fcn);
            if (args != null) {
                queryReq.setArgs(args);
            }
            queryResponse = channel.queryByChaincode(queryReq);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown(true);
            return queryResponse;
        }

    }

    /**
     * @param channelName
     * @param chaincodeName
     * @param fcn
     * @param args
     * @return CompletableFuture<BlockEvent.TransactionEvent>
     */
    public CompletableFuture<BlockEvent.TransactionEvent> invokeChainCode(String channelName, String
            chaincodeName, String fcn, String[] args) {
        CompletableFuture<BlockEvent.TransactionEvent> commitResp = null;
        Channel channel = null;
        try {
            channel = fc.getChannelClient(channelName);
            UserContext userContext = CAClientWrapper.getUserContext(this.userName, this.org);
            TransactionProposalRequest transactionProposalRequest = TransactionProposalRequest.newInstance(userContext);
            transactionProposalRequest.setChaincodeID(ChaincodeID.newBuilder().setName(chaincodeName).build());
            transactionProposalRequest.setFcn(fcn);
            transactionProposalRequest.setArgs(args);
            transactionProposalRequest.setProposalWaitTime(110000);

            Map<String, byte[]> tm = new HashMap<>();
            tm.put("HyperLedgerFabric", "Java - SDK".getBytes(UTF_8));
            tm.put("method", fcn.getBytes(UTF_8));
            transactionProposalRequest.setTransientMap(tm);

            Collection<ProposalResponse> response = channel.sendTransactionProposal(transactionProposalRequest);
            for (ProposalResponse resp : response) {
                ChaincodeResponse.Status status = resp.getStatus();
                Logger.getLogger(ChannelWrapper.class.getName()).log(Level.WARNING, "Invoked chaincode " + chaincodeName + " - " + fcn + ". Status - " + status);

                if (status.getStatus() != 200) {
                    throw new Exception(resp.getMessage());
                }
            }

            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(response);
            if (proposalConsistencySets.size() != 1) {

                throw new Exception("Expected only one set of consistent proposal responses but got more");
            }

            commitResp = channel.sendTransaction(response, userContext);

            //Logger.getLogger(ChannelWrapper.class.getName()).log(Level.INFO, "Invoked chaincode " + chaincodeName + " - " + fcn + ". Status - " + commitResp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown(true);
            return commitResp;
        }
    }

    /**
     * @param txnId
     * @param channelName
     * @return TransactionInfo
     */
    public TransactionInfo queryByTransactionId(String txnId, String channelName) {
        Channel channel = null;
        TransactionInfo transactionInfo = null;
        try {
            channel = fc.getChannelClient(channelName);
            Collection<Peer> peers = channel.getPeers();
            for (Peer peer : peers) {
                transactionInfo = channel.queryTransactionByID(peer, txnId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown(true);
            return transactionInfo;
        }
    }
}
