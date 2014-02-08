import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

class ses {
  public static void main(String[] args)
  {
    try {
      FileWriter f = new FileWriter("er1.txt");
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
			/*/WorldParams.mammothCycle =
			(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
			WorldParams.mammothHealth =
			(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();*/
      WorldParams.mutation =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.grassIntencity =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      //WorldParams.mammothIntencity =
      //(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
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
      //WorldParams.eHunt =
      //(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      WorldParams.eGrass =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
      //WorldParams.eMammoth =
      //(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
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
			/*WorldParams.grassPerFlag =
			(new Boolean(new StringTokenizer(r.readLine()).nextToken())).booleanValue();
			WorldParams.grassPeriod =
			(new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();
			WorldParams.heat =
			(new Boolean(new StringTokenizer(r.readLine()).nextToken())).booleanValue(); */
      WorldParams.mapScale =
	  (new Integer(new StringTokenizer(r.readLine()).nextToken())).intValue();

      World2D world = new World2D();
      world.evolve(f, r);
      f.close();
      r.close();
    }
    catch (IOException e) { }
    System.out.println("simulation ended <<<<<<<<<<<<<<<<<<<<<<<<<");
    System.exit(0);
  }
}