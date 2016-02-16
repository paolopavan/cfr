import java.lang.Math.*;
import java.util.ArrayList;

class molecola {
    public String nomeFile; //nome del file dalla quale e' stata copiata
    public int X, Y, Z; // dimensioni della griglia
    public float passoX, passoY, passoZ;
    public float xMin, yMin, zMin; //coordinate minime della griglia
    public float xMax, yMax, zMax; //coordinate massime della griglia
    public float Xb, Yb, Zb;       //coordinate del baricentro della molecola rappresentata dalla griglia esp filtrata
    public GridPoint [][][]point;
    public cluster []cluster;
    public boolean hasClusters = false;
    
    public matrixPoint cerca(float px, float py, float pz) {
        matrixPoint nuovo = new matrixPoint();
        
        for (int x = 0; x<X; x++) {
            for (int y = 0; y<Y; y++) {
                for (int z = 0; z<Z; z++) {
                    if (point[x][y][z].x == px &&
                        point[x][y][z].y == py &&
                        point[x][y][z].z == pz
                        ) {
                        nuovo.x= x;nuovo.y= y;nuovo.z= z;
                        return nuovo;
                    }
                }
            }
        }
        return null;
    }
    
    public ArrayList cercad (double d, double soglia) {
        ArrayList res = new ArrayList();
        matrixPoint nuovo;
        
        for (int x = 0; x<X; x++) {
            for (int y = 0; y<Y; y++) {
                for (int z = 0; z<Z; z++) {
                    if (Math.abs(point[x][y][z].d - d) <= soglia) {
                        nuovo = new matrixPoint();
                        nuovo.x = x; nuovo.y= y; nuovo.z= z;
                        res.add (nuovo);
                    }
                }
            }
        }
        return res;
    }
    
    public void popolaCluster () {
        for (int i=0;i<cluster.length;i++) cluster[i] = new cluster();
        for (int x = 0; x<X; x++) {
            for (int y = 0; y<Y; y++) {
                for (int z = 0; z<Z; z++) {
                    if (point[x][y][z].cluster != -1) {
                        cluster[point[x][y][z].cluster].elementi ++;
                        cluster[point[x][y][z].cluster].xMean += point[x][y][z].x; cluster[point[x][y][z].cluster].xStdDev += Math.pow(point[x][y][z].x,2);
                        cluster[point[x][y][z].cluster].yMean += point[x][y][z].y; cluster[point[x][y][z].cluster].yStdDev += Math.pow(point[x][y][z].y,2);
                        cluster[point[x][y][z].cluster].zMean += point[x][y][z].z; cluster[point[x][y][z].cluster].zStdDev += Math.pow(point[x][y][z].z,2);
                        cluster[point[x][y][z].cluster].dMean += point[x][y][z].d; cluster[point[x][y][z].cluster].dStdDev += Math.pow(point[x][y][z].d,2);
                    }
                }
            }
        }
        for (int i=0;i<cluster.length;i++) {
            cluster[i].xMean = cluster[i].xMean/cluster[i].elementi;
            cluster[i].xStdDev = Math.sqrt(cluster[i].xStdDev / cluster[i].elementi - Math.pow(cluster[i].xMean,2));
            
            cluster[i].yMean = cluster[i].yMean/cluster[i].elementi;
            cluster[i].yStdDev = Math.sqrt(cluster[i].yStdDev / cluster[i].elementi - Math.pow(cluster[i].yMean,2));
            
            cluster[i].zMean = cluster[i].zMean/cluster[i].elementi;
            cluster[i].zStdDev = Math.sqrt(cluster[i].zStdDev / cluster[i].elementi - Math.pow(cluster[i].zMean,2));
            
            cluster[i].dMean = cluster[i].dMean/cluster[i].elementi;
            cluster[i].dStdDev = Math.sqrt(cluster[i].dStdDev / cluster[i].elementi - Math.pow(cluster[i].dMean,2));
            
            //togli il commento per visualizzare i dati riassuntivi dei clusters
            //NB sono leggermente diversi dai riassuntivi di weka. Forse perche' li calcolo con precisione doppia?
            //System.out.println(i+") "+cluster[i].xMean+" "+cluster[i].yMean+" "+cluster[i].zMean);
            //System.out.println("sd) "+cluster[i].xStdDev+" "+cluster[i].yStdDev+" "+cluster[i].zStdDev);
        }
        
        
    }
    
    public int[] applicaVettore (double ro, double phi, double theta) {
        ArrayList soluzioni = new ArrayList();
        double y = ro * Math.sin(theta)*Math.cos(phi);
        double x = ro * Math.sin(theta)*Math.sin(phi);
        double z = ro * Math.cos(theta);
        
        x+=Xb;y+=Yb;z+=Zb;
        
        for (int i =0; i< cluster.length; i++) {
            if (Math.abs(x-cluster[i].xMean) <= 2*cluster[i].xStdDev &&
                Math.abs(y-cluster[i].yMean) <= 2*cluster[i].yStdDev &&
                Math.abs(z-cluster[i].zMean) <= 2*cluster[i].zStdDev) soluzioni.add(new Integer(i));
        }
        
        if (soluzioni.size() == 0) {
            int res[] = {-1};
            return res;
        } else {
            int res[] = new int[soluzioni.size()];
            for (int i=0; i<soluzioni.size();i++) res[i]=((Integer)soluzioni.get(i)).intValue();
            return res;
        }
    }
}
