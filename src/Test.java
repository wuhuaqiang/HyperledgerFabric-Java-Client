import com.fabric.client.CAClient;
import com.fabric.network.LoadNetwork;
public class Test {

    public static void main(String args[]) throws Exception {
        String org="org1";
        LoadNetwork networkConfig=new LoadNetwork();


        CAClient caClient=new CAClient(org);
        //caClient.enrollAdmin(networkConfig.getCaInfo(org).getRegistrars().iterator().next().getName(),
               // networkConfig.getCaInfo(org).getRegistrars().iterator().next().getEnrollSecret());
        caClient.registerUser("Jamila1235167","admin1");
    }
}
