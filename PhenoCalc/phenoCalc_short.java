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

//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< save types of cooperation to file

  public void saveModeOfCooperation(FileWriter file) {
    int noFightNoCoop = 0;
    int noFightWeakCoop = 0;
    int fightNoCoop = 0;
    int fightWeakCoop = 0;
    int fightStrongCoop = 0;
    boolean fight = false;
    boolean weakCoop = false;
    boolean strongCoop = false;
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

      fight = false;
      weakCoop = false;
      strongCoop = false;
      noCoop = false;
    }
    try {
        file.write(noFightNoCoop + "\t"+
                   noFightWeakCoop + "\t"+
                   fightNoCoop + "\t"+
                   fightWeakCoop + "\t"+
                   fightStrongCoop);
        file.write("\r\n");
         }
    catch (IOException e) {
      System.out.println("IO Error");
    }
    }


}
