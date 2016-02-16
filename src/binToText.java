//
//  untitled.java
//  
//
//  Created by ppking on Sat Mar 11 2006.
//  Copyright (c) 2006 __MyCompanyName__. All rights reserved.
//

import java.util.ArrayList;
import java.io.*;

public class binToText {
    //legge un file clusters in formato binario e lo trasforma in formato testo
    public static void main (String []args) {
        DataInputStream dis = null;
        String nomeFile = args[I];
        try{
            File f = new File(nomeFile);
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream	bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            int record;
            int numClusters=0;
            try {
                for (;;) {
                    record = dis.readInt();
                    System.out.println(record);
                }
            } catch (EOFException eof) {}
        } catch (IOException ioe) {System.out.println ("Errore in lettura");
            
        } finally {try {dis.close();} catch (IOException ioe) {}}
        
    }   
}

