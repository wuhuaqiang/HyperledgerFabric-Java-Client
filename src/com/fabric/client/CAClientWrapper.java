package com.fabric.client;

import com.fabric.network.LoadConnectionProfile;
import com.fabric.participant.UserContext;
import com.fabric.util.Util;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper class for HFCAClient.class
 *
 * @author Vishal
 */

public class CAClientWrapper {

    private HFCAClient hfcaClient;
    private static String org;
    private LoadConnectionProfile config;
    // private static final Log logger = LogFactory.getLog();


    /**
     * Constructor - loads the CA configuration from network configuration file and intitialize the caClient for organization org
     *
     * @param org - organization name
     * @throws Exception
     */
    public CAClientWrapper(String org) throws Exception {
        this.config = LoadConnectionProfile.getInstance();
        CAClientWrapper.org = org;
        this.hfcaClient = HFCAClient.createNewInstance(LoadConnectionProfile.getCaInfo(org));
    }

    /**
     * Return UserContext for user from store /cred directory.
     *
     * @param userName
     * @return UserContext, null if not found
     * @throws Exception
     */
    public static UserContext getUserContext(String userName, String org) throws Exception {
        UserContext userContext;
        userContext = Util.readUserContext(org, userName);
        if (userContext != null) {
            return userContext;
        }
        Logger.getLogger(CAClientWrapper.class.getName()).log(Level.SEVERE, "Userconext not found in store for " + userName + ". Enroll the user.");
        return null;

    }

    /**
     * Enroll the admin. This admin will be used as a registrar to register other users.
     *
     * @param name   - admin name
     * @param secret - admin secret
     * @return adminContext
     * @throws Exception
     */

    public void enrollAdmin(String name, String secret) throws Exception {
        UserContext adminContext;
        adminContext = Util.readUserContext(org, name);
        if (adminContext != null) {
            Logger.getLogger(CAClientWrapper.class.getName()).log(Level.WARNING, "Admin is already enrolled. Therefore skipping...admin enrollment");
        }

        Enrollment enrollment = hfcaClient.enroll(name, secret);
        Logger.getLogger(CAClientWrapper.class.getName()).log(Level.INFO, "Admin enrolled.");

        adminContext = new UserContext();
        adminContext.setName(name);
        adminContext.setEnrollment(enrollment);
        adminContext.setAffiliation(LoadConnectionProfile.getOrgInfo(org).getName());
        adminContext.setMspId(LoadConnectionProfile.getOrgInfo(org).getMspId());

        Util.writeUserContext(adminContext);

    }

    /**
     * Register and enroll the user with organization MSP provider. User context saved in  /cred directory.
     * This is an admin function; admin should be enrolled before enrolling a user.
     *
     * @param userName
     * @param registrarAdmin - network admin
     * @return UserContext
     * @throws Exception
     */
    public void registerUser(String userName, String registrarAdmin) throws Exception {
        UserContext userContext;
        userContext = Util.readUserContext(org, userName);
        if (userContext != null) {
            Logger.getLogger(CAClientWrapper.class.getName()).log(Level.WARNING, "UserName - " + userName + "  is already registered. Therefore skipping..... registeration");

        }
        RegistrationRequest regRequest = new RegistrationRequest(userName, org);
        UserContext registrarContext = Util.readUserContext(org, registrarAdmin);
        if (registrarContext == null) {
            Logger.getLogger(CAClientWrapper.class.getName()).log(Level.SEVERE, "Registrar " + registrarAdmin + " is not enrolled. Enroll Registrar.");

        }
        String enrollSecret = hfcaClient.register(regRequest, registrarContext);

        Enrollment enrollment = hfcaClient.enroll(userName, enrollSecret);

        userContext = new UserContext();
        userContext.setMspId(LoadConnectionProfile.getOrgInfo(org).getMspId());
        userContext.setAffiliation(org);
        userContext.setEnrollment(enrollment);
        userContext.setName(userName);

        Util.writeUserContext(userContext);
        Logger.getLogger(CAClientWrapper.class.getName()).log(Level.INFO, "UserName - " + userName + "  is successfully registered and enrolled by registrar -  " + registrarAdmin);

    }

    /**
     * Usercontext is  generated from user secret key and store is also refreshed,
     * * User must be registered with MSP provider.
     *
     * @param userName
     * @param enrollSecret
     * @return UserContext
     * @throws Exception
     */
    public UserContext getUserContext(String userName, String enrollSecret, String org) throws Exception {

        Enrollment enrollment = hfcaClient.enroll(userName, enrollSecret);

        UserContext userContext = new UserContext();
        userContext.setMspId(LoadConnectionProfile.getOrgInfo(org).getMspId());
        userContext.setAffiliation(CAClientWrapper.org);
        userContext.setEnrollment(enrollment);
        userContext.setName(userName);

        Util.writeUserContext(userContext);
        Logger.getLogger(CAClientWrapper.class.getName()).log(Level.INFO, "UserName - " + userName + "  is successfully enrolled ");
        return userContext;
    }
}
