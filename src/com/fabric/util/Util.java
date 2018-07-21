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
    public static void writeUserContext(UserContext userContext) throws Exception {
        String directoryPath = "cred/" + userContext.getAffiliation();
        String filePath = directoryPath + "/" + userContext.getName() + ".context";
        File directory = new File(directoryPath);
        if (!directory.exists())
            directory.mkdirs();

        FileOutputStream file = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(file);

        out.writeObject(userContext);

        out.close();
        file.close();
    }

    /**
     * Deserialize user
     *
     * @param affiliation
     * @param username
     * @return
     * @throws Exception
     */
    public static UserContext readUserContext(String affiliation, String username) throws Exception {
        String filePath = "cred/" + affiliation + "/" + username + ".context";
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream fileStream = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileStream);

            UserContext uContext = (UserContext) in.readObject();

            in.close();
            fileStream.close();
            return uContext;
        }

        return null;
    }

}


