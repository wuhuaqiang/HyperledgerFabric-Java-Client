import com.fabric.client.ChannelWrapper;
import com.fabric.client.CAClient;
import com.fabric.network.LoadConnectionProfile;
public class Test {

    public static void main(String args[]) throws Exception {
        String org="org1";
        String userName = "Ankur";
        //
        LoadConnectionProfile networkConfig = LoadConnectionProfile.getInstance();


        //CAClient caClient=new CAClient(org);
       // caClient.enrollAdmin(networkConfig.getCaInfo(org).getRegistrars().iterator().next().getName(),
             //   networkConfig.getCaInfo(org).getRegistrars().iterator().next().getEnrollSecret());
        //caClient.getUserContext("Ankur","HtcczMLzSDjw","org1");

        ChannelWrapper channelClient = ChannelWrapper.getChannelWrapperInstance(userName, org);
        //channelClient.queryByTransactionId("14582cbfa64c7a8664bcbdd4f812c4119142a230e017aef10fc700462fe8e439", "mychannel");
       //channelClient.queryChaincode("mychannel","pnp_go1","queryAssetData","988881530630158000");
        String[] args1={"31103337","Vishal","03-10-1990","Single","9980025414","IN","560066","BEML Layout","BLR","KR"};
        channelClient.invokeChainCode("mychannel","pnp_go1","registerBorrower",args1);
    }

}
