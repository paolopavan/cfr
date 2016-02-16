import weka.core.*;
import weka.clusterers.*;
//import weka.filters.*;

class prova {

	public static void main (String []args) {
		//Filter m_Filter = new DiscretizeFilter();
		EM analizzatore = new EM();

		FastVector nomiAttributi = new FastVector();
		nomiAttributi.addElement(new Attribute ("X"));
		nomiAttributi.addElement(new Attribute ("Y"));
		nomiAttributi.addElement(new Attribute ("Z"));
		nomiAttributi.addElement(new Attribute ("d"));

		String molecola = "nomeMolecola";

		Instances esp = new Instances (molecola, nomiAttributi, 4);

		Instance inst1= new Instance (4);
		Instance inst2= new Instance (4);

		inst1.setValue(0, 5.3);
		inst1.setValue(1, 6.3);
		inst1.setValue(2, 5.8);
		inst1.setValue(3, 2.6);

		inst2.setValue(0, 23.3);
		inst2.setValue(1, 64.3);
		inst2.setValue(2, 56.8);
		inst2.setValue(3, 45.6);

		System.out.println(analizzatore.globalInfo());

		for (int i=0; i <500; i++) {esp.add(inst1);esp.add(inst2);}

		try {
			//m_Filter.inputFormat(esp);
			//Instances f_esp = Filter.useFilter(esp, m_Filter);

			// Questi che seguono sono i valori di default e quindi non servirebbe settarli
			analizzatore.setMaxIterations(100);
			analizzatore.setNumClusters(-1);
			analizzatore.setMinStdDev (1.0E-6);
			analizzatore.setSeed(100);
			analizzatore.setDebug(false);


			double []v = {1.0E-6,1.0E-6,1.0E-6,1.0E-6};
			//analizzatore.setMinStdDevPerAtt(v);

			analizzatore.buildClusterer(esp);
			System.out.println(analizzatore.numberOfClusters());
			System.out.println(analizzatore.toString());
			double valore[][][] = analizzatore.getClusterModelsNumericAtts();
			System.out.println(valore.length);
			for (int a=0; a< valore.length;a++) {System.out.println("a->"+a);
			for (int b=0; b< valore[a].length; b++) {System.out.println("b->"+b);
			for (int c=0; c< valore[a][b].length; c++) System.out.println("c-> "+c+" val "+valore[a][b][c]);


		}}
		System.out.println("\n\nProva clusterizzazione istanza: "+analizzatore.clusterInstance(inst1));

		} catch (Exception e) {
			System.out.println("Errore");
			e.printStackTrace();
		}


	}

}