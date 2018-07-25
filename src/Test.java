import com.fabric.client.ChannelWrapper;
import com.fabric.network.LoadConnectionProfile;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.TransactionInfo;

import java.util.concurrent.CompletableFuture;

public class Test {

    public static void main(String args[]) throws Exception {
        String org="org1";
        String userName = "Ankur";
        //
        LoadConnectionProfile networkConfig = LoadConnectionProfile.getInstance();


        //CAClientWrapper caClient=new CAClientWrapper(org);
       // caClient.enrollAdmin(networkConfig.getCaInfo(org).getRegistrars().iterator().next().getName(),
             //   networkConfig.getCaInfo(org).getRegistrars().iterator().next().getEnrollSecret());
        //caClient.getUserContext("Ankur","HtcczMLzSDjw","org1");

        ChannelWrapper channelClient = ChannelWrapper.getChannelWrapperInstance(userName, org);
        TransactionInfo ss = channelClient.queryByTransactionId("9fac3ff23bbc608914524980c72913493241ad7858ebf73a7b86d5afa13b7652", "mychannel");
       System.out.println(ss.getProcessedTransaction());
       // channelClient.queryChaincode("mychannel","pnp_go1","queryAssetData","988881530630158000");
        String[] args1={"3199957","Vishal","03-10-1990","Single","9980025414","IN","560066","BEML Layout","BLR","KR"};
        channelClient.invokeChainCode("mychannel", "pnp_go1", "registerBorrower", args1);
    }

}
