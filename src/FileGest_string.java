import java.io.*;
import java.lang.Math.*;
import java.util.ArrayList;
import java.sql.Timestamp;

class FileGest {
    private static String pulisciStringa (String S) {
        while (S.startsWith(" ")) S= S.substring(1);
        while (S.endsWith(" ")) S=S.substring(0, S.length()-1);
        return S;
    }
    
    public static molecola readMyFile(String nomeFile) {
        
        DataInputStream dis = null;
        String record = null;
        molecola M = new molecola();
        M.nomeFile = nomeFile;
        
        try {
            
            File f = new File(nomeFile);
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            final double soglia =  (double)0.1;
            
            if (nomeFile.endsWith(".gri")) {
                //File output di readGrid g
                //legge l'intestazione
                record=dis.readLine();  // gridpoints
                
                M.X = Integer.valueOf(pulisciStringa(record.substring(1,10))).intValue();
                M.Y = Integer.valueOf(pulisciStringa(record.substring(11,21))).intValue();
                M.Z = Integer.valueOf(pulisciStringa(record.substring(22,32))).intValue();
                
                record=dis.readLine();  // coordinate minime
                record=dis.readLine();  // coordinate massime
                record=dis.readLine();  // passi
                
                M.point = new GridPoint [M.X][M.Y][M.Z];
                
                //legge il valore dei punti
                int conta=0;
                for (int iz = 0; iz<M.Z; iz ++) {
                    for (int ix = 0; ix< M.X; ix ++) {
                        for (int iy = 0; iy< M.Y; iy ++) {
                            record=dis.readLine();
                            M.point[ix][iy][iz] = new GridPoint();
                            
                            M.point[ix][iy][iz].x = Float.valueOf(pulisciStringa(record.substring(0,10))).floatValue();
                            M.point[ix][iy][iz].y = Float.valueOf(pulisciStringa(record.substring(11,20))).floatValue();
                            M.point[ix][iy][iz].z = Float.valueOf(pulisciStringa(record.substring(21,30))).floatValue();
                            if (Math.abs(Double.valueOf(pulisciStringa(record.substring(31,42))).doubleValue()) >= soglia) {
                                M.point[ix][iy][iz].d = Double.valueOf(pulisciStringa(record.substring(31,42))).doubleValue();
                                M.Xb += M.point[ix][iy][iz].x;
                                M.Yb += M.point[ix][iy][iz].y;
                                M.Zb += M.point[ix][iy][iz].z;
                                conta++;
                            }
                        }
                    }
                }
                M.Xb /= conta;
                M.Yb /= conta;
                M.Zb /= conta;
                
            }
            
            /*			if (nomeFile.endsWith(".arff")) {
                boolean clustered = false;
            String []parametri;
            float x,y,z;
            double d;
            int cluster;
            
            //file del programma weka (con o senza clusters)
            //legge l'intestazione
            while ((record = dis.readLine()).equals("@data")) if (record.startsWith("@attribute Cluster")) clustered = true;
            
            M.X = 40; M.Y = 40; M.Z=40;
            M.point = new GridPoint [M.X][M.Y][M.Z];
            M.passoX = (float)0.7074; M.passoY = (float)0.79735;M.passoZ = (float)0.90455;
            
            //legge il valore dei punti
            while ((record = dis.readLine()) != null) {
                
                if (clustered) {
                    parametri = record.split(",");
                    x =(float)Float.valueOf(parametri[1]); y = Float.valueOf(parametri[2]); z = Float.valueOf(parametri[3]);
                    d = Double.valueOf(parametri[4]); cluster = Integer.valueOf(parametri[5]);
                } else {
                    parametri = record.split(" ");
                    x = Float.valueOf(parametri[0]); y = Float.valueOf(parametri[1]); z = Float.valueOf(parametri[2]);
                    d = Double.valueOf(parametri[3]);
                }
                
                //M.point[
				}
			}
            */
            return M;
        } catch (IOException e) {
            // catch io errors from FileInputStream or readLine()
            System.out.println("Si e' verificato un errore durante la lettura del file "+nomeFile+" :\n" + e.getMessage());
            return null;
        } catch (Exception ue) {
            // Eccezione generica come ad esempio java.lang.NumberFormatException indica un errore di lettura del file perchè
            // ad esempio di formato non corretto
            System.out.println ("Errore di lettura nel file "+nomeFile+"\nIl formato probabilmente non e' corretto.");
            return null;
            
        } finally {
            if (dis != null) try {dis.close();} catch (IOException ioe) {}
        }
     }
    
    
    public static int readClusters(molecola m)  {
        
        DataInputStream dis = null;
        try{
            File f = new File(m.nomeFile+".clusters");
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream	bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            String record;
            int numClusters=0;
            ArrayList valori = new ArrayList();
            while ((record = dis.readLine())!=null) {
                valori.add(Integer.valueOf(record.trim()));
                numClusters = Math.max(numClusters, ((Integer)valori.get(valori.size()-1)).intValue());
            }
            m.cluster = new cluster[numClusters+1];
            
            for (int iz = 0; iz<m.Z; iz ++) { for (int ix = 0; ix< m.X; ix ++) { for (int iy = 0; iy< m.Y;iy ++) {
                if (m.point[ix][iy][iz].d != 0) {
                    m.point[ix][iy][iz].cluster = ((Integer)valori.remove(0)).intValue();
                } else {
                    m.point[ix][iy][iz].cluster = -1;
                }
                
            } } }
            m.hasClusters = true;
            return 0;
        } catch (Exception e) {
            return -1;
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ioe) {}
            }
        }
    }
    public static boolean writeClusters(molecola m) {
        DataOutputStream dos = null;
        try {
            File f = new File(m.nomeFile+".clusters");
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bof = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bof);
            for (int iz = 0; iz<m.Z; iz ++) for (int ix = 0; ix< m.X; ix ++) for (int iy = 0; iy< m.Y;iy ++)
                if (m.point[ix][iy][iz].cluster != -1) {
                    //dos.writeInt(m.point[ix][iy][iz].cluster);
                    Integer numero = new Integer (m.point[ix][iy][iz].cluster);
                    String str = numero.toString();
                    dos.writeChars(str.trim()+"\n");
                    //dos.writeChars(pulisciStringa(str));
                    //dos.writeChars("\n");
                }
                    return true;
        } catch (IOException ioe) {
            System.out.println ("Errore nel salvataggio dei clusters per il file "+m.nomeFile+".clusters");
            return false;
        } finally {
            try {dos.close();} catch (IOException ioe) {}
        }
    }
    
    public static void salvaRisultati (String []nomi, int [][]res) {
        DataOutputStream dos = null;
        Timestamp oggi = new Timestamp(System.currentTimeMillis());
        String str, nomeFile="";
        char car;
        
        try {
            str = oggi.toString();
            for (int i =0;i<str.length();i++) {
                if (str.charAt(i) != ':' &&
                    str.charAt(i) != '.' ) {
                    nomeFile += str.charAt(i);
                } else {
                    nomeFile += "_";
                }
            }
            
            nomeFile = "Tabella "+nomeFile+".csv";
            
            System.out.println("Salvo il file "+nomeFile);
            File f = new File(nomeFile);
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bof = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bof);
            
            str = new String(",");
            for (int i=0; i<nomi.length; i++) str+= nomi[i]+",";
            dos.writeChars(str+"\n");
            
            for (int i=0; i<res.length; i++) {
                str= new String(nomi[i]);
                
                for (int j=0; j<res[i].length; j++) str += ","+res[i][j];
                
                dos.writeChars(str);
                dos.writeChars("\n");
            }
        } catch (IOException ioe) {
            System.out.println ("Errore nel salvataggio della tabella riassuntiva");
            ioe.printStackTrace();
        } finally {
            try {dos.close();} catch (IOException ioe) {}
        }
    }
    
    public static void main (String []args) {
        molecola m = readMyFile(args[0]);
        readClusters(m);
        writeClusters(m);
    }
}


