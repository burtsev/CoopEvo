import java.util.*;
// Object Agent generates action at conditions of enviroment and genome
public class Agent {
  Agent() { } ;
  public int DoAction (FieldOfVision vision, Genome gnm, int maxEnergy, int diff) {
    int maxSum,maxN,sum[],inputValue[],min,sumTr,partSum;
    int nIn = Genome.nIn;
    int nAct = Genome.nAct;

    //filling vector of input values
    inputValue = new int[nIn];
    int k = maxEnergy;
    for ( int i = 0; i < nIn; i++) {
      inputValue[i] = 0;
    }
    inputValue[0] = k;
    if (vision.near.hereIsGrass) inputValue[1] = k;
    if (vision.left.hereIsGrass) inputValue[2] = k;
    if (vision.forward.hereIsGrass) inputValue[3] = k;
    if (vision.right.hereIsGrass) inputValue[4] = k;
    inputValue[5] = vision.near.agents.size()*1000;
    inputValue[6] = vision.left.agents.size()*1000;
    inputValue[7] = vision.forward.agents.size()*1000;
    inputValue[8] = vision.right.agents.size()*1000;
    vision.near.calcKin(gnm);
    inputValue[9] = vision.near.kinship;
    inputValue[10] = gnm.energy;
    inputValue[11] = maxEnergy - gnm.energy;
    inputValue[12] = (int) (diff*k/(2*WorldParams.maxMarkerValue));
    //inputValue[18] = gnm.deltaE * (int)(k/WorldParams.eGrass);
    //end filling

    if (World2D.time == 100000) {
      //inputValue[9] = 0;
      //inputValue[12] = 0;
      //gnm.input[9] = false;
      //gnm.input[12] = false;
      if(!gnm.action[6]){
        gnm.action[6] = true;
        for (int j = 0; j < nIn; j++){
            gnm.weight[j][6] = - WorldParams.maxWeight; //seting weights to emerged action to minimum
          }
      }
      //gnm.action[6] = true;
    }

    maxSum = -100000000;
    maxN = 0;
//    if (World2D.time < 5000000) {
   //   nAct = nAct-1;
//    }
    sum = new int[nAct];
    for ( int j = 0; j < nAct; j++) {
      if (gnm.action[j]) {
	sum[j] = 0;
	for ( int i = 0; i < nIn; i++) {
	  if (gnm.input[i]) sum[j] = sum[j]+gnm.weight[i][j]*inputValue[i];
	}
	if (sum[j] > maxSum) {
	  maxN = j;
	  maxSum = sum[j];
	}
      }
    }
    return (maxN);
  } // end of DoAction
} // end of Agent class