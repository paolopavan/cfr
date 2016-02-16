import java.util.ArrayList;
import java.io.*;

class trasformaFile {
    //legge un file clusters in formato testo e lo trasforma in formato binario
    public static void main (String []args) {
        for (int I=0;I<args.length;I++) {
            System.out.println(args[I]);
            
            
            DataInputStream dis = null;
            String nomeFile = args[I];
            ArrayList valori = new ArrayList();
            try{
                File f = new File(nomeFile);
                FileInputStream fis = new FileInputStream(f);
                BufferedInputStream	bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);
                String record;
                int numClusters=0;
                while ((record = dis.readLine())!=null) {
                    valori.add(Integer.valueOf(record.trim()));
                    numClusters = Math.max(numClusters, ((Integer)valori.get(valori.size()-1)).intValue());
                }
            } catch (IOException ioe) {System.out.println ("Errore in lettura");
            } finally {try {dis.close();} catch (IOException ioe) {}
                
                
                DataOutputStream dos = null;
                try {
                    File f = new File(nomeFile+".nuovo");
                    FileOutputStream fos = new FileOutputStream(f);
                    BufferedOutputStream bof = new BufferedOutputStream(fos);
                    dos = new DataOutputStream(bof);
                    while (!valori.isEmpty()) dos.writeInt(((Integer)valori.remove(0)).intValue());
                } catch (IOException ioe) {
                    System.out.println ("Errore in scrittura");
                } finally {try {dos.close();} catch (IOException ioe) {}}
            }
        }
    }
 
}
