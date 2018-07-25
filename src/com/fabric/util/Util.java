package com.fabric.util;

import com.fabric.participant.UserContext;

import java.io.*;

/**
 * @author Vishal
 */

public class Util {

    /**
     * Serialize user
     *
     * @param userContext
     * @throws Exception
     */
    public static void writeUserContext(UserContext userContext) {
        try {
            String directoryPath = "cred/" + userContext.getAffiliation();
            String filePath = directoryPath + "/" + userContext.getName() + ".context";
            File directory = new File(directoryPath);
            if (!directory.exists())
                directory.mkdirs();

            FileOutputStream file = null;
            file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(userContext);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            file.close();
        }
    }

    /**
     * Deserialize user
     *
     * @param affiliation
     * @param username
     * @return
     * @throws Exception
     */
    public static UserContext readUserContext(String affiliation, String username) {
        UserContext uContext = null;
        try {
            String filePath = "cred/" + affiliation + "/" + username + ".context";
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fileStream = new FileInputStream(filePath);
                ObjectInputStream in = new ObjectInputStream(fileStream);
                uContext = (UserContext) in.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            fileStream.close();
            return uContext;
        }

    }

}


