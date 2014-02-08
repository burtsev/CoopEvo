//{$R Map.JFM}

import java.awt.*;
import java.util.*;
// Class Map
public class Map extends Frame
{
  final int MenuBarHeight = 0;
  boolean fForm_Create;
  public static boolean isShowing;
  int scale;
  public static boolean pleaseDraw;
  Genome agent;

  // Component Declaration
  // End of Component Declaration

  // Constructor
  public Map()
  {
    // Frame Initialization
    scale = WorldParams.mapScale;
    setForeground(Color.black);
    setBackground(Color.black);
    setFont(new Font("Dialog",Font.BOLD,12));
    setTitle("MAP");
    setLayout(null);


    // End of Frame Initialization

    // Component Initialization
    // End of Component Initialization

    // Add()s
    // End of Add()s
    InitialPositionSet();

    fForm_Create = true;
  }

  public void InitialPositionSet()
  {
    // InitialPositionSet()
    reshape(303,134,WorldParams.worldXsize*scale+10,WorldParams.worldYsize*scale+30);


    // End of InitialPositionSet()
    isShowing = true;
    fForm_Create = false;
  }

  public boolean handleEvent(Event evt)
  {
    // handleEvent()
    if (evt.id == Event.WINDOW_DESTROY && evt.target == this) Map_WindowDestroy(evt.target);
    // End of handleEvent()
    return super.handleEvent(evt);
  }

  public void update(Graphics g) {
    paint(g);
    setTitle("MAP time:"+World2D.time+"   agents:"+World2D.population);
  }

  public void paint(Graphics g)   {
    // paint()
    int x,y;
    Random place = new Random(WorldParams.version);
    g.translate(5,25);
    Color curColor = new Color(0,0,0);

    if (pleaseDraw) {
      for(int i = 0; i < WorldParams.worldXsize; i++)
	for(int j = 0; j < WorldParams.worldYsize; j++) {
      curColor = new Color(0,0,0);
      if (World2D.cWorld[i][j].hereIsGrass) curColor = new Color(0,100,0);
      g.setColor(curColor);
      g.fillRect(i*scale,j*scale,scale,scale);
      if (World2D.cWorld[i][j].agents.size()>0){
	for (int z=0; z<World2D.cWorld[i][j].agents.size();z++){
	  x = place.nextInt(scale-8);
	  y = place.nextInt(scale-8);
	  agent = (Genome) World2D.cWorld[i][j].agents.elementAt(z);
	  switch (agent.act){
	    case 0: //rest
	      curColor = new Color(255,255,255);
	      break;
	    case 1://eat
	      curColor = new Color(255,255,0);
	      break;
	    case 2://moving
	      curColor = new Color(100,100,250);
	      break;
	    case 3://turn left
	      curColor = new Color(0,0,255);
	      break;
	    case 4://turn right
	      curColor = new Color(0,0,255);
	      break;
	    case 5://divide
	      curColor = new Color(255,50,150);
	      break;
	    case 6://fight
	      curColor = new Color(255,0,0);
	      break;
	  }// end switch
	  g.setColor(curColor);
	  g.fillRect(i*scale+x,j*scale+y,8,8);
	}
      }

	}
    }
    pleaseDraw = false;
    if (fForm_Create) InitialPositionSet();
  }// End of paint()

  // Event Handling Routines
  public void Map_WindowDestroy(Object target)
  {
    hide();
    isShowing = false;
  }




  // End of Event Handling Routines
} // End of Class Map