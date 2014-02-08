import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
class ActCalc {
  public static int nIn = 13;
  public static int nAct = 7;
  public static int nAgents;
  public static boolean noMarkers = true;
  public static boolean highPopDensity = true;
  public static boolean getAverKinValue = false;
  public static boolean calcAverOnly = true;
  public static boolean energyDistr = false;


  public static void main(String[] args) {
    try {
      //int nKern=500, kernNeeded = 50, time, maxTime = 41400000, hWndSize = 10;
       int nKern=100, kernNeeded = 50, time, maxTime = 50000000, hWndSize = 10;
       boolean universal = true; int enrgLvls = 3; int kinLvls = 2;
       boolean agression = false;
      // Reading parameters of simulation
      System.out.println("Reading parameters of simulation ...");
      System.out.println();

      //reading parameters of calculations
      BufferedReader par = new BufferedReader(new FileReader("paramCalc.txt"));
      noMarkers = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      universal = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      agression = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      highPopDensity = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      getAverKinValue = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      calcAverOnly = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      maxTime = (new Integer(new StringTokenizer(par.readLine()).nextToken())).intValue();
      energyDistr = (new Boolean(new StringTokenizer(par.readLine()).nextToken())).booleanValue();
      par.close();

      BufferedReader r = new BufferedReader(new FileReader("param.txt"));

      WorldParams.version =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.worldXsize =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.worldYsize =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.initPopulation =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.age =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.grassCycle =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.mutation =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.grassIntencity =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.maxEnergy =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eDivide =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eMove =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eEat =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eTurn =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eRest =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eFight =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eGrass =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.period =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.mutModul =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.markerLength =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.markerMutRate =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.markerMutInt =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.maxMarkerValue =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.savePeriod =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.saveAverGenPeriod =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.maxWeight =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.mapScale =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();

      r.close();

      int markerL = WorldParams.markerLength;

      // prepearing files to save output

      FileWriter evolActivity = new FileWriter("evol_act.txt");
      FileWriter phaseTrack = new FileWriter("phase_track.txt");

      //initializing phenotypes
      PhenoCalc pheno;
      if (universal) {
        pheno = new PhenoCalc(enrgLvls, kinLvls, agression);
      }
      else {
        if (noMarkers) pheno = new PhenoCalc(3);
          else pheno = new PhenoCalc();
      }

      System.out.println("Mesuaring Evolutionary Activity...");

      // reading file
      System.out.println("reading file...");
      Vector wm = new Vector();
      BufferedReader gm = new BufferedReader(new FileReader("aver_gen.txt"));
      StringTokenizer ssm= new StringTokenizer(gm.readLine());
      String strm =  new String(gm.readLine());
      while (strm!=null){
	wm.addElement(strm);
	strm = gm.readLine();
      }
      gm.close();
      int wmSize = wm.size()-2;
      wm.removeAllElements();

      float[][] averW = new float[wmSize][nIn*nAct];

      BufferedReader g1m = new BufferedReader(new FileReader("aver_gen.txt"));
      String str1m =  new String();
      StringTokenizer ss1m = new StringTokenizer(str1m);
      //String str1m =  new String(g1m.readLine());
      for (int j = 0; j < wmSize; j++){
	str1m =  g1m.readLine();
	ss1m = new StringTokenizer(str1m);
	for (int i = 0; i < (nIn*nAct); i++) { // Reading of initial weight matrix
	  averW[j][i] = (float) (new Double(ss1m.nextToken())).doubleValue();
	}
      }
      g1m.close();

      System.out.println("calculating derivations...");

      // opening files
      FileWriter euclDerF = new FileWriter("euclDer.txt");
      euclDerF.write("time"+"\t"+"euclDer"+"\r\n");
      FileWriter phaseCompr = new FileWriter("phaseCompr.txt");
      phaseCompr.write("time"+"\t"+"wght1"+"\t"+"wght2"+"\r\n");
      FileWriter phaseBasSpec = new FileWriter("basinsSpec.txt");
      phaseBasSpec.write("basin N"+"\t"+"wght1"+"\t"+"der_w1"+"\r\n");

      //defining variables
      float sumDer = 0;
       // array fo two max derivated weights [time][numberOfWeight][valueOfWeight]
      //int[][] maxDerWght = new int[wmSize][2];
      float[] derivation = new float[wmSize-1];
      double[] euclDer = new double[wmSize-1];
      int ratio = (int) WorldParams.savePeriod/WorldParams.saveAverGenPeriod;
      Sorter sort = new Sorter();
      int[] toSort = new int[nIn * nAct];
      int[] sorted = new int[2];
      int phaseBasins = 0;

      double[] euclMADer = new double[(int) (wmSize)/ratio+1];
      for (int i = 0; i < (wmSize-1); i++){
        euclDer[i] = 0;
	for (int j = 0; j < (nIn*nAct); j++) {
          euclDer[i] += (averW[i][j] - averW[i+1][j])*(averW[i][j] - averW[i+1][j]);
	  averW[i][j] = Math.abs(averW[i][j] - averW[i+1][j]);
	  sumDer += averW[i][j];
          toSort[j] = (int) averW[i][j];
	}
	derivation[i] = sumDer/(nIn*nAct);
        euclDer[i] = Math.sqrt(euclDer[i]);
        euclMADer[(int) (i/ratio)] += euclDer[i];
        if (((i%ratio)==0)&&(i>1)){
          euclMADer[(int) ((i-1)/ratio)] =
              euclMADer[(int) ((i-1)/ratio)]/ratio;
          euclDerF.write(i*WorldParams.saveAverGenPeriod+"\t"+euclMADer[(int) ((i-1)/ratio)]+"\r\n");
        }
	sumDer = 0;
        sorted = sort.sortInd(toSort,2);
        phaseCompr.write(i*WorldParams.saveAverGenPeriod+"\t"+sorted[0]
                        +"\t"+averW[i][sorted[0]]+"\r\n");

      }
      euclDerF.close();
      phaseBasSpec.close();

       /*//saving basins
      for (int i = 0; i < (wmSize-1); i++){
        for (int b = 0; b < phaseBasins; b++) {
          phaseCompr.write(maxDerWght[i][phaseBasins][2]+"\t"+maxDerWght[i][phaseBasins][2]+"\t");
        }
        phaseCompr.write("\r\n");
      } */

      phaseCompr.close();

      // saving derivations file
      evolActivity.write("time"+"\t"+"sumDer"+"\t"+"euclDer"+"\r\n");
      int ttttt;
      for (int i = 0; i < (wmSize - 1); i++) {
        ttttt = i * WorldParams.saveAverGenPeriod;
        evolActivity.write(ttttt + "\t" + derivation[i] + "\t" + euclDer[i] +
                           "\t");
        for (int j = 0; j < (nIn * nAct); j++) {
          evolActivity.write(averW[i][j] + "\t");
        }
        evolActivity.write("\r\n");
      }
      evolActivity.close();


      // searching for cycles in phase space
      int cycleLenght = 10;
      //wmSize = 500;

      System.out.println("Searching for cycles in phase space...");
      float[][] jumps = new float[wmSize-1][(int)((wmSize-1)/(cycleLenght*2))];
      for (int dst = 0; dst < ((wmSize-1)/(cycleLenght*2)); dst++) {
	for (int i = 0; i < (wmSize-1); i++){
	  jumps[i][dst] = 0;
	}
      }

      for (int dst = 1; dst < ((wmSize-1)/(cycleLenght*2)); dst++) {
	//System.out.println("Cycle lenght = "+dst);
	for (int i = 0; i < (wmSize-dst*cycleLenght-1); i++){
	  double sum=0;
	  for (int j = 0; j < (nIn*nAct); j++){
	    sum = sum + (averW[i][j]-averW[i+dst*cycleLenght][j])*(averW[i][j]-averW[i+dst*cycleLenght][j]);
	  }
	  jumps[i][dst] = (float)Math.sqrt(sum);
	}
      }
      double[] averAutoCorr = new double[(wmSize - 1) / (cycleLenght * 2)];
      // saving phase tracking file
      for (int i = 0; i < (wmSize - 1); i++) {
      for (int dst = 1; dst < ( (wmSize - 1) / (cycleLenght * 2)); dst++) {
          phaseTrack.write(jumps[i][dst] + "\t");
          averAutoCorr[dst] += jumps[i][dst];
        }
        phaseTrack.write("\r\n");
      }
      phaseTrack.close();

      //saving average auto correlation
      FileWriter autoCorr = new FileWriter("averAutoCorr.txt");
      autoCorr.write("Autocorrelation averaged throug cycles"+"\r\n");
      autoCorr.write("Cycle Lenght"+ "\t"+"AutoCorr"+"\r\n");
      for (int dst = 1; dst < ( (wmSize - 1) / (cycleLenght * 2)); dst++) {
        averAutoCorr[dst] = averAutoCorr[dst] / (wmSize-dst*cycleLenght-1);
        autoCorr.write((dst*cycleLenght)+ "\t"+averAutoCorr[dst]+"\r\n");
      }
      autoCorr.close();

      //beginig of data processing >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

      if (!calcAverOnly){

        FileWriter averSGA = new FileWriter("averSGA.txt");
     averSGA.write("time"+"\t"+"Agents"+"\t"+"MSE"+"\t"+"Spec."+"\t"+"Spec.D."+"\t"+"AverAge"+"\t"+"AverGeneration"+"\r\n");

     FileWriter taxa = new FileWriter("taxa.txt");
     taxa.write("time"+"\t"+"Spec.ID"+"\t"+"Spec.Vol."+"\t"+"D.fromPrev."+"\t"+"weights"+"\r\n");

     FileWriter generations = new FileWriter("generations.txt");
     FileWriter phenoSp = new FileWriter("pheno_dyn.txt");
     phenoSp.write("time"+"\t"+"nSpec"+"\t"+"kinSelSp"+"\t"+"fighters"+"\t"+"sum"+"\t"+"AverKinValue"+"\r\n");
     FileWriter phenoVar = new FileWriter("pheno_var.txt");
     phenoVar.write("time"+"\t"+"BetweenPhenotypeVariance"+"\t"+"AvrgPhenotypeVariance"+"\r\n");
     FileWriter phenoMVar = new FileWriter("pheno_Mvar.txt");
     phenoMVar.write("time"+"\t"+"BetweenPhenotypeMVariance"+"\t"+"AvrgPhenotypeMVariance"+"\r\n");

     FileWriter agesVsTime = new FileWriter("agesDistr_bin250.txt");
     FileWriter wghtMSE = new FileWriter("wghtMSE.txt");
     FileWriter markerFMSE = new FileWriter("markerMSE.txt");

     wghtMSE.write("time"+"\t"+"Agents"+"\t"+"GenVar"+"\t"+"AvrgWeightVar"+"\r\n");
     markerFMSE.write("time"+"\t"+"Agents"+"\t"+"avrg.markersVariance"+"\t"+"variance of every marker ->"+"\r\n");
     agesVsTime.write("bin = 250"+"\r\n");

     FileWriter distDistrF = new FileWriter("distDistr.txt");
     FileWriter distDistrMF = new FileWriter("distDistrM.txt");
     FileWriter covDistrMF = new FileWriter("covDistrM.txt");
     FileWriter energyDistrF = new FileWriter("energyDistr_bin50.txt");
     FileWriter wpD= new FileWriter("wp_displ.txt");
     wpD.write("displacments in weights and pheno spaces"+"\r\n"+"time"+"\t"+"wD"+"\t"+"pD"+"\r\n");

     FileWriter averNOfKin= new FileWriter("kinR.txt");
     averNOfKin.write("Average number of kins in population"+"\r\n"+"time"+"\t"+"KinN"+"\t"+"KinAgntsInPop"+"\r\n");

     FileWriter kinFreqMap= new FileWriter("kinFreqMap.txt");

     FileWriter dynamicsOfCoopTypes= new FileWriter("coopTypes.txt");
     dynamicsOfCoopTypes.write("noFightNoCoop" + "\t"+
                   "noFightWeakCoop" + "\t"+
                   "fightNoCoop" + "\t"+
                   "fightWeakCoop" + "\t"+
                   "fightStrongCoop" + "\t"+
                   "fightStrongCoop2" + "\t"
                   +"\r\n"
);

      time = WorldParams.savePeriod;//14005000;
      boolean firstIteration = true;
      // initialazing kernels
      float[][] kernel = new float[nIn*nAct][nKern];
      float[][] kernelOld = new float[nIn*nAct][nKern];
      float[][] kernelPrev = new float[nIn*nAct][kernNeeded];
      float[][] kernelCur = new float[nIn*nAct][kernNeeded];
      float[] kernDist = new float[kernNeeded];
      float[] kernDev = new float[nKern];
      int[] wghtInKern = new int[kernNeeded];
      String[] kernID = new String[kernNeeded];
      String[] kernIDNext = new String[kernNeeded];
      boolean[] kernelPersist = new boolean[nKern];
      int[] wInKern = new int[nKern];
      Random rand = new Random(WorldParams.version);

      for (int i = 0; i < kernNeeded; i++){
	kernID[i] = "0";
	kernIDNext[i] = "0";
	for (int j = 0; j < (nIn*nAct); j++){
	  kernelPrev[j][i] = 0;
	  kernelCur[j][i] = 0;
	}
      }

      float [] oldWww = new float[nIn*nAct];

      while (time <= maxTime){

	//double sum;
	System.out.println("###############################################\r\n");
	System.out.println("Reading file log"+time+".txt ...");
	System.out.println();

	// getting number of lines in file
	Vector w = new Vector();
	BufferedReader g = new BufferedReader(new FileReader("log"+time+".txt"));
	StringTokenizer ss = new StringTokenizer(g.readLine());
	String str =  new String(g.readLine());
	while (str!=null){
	  w.addElement(str);
	  str = g.readLine();
	}
	g.close();
	int wSize = w.size()-2;
	w.removeAllElements();

	// creating containers for data
	int[] id = new int [wSize];
	int[] age = new int [wSize];
	int[] generation = new int [wSize];
	int[][] www = new int[nIn*nAct][wSize];
	int[][] marker = new int[WorldParams.markerLength][wSize];
        int[] energy = new int[wSize];

	// reading data
	BufferedReader g1 = new BufferedReader(new FileReader("log"+time+".txt"));
	StringTokenizer ss1 = new StringTokenizer(g1.readLine());
	String str1 =  new String(g1.readLine());
	for (int j = 0; j < wSize; j++){
	  ss1= new StringTokenizer(str1);
	  id[j] = (new Integer(ss1.nextToken())).intValue();
	  age[j] = (new Integer(ss1.nextToken())).intValue();
	  generation[j] = (new Integer(ss1.nextToken())).intValue();
          if (energyDistr) {
            energy[j] = (new Integer(ss1.nextToken())).intValue();
          }
          for (int i = 0; i < (nIn * nAct); i++) { // Reading of initial weight matrix
            www[i][j] = (new Integer(ss1.nextToken())).intValue();
	  }
	  for (int i = 0; i < (WorldParams.markerLength); i++) { // Reading markers
	    marker[i][j] = (new Integer(ss1.nextToken())).intValue();
	  }
	  str1 = g1.readLine();
	}
	g1.close();
        double[] enrgD = new double[100];
        enrgD = sort.freq(energy,50,100);
        for (int i = 0; i < 100; i++){
          energyDistrF.write(enrgD[i] + "\t");
        }
        energyDistrF.write("\r\n");
	nAgents = wSize;

	System.out.println("Searching species ...");
	System.out.println();

/*
	for (int i = 0; i < (nIn*nAct); i++){
	  for (int j = 0; j < nKern; j++){
	    kernel[i][j] = rand.nextInt(2*WorldParams.maxWeight)-WorldParams.maxWeight;
	    kernelOld[i][j] = kernel[i][j];
	    kernelPersist[j] = true;
	    wInKern[j] = wSize;
	  }
	}
	float maxKernShift = WorldParams.maxWeight;
	float kernShift = 0;
	int[] wClass = new int[wSize];
	int curKernels = nKern;
	//float [][] kDist = new float[nKern][nKern];
	float kernelD = 2*WorldParams.maxWeight;
	float distance = 0;
	boolean merging = true;

	int[][] averMarker = new int[nKern][markerL];

	//averaging markers for each kernel
	for (int i = 0; i < nKern; i++){
	  for (int j = 0; j < markerL; j++){
	    averMarker[i][j]=0;
	  }
	}
	for (int z = 0; z < wSize; z++){
	  for (int j = 0; j < markerL; j++){
	    averMarker[wClass[z]][j]+=marker[j][z];
	  }
	}
	for (int i = 0; i < nKern; i++){
	  if (kernelPersist[i]){
	    for (int j = 0; j < markerL; j++){
	      averMarker[i][j] = (int) (averMarker[i][j]/wInKern[i]);
	    }
	  }
	}

	//calculating MSE of kernels
	System.out.println();
	System.out.println("Calculating kernels dispertion, averaging ages and generations...");
	float[] meanKern = new float[nIn*nAct];
	for (int i = 0; i < nIn*nAct; i++){
	  meanKern[i] = 0;
	}
	float kernMSE = 0;
	for (int i = 0; i < nKern; i++){
	  if (kernelPersist[i]){
	    for (int j = 0; j < nIn*nAct; j++){
	      meanKern[j] = meanKern[j] + kernel[j][i];
	    }
	  }
	}
	for (int j = 0; j < nIn*nAct; j++){
	  meanKern[j] = meanKern[j]/curKernels;
	}

	for (int i = 0; i < nKern; i++){
	  if (kernelPersist[i]){
	    float dist = 0;
	    for (int j = 0; j < nIn*nAct; j++){
	      dist = dist + (kernel[j][i] - meanKern[j])*(kernel[j][i] - meanKern[j]);
	    }
	    dist = (float) Math.sqrt(dist);
	    kernMSE = kernMSE + dist;
	  }
	}
	kernMSE = kernMSE/curKernels;
*/
	//averaging ages and generations
	float averAge = 0;
	float averGeneration = 0;
	for (int i = 0; i < wSize; i++){
	  averAge = averAge + age[i];
	  averGeneration = averGeneration + generation[i];
	}
	averGeneration = averGeneration/wSize;
	averAge = averAge/wSize;


	//calculating variance of whole genotype
	double[] wMean = new double[nIn*nAct];
	double wMSE;
	double sumWMSE;
        wMSE=0;
	sumWMSE=0;
	for (int i = 0; i < (nIn*nAct); i++) {
	    wMean[i] = 0;
	  }
	for (int j = 0; j < wSize; j++){
	  for (int i = 0; i < (nIn*nAct); i++) {
	     wMean[i] += www[i][j];
	  }
	}
	for (int i = 0; i < (nIn*nAct); i++) {
	  wMean[i] = wMean[i]/wSize;
	}
	for (int j = 0; j < wSize; j++){
	  for (int i = 0; i < (nIn*nAct); i++) {
	     sumWMSE += (wMean[i]-www[i][j])*(wMean[i]-www[i][j]);
	  }
	  //sumWMSE = (int) sumWMSE;
	  wMSE+=sumWMSE;
	  sumWMSE=0;
	}
	wMSE = wMSE/wSize;

	//calculating variance for each weight
	float[] wwMean = new float[nIn*nAct];
	float[] wwwMSE = new float[nIn*nAct];
	float wwMSE=0;
	float sumwWMSE=0;
	for (int i = 0; i < (nIn*nAct); i++) {
	    wwMean[i] = 0;
	    wwwMSE[i] = 0;
	  }
	for (int j = 0; j < wSize; j++){
	  for (int i = 0; i < (nIn*nAct); i++) {
	     wwMean[i] += www[i][j];
	  }
	}
	for (int i = 0; i < (nIn*nAct); i++) {
	  wwMean[i] = (float) (wwMean[i]/wSize);
	}
	for (int i = 0; i < (nIn*nAct); i++) {
	  for (int j = 0; j < wSize; j++){
	    sumwWMSE += (www[i][j]-wwMean[i])*(www[i][j]-wwMean[i]);//Math.abs(www[i][j]-wwMean[i]);
	  }
	  wwwMSE[i] = (float) (sumwWMSE/wSize);
	  sumwWMSE=0;
	  wwMSE += wwwMSE[i];
	}
	wwMSE = (float)(wwMSE/(nIn*nAct));

	//calculating variance for each marker
	float[] markerMean = new float[WorldParams.markerLength];
	float[] markerMSE = new float[WorldParams.markerLength];
	float mMSE=0;
	float summMSE=0;
	for (int i = 0; i < (WorldParams.markerLength); i++) {
	    markerMean[i] = 0;
	    markerMSE[i] = 0;
	  }
	for (int j = 0; j < wSize; j++){
	  for (int i = 0; i < (WorldParams.markerLength); i++) {
	     markerMean[i] += marker[i][j];
	  }
	}
	for (int i = 0; i < (WorldParams.markerLength); i++) {
	  markerMean[i] = (float) (markerMean[i]/wSize);
	}
	for (int i = 0; i < (WorldParams.markerLength); i++) {
	  for (int j = 0; j < wSize; j++){
	    summMSE += (marker[i][j]-markerMean[i])*(marker[i][j]-markerMean[i]);
	  }
	  markerMSE[i] = (float) (summMSE/wSize);
	  summMSE=0;
	  mMSE += markerMSE[i];
	}
	mMSE = (float)(mMSE/(WorldParams.markerLength));

	//calculating phenotypes
        int[] oldSp;
        oldSp = pheno.curSpecies;
        int oldPhenoSize = 0;
        if (!firstIteration) oldPhenoSize = pheno.curSpecies.length;
	pheno.findSpecies(www);
        if (!universal) pheno.findPhenoVar(www,marker);
        double phenoD = 0;
        double wwwD = 0;
        int phenoSize = pheno.curSpecies.length;
        if (!firstIteration){
          for (int i = 0; i < phenoSize; i++) {
            if (i < oldPhenoSize) {
              phenoD += (oldSp[i] - pheno.curSpecies[i]) *
                  (oldSp[i] - pheno.curSpecies[i]);
            }
            else
              phenoD += pheno.curSpecies[i] * pheno.curSpecies[i];
          }
          phenoD = Math.sqrt(phenoD);
          for (int i = 0; i < (nIn*nAct); i++){
            wwwD += (oldWww[i] - wwMean[i])*(oldWww[i] - wwMean[i]);
          }
          wwwD = Math.sqrt(wwwD);
          wpD.write(time+"\t"+wwwD+"\t"+phenoD+"\r\n");
        }
        for (int i = 0; i < (nIn * nAct); i++) {
          oldWww[i] = wwMean[i];
          }
          firstIteration = false;


        //getting between agents weigths distribution

        if (getAverKinValue) {
          System.out.println(
              "Calculating distribution of distances between agents in weights space...");
          double[] distDistr;
          distDistr = sort.distDistr(wSize, www, (nIn * nAct));
          int ld = distDistr.length;
          for (int i = 0; i < ld; i++) {
            distDistrF.write(distDistr[i] + "\t");
          }
          distDistrF.write("\r\n");

          //getting between agents' markers' distances distribution
          System.out.println(
              "Calculating distribution of distances between agents in markers space...");
          double[] distDistrM;
          distDistrM = sort.distDistr(wSize, marker, WorldParams.markerLength);
          ld = distDistrM.length;
          for (int i = 0; i < ld; i++) {
            distDistrMF.write(distDistrM[i] + "\t");
          }
          distDistrMF.write("\r\n");


//getting r = cov(x,y)/cov(y,y) between agents' markers'
          System.out.println(
              "Calculating covariances...");
          double[] covDistrM;
          covDistrM = sort.covDistr(wSize, marker, WorldParams.markerLength);
          ld = covDistrM.length;
          for (int i = 0; i < ld; i++) {
            covDistrMF.write(covDistrM[i] + "\t");
          }
          covDistrMF.write("\r\n");
        }



	// saving output

	System.out.println("Saving output files...");

	averSGA.write(time+"\t"+wSize+"\t"+wMSE+"\t"+averAge+"\t"+averGeneration
                      +"\t"+pheno.extinct+"\r\n");//+curKernels+"\t"+kernMSE+"\t"+
	wghtMSE.write(time+"\t"+wSize+"\t"+wMSE+"\t"+wwMSE+"\t");
	markerFMSE.write(time+"\t"+wSize+"\t"+mMSE+"\t");
	for (int i = 0; i < (nIn*nAct); i++) {
	  wghtMSE.write(wwwMSE[i]+"\t");
	}
	wghtMSE.write("\r\n");
	for (int i = 0; i < (WorldParams.markerLength); i++) {
	  markerFMSE.write(markerMSE[i]+"\t");
	}
	markerFMSE.write("\r\n");

	//saving pheno

	phenoSp.write(time+"\t"+pheno.nCurSp+"\t"+pheno.kinSens+"\t"
                      +pheno.fightStat+"\t"+wSize+"\t"+pheno.averKinValue+"\t");
        if (!universal) {
          phenoVar.write(time + "\t" + pheno.averPhenoC + "\t" +
                         pheno.averPhenoVar + "\t");
          phenoMVar.write(time + "\t" + pheno.averPhenoMC + "\t" +
                         pheno.averPhenoMVar + "\t");
          for (int i = 0; i < pheno.nSpecies; i++) {
            phenoSp.write(pheno.curSpecies[i] + "\t");
            phenoVar.write(pheno.phenoVar[i] + "\t");
            phenoMVar.write(pheno.phenoMVar[i] + "\t");
          }
          phenoVar.write("\r\n");
          phenoMVar.write("\r\n");
        }
	phenoSp.write("\r\n");

        pheno.saveModeOfCooperation(dynamicsOfCoopTypes);

        if (getAverKinValue) {
          System.out.println("Calculating average number of kins...");

          int[] agentNKins = new int[nAgents];
          double averKinN = pheno.getKinN(www, marker, agentNKins);
          double[] kinNFreq = new double[100];
          kinNFreq = sort.freq(agentNKins, 10, 100); //getting distribution of kin relations
          System.out.println("=== " + averKinN);
          System.out.println("NKinSel = " + pheno.nKinSelA);
          averNOfKin.write(time + "\t");
          averNOfKin.write(averKinN + "\t");
          averNOfKin.write(pheno.nKinSelA + "\t" + "\t");
          for (int i = 0; i < 100; i++) {
            averNOfKin.write(kinNFreq[i] + "\t");
            kinNFreq[i] = 0;
          }
          averNOfKin.write("\r\n");

          kinNFreq = sort.freq(agentNKins, 1, 100); //geting detailed distribution of kin relations
          for (int i = 0; i < 100; i++) {
            kinFreqMap.write(kinNFreq[i] + "\t");
          }
          kinFreqMap.write("\r\n");
        }
        double[] ageDistr = new double[100];
        ageDistr = sort.freq(age,250,100);
        for (int i = 0; i < 100; i++) {
          agesVsTime.write(ageDistr[i]+"\t");
        }
        agesVsTime.write("\r\n");

	  /*generations.write(time);
	  for (int i=0; i < wSize; i++){
	      generations.write("\t"+generation[i]);
	  }
	  generations.write("\r\n");

	  agesVsTime.write(time);
	  for (int i=0; i < wSize; i++){
	      agesVsTime.write("\t"+age[i]);
	  }
	  agesVsTime.write("\r\n"); */

	time = time + WorldParams.savePeriod;
	  System.out.println();

      }// end of iteration<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      averSGA.close();
      taxa.close();
      generations.close();
      agesVsTime.close();
      wghtMSE.close();
      markerFMSE.close();
      phenoSp.close();
      phenoVar.close();
      phenoMVar.close();
      distDistrF.close();
      distDistrMF.close();
      covDistrMF.close();
      wpD.close();
      energyDistrF.close();
      averNOfKin.close();
      kinFreqMap.close();
      dynamicsOfCoopTypes.close();

      pheno.saveSpecies();
      }
      System.out.println("The End ...");

    }
    catch (IOException e) {
      System.out.println("IO Error");

    }
  }
}
