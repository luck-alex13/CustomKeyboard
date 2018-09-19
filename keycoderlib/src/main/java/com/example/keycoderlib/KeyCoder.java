package com.example.keycoderlib;

import java.util.UUID;

public class KeyCoder {

    public static String generateKey(){
        String uniqueID = UUID.randomUUID().toString();
        System.out.print(uniqueID);
        return  uniqueID;
    }
}
