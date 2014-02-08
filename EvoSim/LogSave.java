import java.util.*;
import java.io.*;

public class LogSave {
  FileWriter f1;
  FileWriter fAverGen;
  LogSave()throws IOException {
    f1 = new FileWriter ("agents.txt");
    fAverGen = new FileWriter ("aver_gen.txt");
    // f1 - pointer to file (agents.txt) with data on total number of agents, number of moving agents,
    // number of turning agents etc.
    f1.write("Experiment Parameters:\r\n\r\n");
    f1.write("Version:\t\t"+WorldParams.version+"\r\n");
    f1.write("World size:\t\t"+WorldParams.worldXsize+"x"+WorldParams.worldYsize+"\r\n");
    f1.write("Init population:\t"+WorldParams.initPopulation+"\r\n");
    f1.write("Number of iterations:\t\t\t"+WorldParams.age+"\r\n");
    f1.write("Grass cycle:\t\t"+WorldParams.grassCycle+"\r\n");
		/*f1.write("Mammoth cycle:\t\t"+WorldParams.mammothCycle+"\r\n");
		f1.write("Mammoth health:\t\t"+WorldParams.mammothHealth+"\r\n");*/
    f1.write("Mutatiom Rate:\t\t"+WorldParams.mutation+"\r\n");
    f1.write("Grass intencity:\t"+WorldParams.grassIntencity+"\r\n");
    //f1.write("Mammoths intencity:\t"+WorldParams.mammothIntencity+"\r\n");
    f1.write("Maximal energy:\t\t"+WorldParams.maxEnergy+"\r\n");
    f1.write("Divide:\t\t\t"+WorldParams.eDivide+"\r\n");
    f1.write("Move:\t\t\t"+WorldParams.eMove+"\r\n");
    f1.write("Eat:\t\t\t"+WorldParams.eEat+"\r\n");
    f1.write("Rest:\t\t\t"+WorldParams.eRest+"\r\n");
    f1.write("Turn:\t\t\t"+WorldParams.eTurn+"\r\n");
    f1.write("Fight:\t\t\t"+WorldParams.eFight+"\r\n");
    //f1.write("Hunt:\t\t\t"+WorldParams.eHunt+"\r\n");
    f1.write("Grass cost:\t\t\t"+WorldParams.eGrass+"\r\n");
    //f1.write("Mammoth cost:\t\t\t"+WorldParams.eMammoth+"\r\n");
    f1.write("Grass period:\t\t"+WorldParams.grassPeriod+"\r\n");
    f1.write("Module mutation rate:\t\t"+WorldParams.mutModul+"\r\n");
    f1.write("Marker length:\t\t"+WorldParams.markerLength+"\r\n");
    f1.write("Marker mutation rate:\t\t"+WorldParams.markerMutRate+"\r\n");
    f1.write("Marker max value:\t\t"+WorldParams.maxMarkerValue+"\r\n");
    //f1.write("Heat:\t\t"+WorldParams.heat+"\r\n");
    //f1.write("Grass period(flag):\t\t"+WorldParams.grassPerFlag+"\r\n");
    f1.write("Max weight:\t\t"+WorldParams.maxWeight+"\r\n");
    f1.write("Period\tAgents\tRest\tEat\tMove\tTurnLeft\tTurnRight\tDivide\tFight\tLevel\tgNear\tgLeft\tgForw\tgRight\taNear\taLeft\taForw\taRight\taKin\tEnergy\trEnergy\tMarker\taRest\taEat\taMove\taTurnLeft\taTurnRight\tactDivide\tactFight\tAvg.Ener  \r\n");
  }

  public void saveLog(Vector v, int totalAgents, int t)throws IOException {
    // inputs:
    // 0 constant level
    // 1 food1 is in the same knot there is the agent;
    // 2 food1 is in the knot on the left;
    // 3 food1 is in the knot on the forward;
    // 4 food1 is in the knot on the right;
    // 5 food1 is in the knot on the backward;
    // 6 food2 is in the same knot there is the agent;
    // 7 food2 is in the knot on the left;
    // 8 food2 is in the knot on the forward;
    // 9 food2 is in the knot on the right;
    // 10 food2 is in the knot on the backward;
    // 11 number of agents in the same knot there is the agent;
    // 12 number of agents in the knot on the left;
    // 13 number of agents in the knot on the forward;
    // 14 number of agents in the knot on the right;
    // 15 number of agents in the knot on the backward;
    // 16 number of hunting agents in the same knot there is the agent;
    // 17 neighbour hunting
    // 18 neighbour not hunting
    // 19 value of internal energy resource.
    // 20 energy - division threshold
    // 21 delta energy
    //
    // actions:
    // 0 to be at rest;
    // 1 to eat;
    // 2 to move forward:
    // 3 to move to the left;
    // 4 to move to the right;
    // 5 to move to the backward;
    // 6 to divide;
    // 7 to fight neighbour;
    // 8 to hunt.
    int[] in = new int[Genome.nIn];
    int[] out = new int[Genome.nAct];
    int[] act = new int[Genome.nAct];
    int averageEnergy = 0;
    float averageDividePeriod = 0;
    int agn=0;
    for (int z = 0;z < Genome.nIn;z++) in[z] = 0;
    for (int z = 0;z < Genome.nAct;z++) {
      out[z] = 0;
      act[z] = 0;
    }
    Genome bur = new Genome(WorldParams.maxEnergy,"weights.txt");
    for (int z = 0; z < totalAgents; z++) {
      bur = (Genome) v.elementAt(z);
      //System.out.println(bur.act);
      if (bur.act > -1) act[bur.act]++;
      for (int i = 0;i < Genome.nIn;i++) if (bur.input[i]) in[i]++;
      for (int i = 0;i < Genome.nAct;i++) if (bur.action[i]) out[i]++;
      averageEnergy += bur.energy;
      averageDividePeriod += (bur.divideLast - bur.dividePrevious);
      if ((bur.divideLast - bur.dividePrevious)!=0){
	agn++;
	//bur.dividePrevious=bur.divideLast;
      }
    }
    float aver = 0;
    if (totalAgents != 0) aver = averageEnergy/totalAgents;
    averageDividePeriod = averageDividePeriod/agn;
    f1.write(t+"\t"+totalAgents+"\t");
    for (int i = 0;i < Genome.nAct;i++) f1.write(act[i]+"\t");
    for (int i = 0;i < Genome.nIn;i++) f1.write(in[i]+"\t");
    for (int i = 0;i < Genome.nAct;i++) f1.write(out[i]+"\t");
    f1.write(aver+"\t"+averageDividePeriod+"\r\n");
  }

  public void saveAverGen() throws IOException {
    float[][] averGen = new float[Genome.nIn][Genome.nAct];
    Genome gen;
    for (int k = 0; k < World2D.population; k++){
      gen = (Genome) World2D.v.elementAt(k);
      for  (int i = 0; i < Genome.nIn; i++){
	for (int j = 0; j < Genome.nAct; j++){
	  averGen[i][j] += gen.weight[i][j];
	}
      }
    }
    for  (int i = 0; i < Genome.nIn; i++){
      for (int j = 0; j < Genome.nAct; j++){
	averGen[i][j] = averGen[i][j]/World2D.population;
	fAverGen.write (averGen[i][j]+"\t");
	averGen[i][j] = 0;
      }
    }
    fAverGen.write ("\r\n");
  }

  public void closeLog() throws IOException {
    f1.close();
    fAverGen.close();
  }
}