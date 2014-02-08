import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class World2D {
  public static Vector v = new Vector();	// Storage for all genomes
  public static Cell[][] cWorld = new Cell [WorldParams.worldXsize][WorldParams.worldYsize];	// New world
  public static int time;
  public static int population;
  World2D(){
  }
  public void evolve (FileWriter f, BufferedReader r) throws IOException
  {
    int T,i,j,xNew,xOld,yNew,yOld,n,totalAgents, maxAgent, x, y;


    int action;			// Action performing
    int nIn = Genome.nIn;
    int nAct = Genome.nAct;
    FileWriter saveGenome;		// Pointer to log file with genomes
    Genome bur, bur1;
    Grass[] grassPile;
    T = WorldParams.age;			// Total number of iterations
    maxAgent = 0;			// ID of the next agent
    totalAgents = 0;		// Number of agents


    LogSave simLog = new LogSave();
    Random rand = new Random(WorldParams.version);
    time = 0;
    population = 0;


    for ( int xx = 0; xx < WorldParams.worldXsize; xx++) {
      for ( int yy = 0; yy < WorldParams.worldYsize; yy++) {
        cWorld[xx][yy] = new Cell();
        //cWorld[xx][yy].hereIsGrass = false;
      }
    }

    // Initial filling the grid with grass patches
    int grQuant = (int) (WorldParams.worldXsize*WorldParams.worldYsize*WorldParams.grassIntencity/100);
    grassPile = new Grass[grQuant];

    //grass in array
    for (i = 0; i < grQuant; i++){
      grassPile[i] = new Grass();
      grassPile[i].x = rand.nextInt(WorldParams.worldXsize);
      grassPile[i].y = rand.nextInt(WorldParams.worldYsize);
      if (! cWorld[grassPile[i].x][grassPile[i].y].hereIsGrass){
	grassPile[i].age = rand.nextInt(WorldParams.grassCycle);
	cWorld[grassPile[i].x][grassPile[i].y].hereIsGrass = true;
      }
    }

    Agent executor = new Agent();

    // Filling the world randomly with initial population of agents.
    // If here is agent in the cell the new random number is chosen.

    String fileName = "weights.txt";

    while ( maxAgent < WorldParams.initPopulation) {
      bur = new Genome(WorldParams.maxEnergy,fileName);
      bur.dir = rand.nextInt(3);
      bur.id = maxAgent;
      bur.x = rand.nextInt(WorldParams.worldXsize);
      bur.y = rand.nextInt(WorldParams.worldYsize);
      bur.stackID = maxAgent;
      bur.dividePrevious = 0;
      bur.divideLast = 0;
      bur.age = 0;
      bur.generation = 0;
      cWorld[bur.x][bur.y].agents.addElement(bur);
      v.addElement(bur);
      maxAgent++;
      /*for (int ii = 0; ii < WorldParams.markerLength; ii++) {
        if ((maxAgent%2)==0) {
          bur.marker[ii]=-1000;
          bur.input[12] = false;
        }
        else {
          bur.marker[ii]=1000;
          bur.input[5] = false;
        }
      }*/
      System.out.println("Agent #"+maxAgent+" generated");
    }
    totalAgents = maxAgent; // Initially total number of agents is equal to the id of next agent
    FieldOfVision curFieldOfVision = new FieldOfVision();
    int prob;
    prob = WorldParams.grassIntencity;

    int p1,p2,valP;

    //creating Simulation Control
    SimulationControl simCtrl = new SimulationControl();
    simCtrl.show();

    // creating Map
    Map map = new Map();
    map.show();
    map.pleaseDraw = false;
    population = totalAgents;


    // evolution starts <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    for ( int t = 0; t < T; t++) {
      time = t;

      if (t%2000 == 0) System.out.println(t + " # " + totalAgents);

      //permutations of order of execution of agents
      int[] agentExct = new int[totalAgents];
      for ( int z = 0; z < totalAgents; z++) {
	agentExct[z] = z;
      }
      for ( int z = 0; z < totalAgents; z++) {
	p1 = rand.nextInt(totalAgents);
	p2 = rand.nextInt(totalAgents);
	if (p1!=p2){ // swaping
	  valP = agentExct[p1];
	  agentExct[p1] = agentExct[p2];
	  agentExct[p2] = valP;
	}
      }

      int ttl = totalAgents;

      for ( int zzz = 0; zzz < ttl; zzz++) { // For every agent do...
	int z;
	z = agentExct[zzz];
	bur = (Genome) v.elementAt(z);	// bur - current agent
	x = bur.x;
	y = bur.y;
	int diff=0; //nonkinship - distance in marker space

	// filling Field of agent's vision
	curFieldOfVision.fillingFieldOfVision (cWorld, bur.x, bur.y, bur.dir);

	// searching neighbour
	bur.cellNeighbourID = -1;
	cWorld[bur.x][bur.y].agents.remove(bur); //removing agent from cell
	int cellAgntSize = cWorld[bur.x][bur.y].agents.size();
	if (cellAgntSize>0) {
	  bur.cellNeighbourID = rand.nextInt(cellAgntSize);
	  bur1 = (Genome) cWorld[bur.x][bur.y].agents.elementAt(bur.cellNeighbourID);

	  // calculating marker distance between agents
	  for (int mark = 0; mark < WorldParams.markerLength; mark++) {
	    diff = + (bur.marker[mark] - bur1.marker[mark])*(bur.marker[mark] - bur1.marker[mark]);
	  }
	  diff = (int) (Math.sqrt(diff));
	}


	// getting action to be perfofmed
	bur.act = executor.DoAction(curFieldOfVision, bur, WorldParams.maxEnergy, diff);
	action = bur.act;
	bur.deltaE = bur.energy;

	//resting
	if (action == 0) bur.energy -= WorldParams.eRest;

	//eating
	if (action == 1) {
	  bur.energy -= WorldParams.eEat;
	  if (cWorld[x][y].hereIsGrass) {
	    bur.energy += WorldParams.eGrass;
	    //Energy cannot be more than maximum
	    if (bur.energy > WorldParams.maxEnergy) bur.energy = WorldParams.maxEnergy;
	    cWorld[x][y].hereIsGrass = false;
	  }
	}

	//moving
	if (action == 2) {
	  bur.energy -= WorldParams.eMove;
	  switch (bur.dir) {

	    case 0: //moving up
	      if (x == 0) xNew = WorldParams.worldXsize-1;
	      else  xNew = x-1;
	      bur.x = xNew;
	      break;

	    case 1://moving right
	      yNew = (y+1)%WorldParams.worldYsize;
	      bur.y = yNew;
	      break;

	    case 2://moving down
	      xNew = (x+1)%WorldParams.worldXsize;
	      bur.x = xNew;
	      break;

	    case 3://moving left
	      if (y == 0) yNew = WorldParams.worldYsize-1;
	      else yNew = y-1;
	      bur.y = yNew;
	      break;
	  }
	} // end moving forward

	// turning left
	if (action == 3) {
	  bur.energy -= WorldParams.eTurn;
	  bur.dir = (bur.dir+3)%4;
	}

	// turning right
	if (action == 4) {
	  bur.energy -= WorldParams.eTurn;
	  bur.dir = (bur.dir+1)%4;
	}

	// dividing
	if (action == 5) {
	  //Dividing takes the half of parent's energy to the offspring
	  bur.energy -= WorldParams.eDivide;
	  bur.dividePrevious = bur.divideLast;
	  bur.divideLast = t;
	  if (v.size()<(WorldParams.worldXsize*WorldParams.worldYsize*100)){
	    bur.energy = bur.energy/2;
	    bur1 = new Genome(bur, WorldParams.mutation, WorldParams.mutModul, WorldParams.maxWeight, rand);
	    bur1.id = maxAgent;
	    bur1.energy = bur.energy;
	    bur1.x = x;
	    bur1.y = y;
	    bur1.divideLast = t;
	    bur1.dividePrevious = t;
	    bur1.generation = bur.generation + 1;
	    totalAgents += 1;
	    maxAgent += 1;
	    bur1.stackID = totalAgents-1;
	    cWorld[x][y].agents.add(bur1);
	    v.addElement(bur1); //Add new agent to the end of the vector
	  }
	}

	//fighting
	if (action == 6) {
	  bur.energy -= WorldParams.eFight;
	  if (cellAgntSize>0) {
	    bur1 = (Genome) cWorld[x][y].agents.elementAt(bur.cellNeighbourID);
	    if (bur1.energy < (2*WorldParams.eFight)) {
	      bur.energy += bur1.energy;
	      bur1.energy -= 2*WorldParams.eFight;
	    }
	    else{
	      bur.energy += 2*WorldParams.eFight;//1.5*WorldParams.eFight;
	      bur1.energy -= 2*WorldParams.eFight;
	    }
	    if (bur.energy > WorldParams.maxEnergy){
	      bur.energy = WorldParams.maxEnergy;
	    }
	    cWorld[x][y].agents.setElementAt(bur1, bur.cellNeighbourID);
	    v.setElementAt(bur1, bur1.stackID);
	  }
	}

	// calculating energy loss
	bur.deltaE = bur.energy - bur.deltaE;

	bur.age++;

	//Storing agent in the same position in the vector after performing the action
	cWorld[bur.x][bur.y].agents.add(bur);
	v.setElementAt(bur, z);
      }

      // all agents made their actions

//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

      /*
      //introducing optimized agent
      if (time == 50000) {
        int g = 5;
        //for (int g = 0; g < 20; g++){
          bur1 = new Genome(WorldParams.maxEnergy, "wght_opt.txt");
          bur1.id = maxAgent;
          bur1.energy = WorldParams.maxEnergy/2;
          bur1.x = g;
          bur1.y = g;
          bur1.divideLast = t;
          bur1.dividePrevious = t;
          bur1.generation = 0;
          totalAgents += 1;
          maxAgent += 1;
          bur1.stackID = totalAgents - 1;
          cWorld[g][g].agents.add(bur1);
          v.addElement(bur1); //Add new agent to the end of the vector
        //}
      }
*/
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



      // If energy less than zero agent dies
      for (int z = totalAgents; z > 0; z--) {
	bur = (Genome) v.elementAt(z-1);
	if (bur.energy <= 0) {
	  cWorld[bur.x][bur.y].agents.remove(bur);
	  v.removeElementAt(z-1);
	  totalAgents--;
	}
      }

      //setting agents stackID's
      for (int z = totalAgents; z > 0; z--) {
	bur = (Genome) v.elementAt(z-1);
	cWorld[bur.x][bur.y].agents.remove(bur);
	bur.stackID = z-1;
	v.setElementAt(bur, bur.stackID);
	cWorld[bur.x][bur.y].agents.add(bur);
      }

      population = totalAgents;


      // Grass refreshing every grassCycle

      //grass in array
      for (int g=0; g<grQuant; g++){
	grassPile[g].age++;
	if (grassPile[g].age == WorldParams.grassCycle) {
	  cWorld[grassPile[g].x][grassPile[g].y].hereIsGrass = false;
	  do {
	    i =  rand.nextInt(WorldParams.worldXsize);
	    j = rand.nextInt(WorldParams.worldYsize);
	    grassPile[g].x = i;
	    grassPile[g].y = j;
	  }
	  while (cWorld[grassPile[g].x][grassPile[g].y].hereIsGrass);
	  cWorld[grassPile[g].x][grassPile[g].y].hereIsGrass = true;
	  grassPile[g].age = rand.nextInt(WorldParams.grassCycle);
	}
      }

      // Saving number of agents performing certain actions in file agents.txt
      if (t%WorldParams.period == 0) {
	simLog.saveLog(v,totalAgents,t);
      }

      // Saving  average genome
      if (t%WorldParams.saveAverGenPeriod == 0) {
	simLog.saveAverGen();
      }

      // Saving genomes in log file each savePeriod
      if (t%WorldParams.savePeriod == 0) {
	saveGenome = new FileWriter("log"+t+".txt");
	for (int z = 0; z < totalAgents; z++) {
	  bur = (Genome) v.elementAt(z);
	  bur.saveGenome(saveGenome);
	}
      }

      // Refreshing MAP
      if (simCtrl.showM){
	map.pleaseDraw = true;
	map.repaint();
	while(map.pleaseDraw&&map.isShowing) { }
      }

    } // <<<<<<< END of ITERATION >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    simLog.closeLog();
    double[][] weight = new double[nIn][nAct]; // Average weight matrix of final population
    int[] hasinput = new int[nIn];
    int[] hasaction = new int[nAct];
    for (int z = 0; z < totalAgents; z++) {
      bur = (Genome) v.elementAt(z);
      f.write("#: "+z+"\tID: "+bur.id+"\tE: "+bur.energy+"\tA: "+bur.act+"\tdeltaE "+bur.deltaE+"\r\n");
      for (int xx = 0; xx < nIn; xx++) {
	for (int yy = 0; yy < nAct; yy++) {
	  weight[xx][yy]+= bur.weight[xx][yy];
	  if (bur.action[yy]) hasaction[yy]++;
	}
	if (bur.input[xx]) hasinput[xx]++;
      }
    }
    f.write("Actions\r\n");
    for (int xx = 0; xx < nIn; xx++) {
      for (int yy = 0; yy < nAct; yy++) {
	if (totalAgents != 0) f.write((int) weight[xx][yy]/totalAgents+"\t");
	else f.write("#\t");
      }
      f.write(hasinput[xx]+"\r\n");
    }
    for (int yy = 0; yy < nAct; yy++)
      f.write(hasaction[yy]/nIn+"\t");
    map.removeAll();
    map.dispose();
    simCtrl.stopCtrl();
    simCtrl.removeAll();
    simCtrl.dispose();
  } // end World2D constructor
} // end World2D