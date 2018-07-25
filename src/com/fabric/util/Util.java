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
        ObjectOutputStream out = null;
        FileOutputStream file = null;
        try {
            String directoryPath = "cred/" + userContext.getAffiliation();
            String filePath = directoryPath + "/" + userContext.getName() + ".context";
            File directory = new File(directoryPath);
            if (!directory.exists())
                directory.mkdirs();

            file = null;
            file = new FileOutputStream(filePath);
            out = new ObjectOutputStream(file);

            out.writeObject(userContext);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        FileInputStream fileStream = null;
        ObjectInputStream in = null;
        try {
            String filePath = "cred/" + affiliation + "/" + username + ".context";
            File file = new File(filePath);
            if (file.exists()) {
                fileStream = new FileInputStream(filePath);
                in = new ObjectInputStream(fileStream);
                uContext = (UserContext) in.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return uContext;
        }

    }

}


