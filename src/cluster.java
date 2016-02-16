import java.lang.Math.*;

class cluster {
	public int elementi;
	public double prior;
	public double xMean, xStdDev;
	public double yMean, yStdDev;
	public double zMean, zStdDev;
	public double dMean, dStdDev;

	//trasformazioni polari del centroide con origine nel baricentro della molecola
	public double ro, phi, theta;

public void calcolaPolari (float X, float Y, float Z) {
	//traslazione rispetto a O
	double x = xMean - X;
	double y = yMean - Y;
	double z = zMean - Z;

	//calcolo distanza da O sul piano XY
	ro = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));

	//calcolo gli angoli
	//phi = Math.acos(x/Math.sqrt(Math.pow(x,2)+Math.pow(y,2)));
	//theta = Math.acos(ro/Math.sqrt(Math.pow(ro,2)+Math.pow(z,2)));
	phi = Math.atan2(x,y);
	theta = Math.atan2(ro,z);

	//calcolo distanza da O nello spazio
	ro = Math.sqrt(Math.pow(ro,2)+Math.pow(z,2));
	}
}
