import java.util.*;
import java.io.*;

/**
 * <p>Title: Action Calculation</p>
 * <p>Description: Calculation of agents action</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: KIAM RAS</p>
 * @author Mikhail Burtsev
 * @version 1.0
 */

public class PhenoCalc {

  public int[][] popPhenotype;
  public int[] curSpecies;// number of agents for each phenotype
  public int fightStat;// number of fighting agents
  public int nSpecies;//current number of ever existed species
  public int nCurSp;//current number of non zero species
  public int kinSens;// number of kin selective agents in population
  public double[] phenoVar;// variance of genomes within given phenotype
  public double averPhenoVar;//summ variance of all genotypes within their phenotypes
  public double averPhenoC;//average variance of all centroids of genotypes within every phenotype
  public double[] phenoMVar;// variance of markers within given phenotype
  public double averPhenoMVar;//summ variance of all markers within their phenotypes
  public double averPhenoMC;//average variance of all centroids of markers within every phenotype
  public int extinct; //number of strategies died out during the time step

  public double averKinValue;//average kin treshold
  public int nKinSelA;

  int[][] testBed;
  int situationsToTest = 6;
  int actionsToTest = 4;
  int k = WorldParams.maxEnergy;
  int[][] species;
  int[][] agentPhenotype;
  boolean fullActions = false;
  int[] oldSpecies; //number of agents in every strategy on the previous time step
  int maxSpecies; //a number of possible strategies
  int[] coopMode; // 0 - peacefull no coop; 1 - peacfull coop;
  //2 - fight no coop; 3 - fight weak coop; 4 - fight strong coop

  // universal constructor, where
  // energyLvls - number of gradation of energy,
  // kinLvls - number of gradation of kinship
  public PhenoCalc(int energyLvls, int kinLvls, boolean fighters) {
    situationsToTest = (ActCalc.nIn - 5)*energyLvls*kinLvls;
    species = new int[100000][situationsToTest + 1];
    coopMode = new int[100000];
    nSpecies = 0;
    testBed = new int[situationsToTest][ActCalc.nIn];
    for (int i = 0; i < situationsToTest; i++) {
      for (int j = 0; j < ActCalc.nIn; j++) {
        if (j == 0)
          testBed[i][j] = k;
        else
          testBed[i][j] = 0;
      }
      if (ActCalc.highPopDensity){
        testBed[i][6] = 1000;
        testBed[i][7] = 1000;
        testBed[i][8] = 1000;
      }
    }
    int sumIndx=0;
    for (int i = 0; i < energyLvls; i++) {
      for (int z = 0; z < (ActCalc.nIn - 5); z++) {
        for (int j = 0; j < kinLvls; j++) {
          if (z < 4)
            testBed[sumIndx][z + 1] = k;
          if (z > 3)
            testBed[sumIndx][z + 1] = 1000;
          testBed[sumIndx][ActCalc.nIn -
              3] = (int) i * (k - 100) / (energyLvls - 1) + 100;
          testBed[sumIndx][ActCalc.nIn - 2] = (int) k -
              testBed[sumIndx][ActCalc.nIn - 3];
          if ((z == 4)||fighters) {
            testBed[sumIndx][z + 1] = 2000;
            if (ActCalc.noMarkers) {
              testBed[sumIndx][ActCalc.nIn - 1] = 0;
              testBed[sumIndx][ActCalc.nIn - 4] = 0;
            }
            else {
              testBed[sumIndx][ActCalc.nIn - 1] =
                  (int) (j * 1000 / (kinLvls - 1) * k /
                         (2 * WorldParams.maxMarkerValue) + 100);
              testBed[sumIndx][ActCalc.nIn -
                  4] = (int) (j * 1000 / (kinLvls - 1) + 100) / 2;
            }
          }
          sumIndx++;
        }
      }
    }
    fullActions = true;
  }

  // default constructor (for kinship)
  public PhenoCalc() {
    maxSpecies = 4096;
    species = new int[maxSpecies][situationsToTest + 1];
    nSpecies = 0;
    extinct = 0;
    oldSpecies = new int[maxSpecies];
    coopMode = new int[100000];
    testBed = new int[situationsToTest][ActCalc.nIn];
    for (int i = 0; i < situationsToTest; i++) {
      for (int j = 0; j < ActCalc.nIn; j++) {
        if (j == 0)
          testBed[i][j] = k;
        else
          testBed[i][j] = 0;
      }
      if (ActCalc.highPopDensity) {
        testBed[i][6] = 1000;
        testBed[i][7] = 1000;
        testBed[i][8] = 1000;
      }
    }

    //setting situations to test

    // energy low see no grass kin near
    testBed[0][ActCalc.nIn - 3] = 100; //energy
    testBed[0][ActCalc.nIn - 2] = k - testBed[0][ActCalc.nIn - 3]; //reversed energy
    testBed[0][1] = 0; // grass near
    testBed[0][5] = 2000; // agents in the knot
    testBed[0][9] = 50; // distance to the mean marker
    testBed[0][ActCalc.nIn -
        1] = (int) (100 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship)

    // energy low see no grass non kin near
    testBed[1][ActCalc.nIn - 3] = 100; //energy
    testBed[1][ActCalc.nIn - 2] = k - testBed[1][ActCalc.nIn - 3]; //reversed energy
    testBed[1][1] = 0; // grass near
    testBed[1][5] = 2000; // agents in the knot
    testBed[1][9] = 500; // distance to the mean marker
    testBed[1][ActCalc.nIn -
        1] = (int) (1000 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship)

    // mean energy see no grass kin near
    testBed[2][ActCalc.nIn - 3] = (int) (k / 2); //energy
    testBed[2][ActCalc.nIn - 2] = k - testBed[2][ActCalc.nIn - 3]; //reversed energy
    testBed[2][1] = 0; // grass near
    testBed[2][5] = 2000; // agents in the knot
    testBed[2][9] = 50; // distance to the mean marker
    testBed[2][ActCalc.nIn -
        1] = (int) (100 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship)

    // mean energy see no grass non kin near
    testBed[3][ActCalc.nIn - 3] = (int) (k / 2); //energy
    testBed[3][ActCalc.nIn - 2] = k - testBed[3][ActCalc.nIn - 3]; //reversed energy
    testBed[3][1] = 0; // grass near
    testBed[3][5] = 2000; // agents in the knot
    testBed[3][9] = 500; // distance to the mean marker
    testBed[3][ActCalc.nIn -
        1] = (int) (1000 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship)

    // a lot of mean energy see no grass kin near
    testBed[4][ActCalc.nIn - 3] = k - 100; //energy
    testBed[4][ActCalc.nIn - 2] = k - testBed[4][ActCalc.nIn - 3]; //reversed energy
    testBed[4][1] = 0; // grass near
    testBed[4][5] = 2000; // agents in the knot
    testBed[4][9] = 50; // distance to the mean marker
    testBed[4][ActCalc.nIn -
        1] = (int) (100 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship)

    // a lot of energy see no grass non kin near
    testBed[5][ActCalc.nIn - 3] = k - 100; //energy
    testBed[5][ActCalc.nIn - 2] = k - testBed[5][ActCalc.nIn - 3]; //reversed energy
    testBed[5][1] = 0; // grass near
    testBed[5][5] = 2000; // agents in the knot
    testBed[5][9] = 500; // distance to the mean marker
    testBed[5][ActCalc.nIn -
        1] = (int) (1000 * k / (2 * WorldParams.maxMarkerValue)); // marker distance (reversed kinship) */
  }

// constructor (without kinship)
  public PhenoCalc(int s) {
    situationsToTest = s;
    species = new int[100000][situationsToTest + 1];
    nSpecies = 0;
    testBed = new int[situationsToTest][ActCalc.nIn];
    for (int i = 0; i < situationsToTest; i++) {
      for (int j = 0; j < ActCalc.nIn; j++) {
        if (j == 0)
          testBed[i][j] = k;
        else
          testBed[i][j] = 0;
      }
      if (ActCalc.highPopDensity){
        testBed[i][6] = 1000;
        testBed[i][7] = 1000;
        testBed[i][8] = 1000;
      }
    }

    //setting situations to test

    // energy low
    testBed[0][ActCalc.nIn - 3] = 100; //energy
    testBed[0][ActCalc.nIn - 2] = k - testBed[0][ActCalc.nIn - 3]; //reversed energy
    testBed[0][1] = 0; // grass near
    testBed[0][5] = 2000; // agents in the knot
    testBed[0][9] = 0; // distance to the mean marker
    testBed[0][ActCalc.nIn - 1] = 0; // marker distance (reversed kinship)

    // mean energy
    testBed[1][ActCalc.nIn - 3] = (int) (k / 2); //energy
    testBed[1][ActCalc.nIn - 2] = k - testBed[1][ActCalc.nIn - 3]; //reversed energy
    testBed[1][1] = 0; // grass near
    testBed[1][5] = 2000; // agents in the knot
    testBed[1][9] = 0; // distance to the mean marker
    testBed[1][ActCalc.nIn - 1] = 0; // marker distance (reversed kinship)

    // a lot of energy
    testBed[2][ActCalc.nIn - 3] = k - 100; //energy
    testBed[2][ActCalc.nIn - 2] = k - testBed[2][ActCalc.nIn - 3]; //reversed energy
    testBed[2][1] = 0; // grass near
    testBed[2][5] = 2000; // agents in the knot
    testBed[2][9] = 0; // distance to the mean marker
    testBed[2][ActCalc.nIn - 1] = 0; // marker distance (reversed kinship)
  }


//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<         find species

  public void findSpecies(int[][] weight) {
    popPhenotype = new int[situationsToTest][ActCalc.nAct];
    curSpecies = new int[maxSpecies];
    agentPhenotype = new int[ActCalc.nAgents][situationsToTest];
    fightStat = 0;
    boolean fightSet = false;
    averKinValue = 0;
    extinct = 0;

    System.out.println("Serching for species in phenotypes space...");

    int[] curAction = new int[ActCalc.nAct];
    for (int i = 0; i < (ActCalc.nAgents); i++) { //for each agent
      for (int j = 0; j < (situationsToTest); j++) { //for every situation
        int max = -1000000000;
        int maxAct = -1;
        for (int k = 0; k < ActCalc.nAct; k++) { // calc action
          curAction[k] = 0;
          for (int z = 0; z < (ActCalc.nIn); z++) {
            curAction[k] += testBed[j][z] * weight[k + z * ActCalc.nAct][i];
          }
          if ((curAction[k] > max)&&(curAction[k] != 0)) {
            max = curAction[k];
            maxAct = k;
          }
        }
        if (maxAct == -1) maxAct = 0;
        popPhenotype[j][maxAct] += 1;
        if (fullActions) agentPhenotype[i][j] = maxAct;
          else {
            if ( (maxAct < 6) && (maxAct != 2))
              agentPhenotype[i][j] = 0; //doing nothing
            if (maxAct == 2)
              agentPhenotype[i][j] = 1; //running
            if (maxAct == 6)
              agentPhenotype[i][j] = 2; //fighting

           if (maxAct == 5) agentPhenotype[i][j] = 3; //dividing
          }
        if ((maxAct == 6)&&(!fightSet)) {
          fightStat++;
          fightSet = true;
        }
      }
      fightSet = false;
    }
    //searching for species
    kinSens = 0;
    for (int i = 0; i < ActCalc.nAgents; i++) {
      //searching for kin sensetive phenotypes
      boolean sens = false;
      for (int k = 0; k < (situationsToTest / 2); k++) {
        if (agentPhenotype[i][2 * k] != agentPhenotype[i][2 * k + 1])
          sens = true;
      }
      if (sens){
        kinSens++;
        if (ActCalc.getAverKinValue){
          int[] wght = new int[ActCalc.nAct*ActCalc.nIn];
          for (int w = 0; w < (ActCalc.nAct*ActCalc.nIn); w++){
            wght[w] = weight[w][i];
          }
          averKinValue += getKinValue(wght);
        }
      }
        // serching for species
      boolean equals = true;
      if (nSpecies > 0) {
        for (int j = 0; j < nSpecies; j++) {
          for (int k = 0; k < situationsToTest; k++) {
            if (species[j][k] != agentPhenotype[i][k]) {
              equals = false;
              break;
            }
          }
          if (equals) {
            curSpecies[j] += 1;
            break;
          }
          if (j < (nSpecies - 1))
            equals = true;
        }
        if (!equals) {
          for (int k = 0; k < situationsToTest; k++) {
            species[nSpecies][k] = agentPhenotype[i][k];
          }
          if (sens)
            species[nSpecies][situationsToTest] = 10;
          else
            species[nSpecies][situationsToTest] = -10;
          curSpecies[nSpecies] += 1;
          nSpecies += 1;
          System.out.println("Phenospec = " + nSpecies);
        }
      }
      else {
        for (int k = 0; k < situationsToTest; k++) {
          species[0][k] = agentPhenotype[i][k];
        }
        curSpecies[0] += 1;
        nSpecies += 1;
        System.out.println("Phenospec = " + nSpecies);
      }
    }

    // current number of phenotypes in population
    nCurSp = 0;
    for (int i = 0; i < nSpecies; i++) {
      if (curSpecies[i] != 0) {
        nCurSp++;
      }
      if ( (curSpecies[i] == 0) && (oldSpecies[i] > 0))
        extinct++;
      oldSpecies[i] = curSpecies[i];
    }
   averKinValue = averKinValue / kinSens;

  }


//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  calculate variance of phenotypes

  public void findPhenoVar(int[][] weight, int[][] marker) {
    int[][] meanW = new int[nSpecies][ActCalc.nIn * ActCalc.nAct]; // array of mean weights for given species
    int[] meanWAver = new int[ActCalc.nIn * ActCalc.nAct]; // mean of phenotypes centroid
    int[][] meanM = new int[nSpecies][WorldParams.markerLength]; // array of mean marker for given species
    int[] meanMAver = new int[WorldParams.markerLength]; // mean of phenotypes markers centroid

    for (int i = 0; i < nSpecies; i++) {
      for (int j = 0; j < (ActCalc.nIn * ActCalc.nAct); j++) {
        meanW[i][j] = 0;
      }
      for (int j = 0; j < WorldParams.markerLength; j++) {
        meanM[i][j] = 0;
      }
    }
    phenoVar = new double[nSpecies];
    phenoMVar = new double[nSpecies];
    //calculating mean values for every species
    int extPheno = 0;
    for (int i = 0; i < ActCalc.nAgents; i++) {
      // serching for species
      boolean equals = true;
      for (int j = 0; j < nSpecies; j++) {
        for (int k = 0; k < situationsToTest; k++) {
          if (species[j][k] != agentPhenotype[i][k]) {
            equals = false;
            break;
          }
        }
        if (equals) {
          for (int z = 0; z < (ActCalc.nIn * ActCalc.nAct); z++) {
            meanW[j][z] += weight[z][i];
          }
          for (int z = 0; z < WorldParams.markerLength; z++) {
            meanM[j][z] += marker[z][i];
          }
          break;
        }
        if (j < (nSpecies - 1))
          equals = true;
      }
    }
    //calculating mean of species centroids
    for (int j = 0; j < nSpecies; j++) {
      if (curSpecies[j] > 1) { // if more than one agent has given strategy do ..
        extPheno++;
        for (int z = 0; z < (ActCalc.nIn * ActCalc.nAct); z++) {
          meanW[j][z] = meanW[j][z] / curSpecies[j];
          meanWAver[z] += meanW[j][z];//mean of phenotypes centroid
        }
        for (int z = 0; z < WorldParams.markerLength; z++) {
          meanM[j][z] = meanM[j][z] / curSpecies[j];
          meanMAver[z] += meanM[j][z];//mean of phenotypes marker centroid
        }
      }
    }
    //calculating mean of phenotypes centroid
    for (int z = 0; z < (ActCalc.nIn * ActCalc.nAct); z++) {
      meanWAver[z] = meanWAver[z] / extPheno;
    }
    for (int z = 0; z < WorldParams.markerLength; z++) {
      meanMAver[z] = meanMAver[z] / extPheno;
    }
    for (int i = 0; i < ActCalc.nAgents; i++) {
      // serching for species
      boolean equals = true;
      for (int j = 0; j < nSpecies; j++) {
        for (int k = 0; k < situationsToTest; k++) {
          if (species[j][k] != agentPhenotype[i][k]) {
            equals = false;
            break;
          }
        }
        if (equals) {
          int d = 0;
          int m = 0;
          for (int z = 0; z < (ActCalc.nIn * ActCalc.nAct); z++) {
            d += (meanW[j][z] - weight[z][i]) * (meanW[j][z] - weight[z][i]);
          }
          for (int z = 0; z < WorldParams.markerLength; z++) {
            m += (meanM[j][z] - marker[z][i]) * (meanM[j][z] - marker[z][i]);
          }
          phenoVar[j] += d;
          phenoMVar[j] += m;
          break;
        }
        if (j < (nSpecies - 1))
          equals = true;
      }
    }
    averPhenoVar = 0;
    averPhenoC = 0;
    averPhenoMVar = 0;
    averPhenoMC = 0;
    for (int j = 0; j < nSpecies; j++) {
      if (curSpecies[j] > 1) {

        phenoVar[j] = phenoVar[j] / curSpecies[j];
        averPhenoVar += phenoVar[j];

        phenoMVar[j] = phenoMVar[j] / curSpecies[j];
        averPhenoMVar += phenoMVar[j];
        for (int z = 0; z < (ActCalc.nIn * ActCalc.nAct); z++) {
          averPhenoC += (meanWAver[z] - meanW[j][z])*(meanWAver[z] - meanW[j][z]);
        }
        for (int z = 0; z < WorldParams.markerLength; z++) {
          averPhenoMC += (meanMAver[z] - meanM[j][z])*(meanMAver[z] - meanM[j][z]);
        }
      }
      else{
        phenoVar[j] = 0;
        phenoMVar[j] = 0;
      }
    }
    if (nCurSp > 0){
      averPhenoVar = averPhenoVar/extPheno; // / ActCalc.nAgents;
      averPhenoMVar = averPhenoMVar/extPheno; // / ActCalc.nAgents;
      if (nCurSp > 1){
        averPhenoC = averPhenoC / extPheno;
        averPhenoMC = averPhenoMC / extPheno;
      }
      else{
        averPhenoC = 0;
        averPhenoMC = 0;
      }
    }
    else{
      averPhenoVar = 0;
      averPhenoMVar = 0;
    }
  }


//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< returns kin value for given agent

  public int getKinValue(int[] weight) {
    int kinValue = 0;
    int[] curAction = new int[ActCalc.nAct];
    int[] action = new int[situationsToTest];
    for (int kin = 1; kin < (2*WorldParams.maxMarkerValue); kin++){
      int in1 = (int) (kin * 10) / 2;
      int in2 = (int) (kin * 10 * k / (2 * WorldParams.maxMarkerValue));
      for (int j = 0; j < (situationsToTest); j++) { //for every situation
        int max = -1000000000;
        int maxAct = -1;
        for (int k = 0; k < ActCalc.nAct; k++) { // calc action
          curAction[k] = 0;
          for (int z = 0; z < (ActCalc.nIn); z++) {
            int in = testBed[j][z];
            if (z == (ActCalc.nIn - 4)) in = in1;
            if (z == (ActCalc.nIn - 1)) in = in2;
            curAction[k] += in * weight[k + z * ActCalc.nAct];
          }
          if ( (curAction[k] > max) && (curAction[k] != 0)) {
            max = curAction[k];
            maxAct = k;
          }
        }
        if (maxAct == -1)
          maxAct = 0;
        if (kin > 1) {
          if (action[j]!= maxAct) {
            kinValue = kin *10;
            break;
          }
        }
        else action[j] = maxAct;
      }
      if (kinValue > 0) break;
    }
    return kinValue;
  }

//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< returns average number of kins for every agent

  public double getKinN(int[][] weight, int[][] marker, int[] agntNKins) {
    int kinN = ActCalc.nAgents; //number of kin in the population for the given agent
    int averKinN = 0;
    int nKinSelAgnts = 0;
    int[] curAction = new int[ActCalc.nAct];
    int[] actionKin = new int[situationsToTest];
    int in1 = (int) (Math.sqrt((10 * WorldParams.markerMutRate)/2)) / 2;
    int in2 = (int) (Math.sqrt((10 * WorldParams.markerMutRate)/2) * k / (2 * WorldParams.maxMarkerValue));
    for (int agnt1 = 0; agnt1 < ActCalc.nAgents; agnt1++) {
      //System.out.print(agnt1+" | ");
      kinN = ActCalc.nAgents;
      //getting actions for kin
      for (int j = 0; j < (situationsToTest); j = j+2) { //for every situation
        int max = -1000000000;
        int maxAct = -1;
        for (int kk = 0; kk < ActCalc.nAct; kk++) { // calc action
          curAction[kk] = 0;
          for (int z = 0; z < (ActCalc.nIn); z++) {
            int in = testBed[j][z];
            if (z == (ActCalc.nIn - 4))
              in = in1;
            if (z == (ActCalc.nIn - 1))
              in = in2;
            curAction[kk] += in * weight[kk + z * ActCalc.nAct][agnt1];
          }
          if ( (curAction[kk] > max) && (curAction[kk] != 0)) {
            max = curAction[kk];
            maxAct = kk;
          }
        }
        if ( (maxAct < 6) && (maxAct != 2))
          actionKin[j] = 0; //doing nothing
        if (maxAct == 2)
          actionKin[j] = 1; //running
        if (maxAct == 6)
          actionKin[j] = 2; //fighting
        if (maxAct == 5)
          actionKin[j] = 3; //dividing

        //actionKin[j] = maxAct;
      }
      for (int agnt2 = 0; agnt2 < ActCalc.nAgents; agnt2++) {
        if (agnt1 != agnt2) {
          int dist = 0;
          for (int mInd = 0; mInd < WorldParams.markerLength; mInd++) { //distance to the partner
            dist += (marker[mInd][agnt1] - marker[mInd][agnt2]) *
                (marker[mInd][agnt1] - marker[mInd][agnt2]);
          }
          dist = (int) Math.sqrt(dist);
          for (int j = 0; j < (situationsToTest); j = j+2) { //for every situation
            int max = -1000000000;
            int maxAct = -1;
            for (int kk = 0; kk < ActCalc.nAct; kk++) { // calc action towards partner
              curAction[kk] = 0;
              for (int z = 0; z < (ActCalc.nIn); z++) {
                int in = testBed[j][z];
                if (z == (ActCalc.nIn - 4))
                  in = (int) (dist / 2);
                if (z == (ActCalc.nIn - 1))
                  in = (int) (dist * WorldParams.maxEnergy /
                              (2 * WorldParams.maxMarkerValue)); ;
                curAction[kk] += in * weight[kk + z * ActCalc.nAct][agnt1];
              }
              if ( (curAction[kk] > max) && (curAction[kk] != 0)) {
                max = curAction[kk];
                maxAct = kk;
              }
            }
            if (maxAct == -1)
              maxAct = 0;
            int maxActR = -1;
            if ( (maxAct < 6) && (maxAct != 2))
              maxActR = 0; //doing nothing
            if (maxAct == 2)
              maxActR = 1; //running
            if (maxAct == 6)
              maxActR = 2; //fighting
            if (maxAct == 5)
              maxActR = 3; //dividing

            if (actionKin[j] != maxActR) {
              kinN--;
              break;
            }
          }
        }
      }
      if (kinN < ActCalc.nAgents){
        agntNKins[agnt1] = kinN;
        averKinN += kinN;
        nKinSelAgnts++;
      }
    }
    double kinR;
    kinR = 0;
    double d1,d2;
    d1 = averKinN;
    d2 = nKinSelAgnts;
    if (nKinSelAgnts > 0)
      kinR = d1/d2;
    System.out.print("kinR = "+kinR);
    nKinSelA = nKinSelAgnts;
    return kinR;
  }


//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< save species to file

  public void saveSpecies() {
    try {
      FileWriter fspecies = new FileWriter("pheno_spec.txt");
      for (int i = 0; i < nSpecies; i++) {
        fspecies.write(i + "\t");
        for (int j = 0; j < (situationsToTest + 1); j++) {
          fspecies.write(species[i][j] + "\t");
        }
        fspecies.write(coopMode[i]+"\t");
        fspecies.write("\r\n");
      }
      fspecies.close();
    }
    catch (IOException e) {
      System.out.println("IO Error");
    }
  }

//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< save types of cooperation to file

  public void saveModeOfCooperation(FileWriter file) {
    int noFightNoCoop = 0;
    int noFightWeakCoop = 0;
    int fightNoCoop = 0;
    int fightWeakCoop = 0;
    int fightStrongCoop = 0;
    int fightStrongCoop2 = 0;
    boolean fight = false;
    boolean weakCoop = false;
    boolean strongCoop = false;
    boolean strongCoop2 = false;
    boolean noCoop = false;
    for (int i = 0; i < nSpecies; i++) {
      for (int j = 0; j < (situationsToTest + 1); j++) {
        if (species[i][j] == 2)
          fight = true;
      }
      if ( (species[i][0] == species[i][1]) && (species[i][2] == species[i][3]) &&
          (species[i][4] == species[i][5]))
        noCoop = true;
      if (!noCoop) {
        for (int j = 0; j < 3; j++) {
          if (species[i][j*2] == 1)
            weakCoop = true;
         if ( ( (species[i][j*2] == 0) || (species[i][j*2] == 3)) &&
              (species[i][j*2 + 1] == 2)&&(j < 2))
            strongCoop = true;
          //if ( ( (species[i][j*2] == 0) || (species[i][j*2] == 3)) &&
          //    (species[i][j*2 + 1] == 2)&&(j < 2))
          //  strongCoop2 = true;
        }
      }
      if (!fight && noCoop){ // doves
        noFightNoCoop += curSpecies[i];
        coopMode[i] = 0;
      }
      if (fight && noCoop){ // hawks
        fightNoCoop += curSpecies[i];
        coopMode[i] = 2;
      }
      if (!fight && !noCoop){ // cooperating doves
        noFightWeakCoop += curSpecies[i];
        coopMode[i] = 1;
      }
      if (fight && !noCoop && !strongCoop){ // ravens
        fightWeakCoop += curSpecies[i];
        coopMode[i] = 3;
      }
      if (fight && strongCoop){ // starlings
        fightStrongCoop += curSpecies[i];
        coopMode[i] = 4;
      }
      if (fight && strongCoop2){ // not used
        fightStrongCoop2 += curSpecies[i];
        coopMode[i] = 4;
      }

      fight = false;
      weakCoop = false;
      strongCoop = false;
      strongCoop2 = false;
      noCoop = false;
    }
    try {
        file.write(noFightNoCoop + "\t"+
                   noFightWeakCoop + "\t"+
                   fightNoCoop + "\t"+
                   fightWeakCoop + "\t"+
                   fightStrongCoop + "\t"+
                   fightStrongCoop2 + "\t");
        file.write("\r\n");
         }
    catch (IOException e) {
      System.out.println("IO Error");
    }
    }


}
