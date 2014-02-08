import java.util.*;
public class Cell {
  public boolean hereIsGrass;
  public int kinship;
  public Vector agents = new Vector();

  // Initialazing cell
  Cell () {
    hereIsGrass = false;
    kinship = 0;
  } // end Cell constructor

  // function for filling the grid with grass patches with k% probability (j is random value)
  public void GrassRefresh (int j, int k) {
    if (j < k) hereIsGrass = true;
    else hereIsGrass = false;
  }

  public void calcKin (Genome curAgnt) {
    kinship=0;
    int nAgnts = agents.size();
    if (nAgnts>1){
      Genome agnt;
      int[] aver = new int[WorldParams.markerLength];
      for ( int z = 0; z < WorldParams.markerLength; z++) {
	aver[z] = 0;
      }
      for ( int z = 0; z < nAgnts; z++) {
	agnt = (Genome) agents.elementAt(z);
	for ( int i = 0; i < WorldParams.markerLength; i++) {
	  aver[i] = + agnt.marker[i];
	}
      }
      for ( int z = 0; z < WorldParams.markerLength; z++) {
	aver[z] = (int) (aver[z]/nAgnts);
	kinship = + (aver[z] - curAgnt.marker[z])*(aver[z] - curAgnt.marker[z]);
      }
      kinship = (int) (Math.sqrt(kinship)/nAgnts);
    }
  }
} // end Cell