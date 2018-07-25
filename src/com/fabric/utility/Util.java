package com.fabric.utility;

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
     */
    public static void writeUserContext(UserContext userContext) {
        ObjectOutputStream out = null;
        FileOutputStream file = null;
        try {
            String directoryPath = "msp/" + userContext.getAffiliation();
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
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (file != null)
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
            String filePath = "msp/" + affiliation + "/" + username + ".context";
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
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fileStream != null)
                    fileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return uContext;
        }

    }

}


