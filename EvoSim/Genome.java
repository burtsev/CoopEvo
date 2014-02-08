import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

// Object Genome describes features of agent
public class Genome {
  public boolean input[],action[];
  public int id, act, x, dir, y, energy, weight[][], marker[], deltaE, cellNeighbourID, stackID, divideLast, dividePrevious, age, generation;
  // Id, performing action, direction, location and weight matrix of agent

  public static int nIn = 13, nAct = 7; // Number of inputs and outputs (actions)(+1)
  // inputs:
  // 0 constant level
  // 1 food1 is in the same knot there is the agent;
  // 2 food1 is in the knot on the left;
  // 3 food1 is in the knot on the forward;
  // 4 food1 is in the knot on the right;
  // 5 number of agents in the same knot there is the agent;
  // 6 number of agents in the knot on the left;
  // 7 number of agents in the knot on the forward;
  // 8 number of agents in the knot on the right;
  // 9 number of kin agents in the same knot there is the agent;
  // 10 value of internal energy resource.
  // 11 energy - division threshold
  // 12 kinship
  //
  //
  // actions:
  // 0 to be at rest;
  // 1 to eat;
  // 2 to move forward:
  // 3 to turn to the left;
  // 4 to turn to the right;
  // 5 to divide;
  // 6 to fight;

  // Constructor for initial agents
  Genome(int maxEnergy, String fileName) throws IOException {
    input = new boolean[nIn];
    action = new boolean[nAct];
    weight = new int[nIn][nAct];
    marker = new int[WorldParams.markerLength];
    act = -1;
    BufferedReader r = new BufferedReader(new FileReader(fileName));
    StringTokenizer ss = new StringTokenizer(r.readLine());
    ss.nextToken();
    for ( int i = 0; i < nIn; i++) { // Reading which receptors agent has
      input[i] = (new Boolean(ss.nextToken())).booleanValue();
    }
    ss = new StringTokenizer(r.readLine());
    ss.nextToken();
    for ( int j = 0; j < nAct; j++) { // Reading which effectors agent has
      action[j] = (new Boolean(ss.nextToken())).booleanValue();
    }
    r.readLine();
    for (int i = 0; i < nIn; i++) {
      ss = new StringTokenizer(r.readLine());
      ss.nextToken();
      for ( int j = 0; j < nAct; j++) { // Reading of initial weight matrix
	weight[i][j] = (new Integer(ss.nextToken())).intValue();
      }
    }
    for (int i = 0; i < WorldParams.markerLength; i++) {
      marker[i]=0;
    }

    r.close();
    energy = maxEnergy;	// initial energy is the maximal energy
    deltaE = 0;
    cellNeighbourID = -1;
  }

  // Constructor for offspring agent
  // parent - ancestor agent
  // mut - mutation rate
  // mutModul - probability of disabling/enabling of one of the receptors or effectors

  Genome(Genome parent, int mut, int mutModul, int maxWeight, Random r) {
    input = new boolean[nIn];
    action = new boolean[nAct];
    weight = new int[nIn][nAct];
    marker = new int[WorldParams.markerLength];
    act = -1;
    int k;
    for (int i = 0; i < nIn; i++) {
      k = r.nextInt(10000);
      if (k < mutModul)
	input[i] = ! parent.input[i]; // Inverting of parent's receptor
      else
	input[i] = parent.input[i];
    }
    for (int i = 0; i < nAct; i++) {
      k = r.nextInt(10000);
      if (k < mutModul){
        action[i] = !parent.action[i]; // Inverting of parent's effector
        if (action[i]){ //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
          for (int j = 0; j < nIn; j++){
            weight[j][i] = - maxWeight; //seting weights to emerged action to minimum
          }
        }  //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      }
      else
	action[i] = parent.action[i];
    }

    for ( int i = 0; i < nIn; i++) {
      if ( ! input[i]) { 				// Setting weights
	for ( int j = 0; j < nAct; j++) { 	// from effector to zero
	  weight[i][j] = 0;		// if agent doesn't have it
	}
      }
      else {
	for ( int j = 0; j < nAct; j++) {
	  // Mutating parent weights
          if (parent.action[j]) {
            weight[i][j] = parent.weight[i][j] + r.nextInt(2 * mut) - mut;
            // Checking for weight not to be more than max
            if (weight[i][j] > maxWeight)
              weight[i][j] = maxWeight;
            if (weight[i][j] < -maxWeight)
              weight[i][j] = -maxWeight;
          }
	}
      }
    }
    for (int j = 0; j < nAct; j++) {
      if ( ! action[j]) { 			// Setting weights
	for (int i = 0; i < nIn; i++) { 	// to the effector to zero
	  weight[i][j] = 0;		// if agent doesn't have it
	}
      }
    }
    // mutate marker
    int mutP;
    for (int i = 0; i < WorldParams.markerLength; i++) {
      mutP = r.nextInt(100);
      marker[i]=parent.marker[i];
      if (WorldParams.markerMutInt > mutP){
	mutP=0;
	while (!((mutP>(0.8*WorldParams.markerMutRate))||(mutP<(-0.8*WorldParams.markerMutRate)))){
	  mutP = r.nextInt(2*WorldParams.markerMutRate)-WorldParams.markerMutRate;
	}
	marker[i]=marker[i]+mutP;
	if (marker[i] > WorldParams.maxMarkerValue) marker[i] = WorldParams.maxMarkerValue;
	if (marker[i] < -WorldParams.maxMarkerValue) marker[i] = -WorldParams.maxMarkerValue;
      }
    }
    // Energy of the offspring is equal to parent's
    energy = parent.energy;
    deltaE = 0;
    cellNeighbourID = -1;
    dividePrevious = 0;
    divideLast = 0;
    age = 0;
    // Initial offspring direction
    dir = r.nextInt(3);
  } // End of offspring constructor

  // Save genome in log file
  public void saveGenome(FileWriter f) throws IOException {
    f.write(id+"\t"+age+"\t"+generation+"\t"+energy+"\t");
    for (int i = 0; i < nIn; i++) {
      for (int j = 0; j < nAct; j++) {
	f.write(weight[i][j]+"\t");
      }
    }
    for (int i = 0; i < WorldParams.markerLength; i++) {
      f.write(marker[i]+"\t");
    }
    f.write("\r\n");
  }

} // End of Genome class