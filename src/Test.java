import com.fabric.client.CAClient;
import com.fabric.network.LoadConnectionProfile;
public class Test {

    public static void main(String args[]) throws Exception {
        String org="org1";
        //
        LoadConnectionProfile networkConfig = LoadConnectionProfile.getInstance();


        CAClient caClient=new CAClient(org);
        //caClient.enrollAdmin(networkConfig.getCaInfo(org).getRegistrars().iterator().next().getName(),
               // networkConfig.getCaInfo(org).getRegistrars().iterator().next().getEnrollSecret());
        caClient.registerUser("Jamila1235167","admin1");
    }
}
