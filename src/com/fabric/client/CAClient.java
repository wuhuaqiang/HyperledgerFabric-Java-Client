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

public class CAClient {

    private HFCAClient hfcaClient;
    private String org;
    private LoadConnectionProfile config;

    /**
     * Constructor - loads the CA configuration from network configuration file and intitialize the caClient for organization org
     *
     * @param org - organization name
     * @throws Exception
     */
    public CAClient(String org) throws Exception {
        this.config = LoadConnectionProfile.getInstance();
        this.org = org;
        this.hfcaClient = HFCAClient.createNewInstance(config.getCaInfo(org));
    }

    /**
     * Enroll the admin. This admin will be used as a registrar to register other users.
     *
     * @param name   - admin name
     * @param secret - admin secret
     * @return adminContext
     * @throws Exception
     */

    public UserContext enrollAdmin(String name, String secret) throws Exception {
        UserContext adminContext;
        adminContext = Util.readUserContext(this.org, name);
        if (adminContext != null) {
            Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "Admin is already enrolled. Therefore skipping...admin enrollment");
            return adminContext;
        }

        Enrollment enrollment = hfcaClient.enroll(name, secret);
        Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "Admin enrolled.");

        adminContext = new UserContext();
        adminContext.setName(name);
        adminContext.setEnrollment(enrollment);
        adminContext.setAffiliation(config.getOrgInfo(org).getName());
        adminContext.setMspId(config.getOrgInfo(org).getMspId());

        Util.writeUserContext(adminContext);
        return adminContext;
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
    public UserContext registerUser(String userName, String registrarAdmin) throws Exception {
        UserContext userContext;
        userContext = Util.readUserContext(this.org, userName);
        if (userContext != null) {
            Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "UserName - " + userName + "  is already registered. Therefore skipping..... registeration");
            return userContext;
        }
        RegistrationRequest regRequest = new RegistrationRequest(userName, this.org);
        UserContext registrarContext = Util.readUserContext(this.org, registrarAdmin);
        if (registrarContext == null) {
            Logger.getLogger(CAClient.class.getName()).log(Level.SEVERE, "Registrar " + registrarAdmin + " is not enrolled. Enroll Registrar.");
            return null;
        }
        String enrollSecret = hfcaClient.register(regRequest, registrarContext);

        Enrollment enrollment = hfcaClient.enroll(userName, enrollSecret);

        userContext = new UserContext();
        userContext.setMspId(config.getOrgInfo(this.org).getMspId());
        userContext.setAffiliation(this.org);
        userContext.setEnrollment(enrollment);
        userContext.setName(userName);

        Util.writeUserContext(userContext);
        Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "UserName - " + userName + "  is successfully registered and enrolled by registrar -  " + registrarAdmin);
        return userContext;
    }

    /**
     * Return UserContext for user; if not find in /cred directory, usercontext is generated from user enrollSecret.
     * User must be registered with MSP provider.
     *
     * @param userName
     * @param enrollSecret optional
     * @return UserContext
     * @throws Exception
     */
    public UserContext getUserContext(String userName, String enrollSecret) throws Exception {
        UserContext userContext;
        userContext = Util.readUserContext(this.org, userName);
        if (userContext != null) {
            return userContext;
        } else {
            //Logger.getLogger(CAClient.class.getName()).log(Level.SEVERE, "UserName - " + userName + "  is not enrolled. Register user first.");
            Enrollment enrollment = hfcaClient.enroll(userName, enrollSecret);

            userContext = new UserContext();
            userContext.setMspId(config.getOrgInfo(this.org).getMspId());
            userContext.setAffiliation(this.org);
            userContext.setEnrollment(enrollment);
            userContext.setName(userName);

            Util.writeUserContext(userContext);
            Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "UserName - " + userName + "  is successfully enrolled ");
            return userContext;
        }

    }
}
