import com.fabric.client.ChannelWrapper;
public class Test {

    public static void main(String args[]) throws Exception {
        String org="org1";
        String userName = "Ankur";
        //
        //LoadConnectionProfile networkConfig = LoadConnectionProfile.getInstance();


        // CAClient caClient=new CAClient(org);
        //caClient.enrollAdmin(networkConfig.getCaInfo(org).getRegistrars().iterator().next().getName(),
               // networkConfig.getCaInfo(org).getRegistrars().iterator().next().getEnrollSecret());
        //caClient.getUserContext("Ankur");
        //"HtcczMLzSDjw");

        ChannelWrapper channelClient = ChannelWrapper.getChannelWrapperInstance(userName, org);
        channelClient.queryByTransactionId("14582cbfa64c7a8664bcbdd4f812c4119142a230e017aef10fc700462fe8e439", "mychannel");
    }
}
