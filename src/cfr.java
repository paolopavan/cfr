import java.util.List;
import java.util.ArrayList;
import weka.core.*;
import weka.clusterers.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.*;

class cfr {
    public static float confronta (molecola m, molecola M, angoloDiedro O, ArrayList clusters, float sharpness, boolean D) {
        // m = molecola di riferimento; M = molecola di studio
        double DeltaTheta=O.theta, DeltaPhi=O.phi;
        int partenza, confronto=-2,numMatch=0;
        int []confronti;
        float punteggio=0;
        
        if (D) System.out.println("\nConfronto "+m.nomeFile+" vs "+M.nomeFile+"\nAngolo diedro:\ntheta:"+O.theta+"\nphi:"+O.phi);
        
        for (int cl= 0; cl<clusters.size();cl++) {
            partenza = ((Integer)clusters.get(cl)).intValue();
            
            confronti = M.applicaVettore(m.cluster[partenza].ro, m.cluster[partenza].phi+DeltaPhi, m.cluster[partenza].theta+DeltaTheta);
            if (confronti.length != 1) {
                double differenza;
                
                confronto = confronti[0];
                double minimo = Math.abs(M.cluster[confronti[0]].dMean - m.cluster[partenza].dMean);
                
                for (int it=1;it<confronti.length;it++) {
                    differenza = Math.abs(M.cluster[confronti[it]].dMean - m.cluster[partenza].dMean);
                    if (differenza < minimo) {
                        confronto = confronti[it];
                        minimo = differenza;
                    }
                }
            } else
                confronto = confronti[0];
            
            if(D)System.out.println("Cluster di partenza -> " + partenza+"  "+ confronto+ " <- Cluster trovato");
            if(D)System.out.println("Riassuntivo cluster partenza:\nxMean = "+m.cluster[partenza].xMean+"\txStdDev = "+m.cluster[partenza].xStdDev+
                                    "\nyMean = "+m.cluster[partenza].yMean+"\tyStdDev = "+m.cluster[partenza].yStdDev+
                                    "\nzMean = "+m.cluster[partenza].zMean+"\tzStdDev = "+m.cluster[partenza].zStdDev+
                                    "\ndMean = "+m.cluster[partenza].dMean+"\tdStdDev = "+m.cluster[partenza].dStdDev);
            if(D && confronto !=-1)System.out.println("Riassuntivo cluster confronto:\nxMean = "+M.cluster[confronto].xMean+"\txStdDev = "+M.cluster[confronto].xStdDev+
                                    "\nyMean = "+M.cluster[confronto].yMean+"\tyStdDev = "+M.cluster[confronto].yStdDev+
                                    "\nzMean = "+M.cluster[confronto].zMean+"\tzStdDev = "+M.cluster[confronto].zStdDev+
                                    "\ndMean = "+M.cluster[confronto].dMean+"\tdStdDev = "+M.cluster[confronto].dStdDev);
            
            //calcola il punteggio
            if (confronto!=-1 && Math.abs(M.cluster[confronto].dMean - m.cluster[partenza].dMean) <= sharpness*M.cluster[confronto].dStdDev) {
                punteggio++;
                if(D) System.out.println("Corrisponde");
            }
            else if(D) System.out.println("Non corrisponde");
            
            if(D)System.out.println();
        }
        if(D)System.out.println("il punteggio e' "+punteggio+"/"+clusters.size());
        if(D)System.out.println("----------------------------\n\n");
        punteggio /= clusters.size(); punteggio *= 100;
        
        
        return punteggio;
        
    }
    
    private static ArrayList seleziona (molecola m, int metodo) {
        ArrayList clusters = new ArrayList();
        if (metodo==0) {
            for (int i=0; i<m.cluster.length; i++) {
                if (m.cluster[i].dMean - m.cluster[i].dStdDev < 0) clusters.add(new Integer(i));
            }
        }
        if (metodo==1) {
            for (int i=0; i<m.cluster.length; i++) clusters.add(new Integer(i));
        }
        //altro metodo basato sulla densita' (elementi/volume occupato)
        
        return clusters;
    }
    
    private static ArrayList orienta (molecola m, molecola M, ArrayList clusters) {
        //Calcolo l'angolo diedro di traslazione medio
        
        //preso un cluster c a piacere dalla molecola di riferimento m, elenca in possibilita tutti i
        //cluster C della molecola di studio M a distanza ro(c) tali che d(C)=d(c)+-2stdDev
        int i=0, c;
        double diametro;
        ArrayList possibilita = new ArrayList();
        
        do {
            c = ((Integer)clusters.get(i)).intValue();
            
            for (int C=0; C<M.cluster.length; C++) {
                diametro = Math.max(M.cluster[C].xStdDev, M.cluster[C].yStdDev); diametro = Math.max(diametro, M.cluster[C].zStdDev);
                if (Math.abs(m.cluster[c].ro - M.cluster[C].ro) <= diametro &&
                    Math.abs(m.cluster[c].dMean - M.cluster[C].dMean) <= 2*M.cluster[C].dStdDev) {
                    
                    angoloDiedro orientamento = new angoloDiedro();
                    orientamento.theta = (m.cluster[c].theta - M.cluster[C].theta)/2; //P
                    orientamento.phi = (m.cluster[c].phi - M.cluster[C].phi)/2;       //P
                    possibilita.add(orientamento);
                }
            }
            i++;
            
        } while (possibilita.isEmpty() && i< clusters.size());
        
        if (possibilita.isEmpty()) {
            System.out.println("Le mappe esp non sono sovrapponibili");
        }
        return possibilita;
    }
    
    
    
    private static boolean analizza(molecola m, boolean D) {
        EM analizzatore = new EM();
        PKIDiscretize filtro = new PKIDiscretize();
        
        FastVector nomiAttributi = new FastVector();
        nomiAttributi.addElement(new Attribute ("X"));
        nomiAttributi.addElement(new Attribute ("Y"));
        nomiAttributi.addElement(new Attribute ("Z"));
        nomiAttributi.addElement(new Attribute ("d"));
        
        Instances esp = new Instances (m.nomeFile, nomiAttributi, 4);        
        
        for (int iz = 0, n=0; iz<m.Z; iz ++) for (int ix = 0; ix< m.X; ix ++) for (int iy = 0; iy< m.Y;iy ++) {
            if (m.point[ix][iy][iz].d != 0) {
                Instance punto= new Instance (4);
                
                punto.setValue(0, m.point[ix][iy][iz].x);
                punto.setValue(1, m.point[ix][iy][iz].y);
                punto.setValue(2, m.point[ix][iy][iz].z);
                punto.setValue(3, m.point[ix][iy][iz].d);
                
                esp.add(punto);
                n++;
            } else {
                m.point[ix][iy][iz].cluster = -1;
            }
        }
            
        if(D)System.out.println (esp.numInstances()+" istanze caricate nel dataset");
        
        try {
            System.out.print ("Analisi in corso... ");
            
            //Filtraggio
            String[] opt = new String[2];
            opt[0]="-R"; opt[1]= "last";
            filtro.setOptions(opt); // discretizzazione del valore d
            filtro.setInputFormat(esp);
            Instances filtered_esp = Filter.useFilter(esp, filtro);
            
            //if (D) for (int I=0;I<filtered_esp.numInstances();I++) System.out.println(filtered_esp.instance(I).toString());
            
            analizzatore.buildClusterer(filtered_esp);
            
            System.out.print ("Ok. ");
            if (D)System.out.println(analizzatore.toString());
            
            //assegna i clusters ad ogni punto
            for (int iz = 0, n=0; iz<m.Z; iz ++) for (int ix = 0; ix< m.X; ix ++) for (int iy = 0; iy< m.Y;iy ++)
                if (m.point[ix][iy][iz].cluster != -1) {
                    m.point[ix][iy][iz].cluster = analizzatore.clusterInstance(filtered_esp.instance(n));//(punto[n]); //esp.instance(n) andava bene lo stesso
                    n++;
                }
                    
                    
                    m.cluster = new cluster[analizzatore.numberOfClusters()];
            m.hasClusters =true;
            return true;
        } catch (Exception e) {
            System.out.println("Errore durante l'analisi.");
            if (D) e.printStackTrace();
            return false;
        }
    }
    
    public static void main (String []args) {
        boolean D=false; //Debug verbosity
        boolean orienta = false;
        float sharpness = 1;  //coefficente per cui viene moltiplicata la dStdDev nel confronto dei cluster
        int metodoSelezione = 0;
        
        if (args.length == 0) {
            System.out.println("Synopsis:\ncfr [-o] [-sAll] [-large] [] nomefile1.gri nomefile2.gri [*.gri]\n\n");
            System.exit(0);
        }
        
        int o=0;
        while (args[o].startsWith("-")) {
            if (args[o].equals("-o")) orienta = !orienta;
            if (args[o].equals("-D")) D=!D;
            if (args[o].equals("-sAll")) metodoSelezione = 1; 
            if (args[o].equals("-sharpness")) {
                o++;
                sharpness = (new Float(args[o])).floatValue();
                System.out.println("Custom sharpness: "+sharpness);
            }
            o++;
        }
        
        System.out.println("Saranno processati nell'ordine i seguenti files:");
        for (int i = o;i<args.length;i++) System.out.println(args[i]);
        
        System.out.println("\nLeggo i dati delle mappe esp\n");
        
        molecola []elencoMolecole= new molecola[args.length-o];
        
        int N=0;
        for (; o< args.length; o++, N++) {
            elencoMolecole[N] = new molecola();
            elencoMolecole[N] = FileGest.readMyFile (args[o]);
            if (elencoMolecole[N] == null) {
                System.out.println ("Il file "+args[o]+" sara' ignorato");
                N--;
            }
        }
        
        if (N==0) {
            System.out.println("\nNessun file valido. Esco.");
            System.exit(0);
        }
        
        for (int n=0; n<N; n++) {
            if (FileGest.readClusters(elencoMolecole[n])==-1) {
                System.out.println("E' necessario analizzare la griglia esp del file "+elencoMolecole[n].nomeFile);
                if (analizza(elencoMolecole[n], D) && FileGest.writeClusters(elencoMolecole[n]))
                    System.out.println("Dati analisi salvati.");
                
            }
        }
        
        System.out.println("\nAvvio il calcolo");
        
        // Calcola i dati riassuntivi dei clusters
        for (int j=0; j<elencoMolecole.length; j++) {
            elencoMolecole[j].popolaCluster();
            for (int i=0; i<elencoMolecole[j].cluster.length; i++)
                elencoMolecole[j].cluster[i].calcolaPolari(elencoMolecole[j].Xb,elencoMolecole[j].Yb,elencoMolecole[j].Zb);
        }
        
        int [][]matriceRisultati = new int[elencoMolecole.length][elencoMolecole.length];
        String [][]matriceAngoli = new String[elencoMolecole.length][elencoMolecole.length];
        
        
        angoloDiedro O = new angoloDiedro();
        
        for (int m=0; m<elencoMolecole.length; m++) {
            System.out.println();
            //seleziona i clusters significativi
            ArrayList clusters = seleziona(elencoMolecole[m], metodoSelezione);
            for (int M=0; M<elencoMolecole.length; M++) {
                if (!clusters.isEmpty()) {
                    if (orienta) {
                        int maxPt=0;
                        angoloDiedro maxAn = new angoloDiedro();
                        ArrayList listaAngoli = orienta (elencoMolecole[m],elencoMolecole[M], clusters);
                        //scelta dell'angolo che da' il maggior punteggio
                        while (!listaAngoli.isEmpty()) {
                            O = (angoloDiedro)listaAngoli.remove(0);
                            int res = (int)confronta(elencoMolecole[m],elencoMolecole[M],O,clusters,sharpness, D);
                            if (res>maxPt) {
                                maxPt = res;
                                maxAn = O;
                            }
                        }
                        matriceRisultati[m][M] = maxPt;
                        matriceAngoli[m][M] = "The:"+O.theta+"\nPhi:"+O.phi+"\n";
                    } else {
                        matriceRisultati[m][M] = (int)confronta(elencoMolecole[m],elencoMolecole[M],O,clusters,sharpness,D);
                    }
                    if(!D)System.out.print(matriceRisultati[m][M]+"\t");
                }
                
                else
                    if(!D)System.out.println("n.a.");
            }
        }
        System.out.println();
        
        String []nomi = new String[elencoMolecole.length];
        for (int j=0; j<elencoMolecole.length; j++) nomi[j] = elencoMolecole[j].nomeFile;
        
        FileGest.salvaRisultati (nomi,matriceRisultati);
        
    }
    
}


