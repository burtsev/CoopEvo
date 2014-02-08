public class FieldOfVision {
  public Cell forward,left,right,near;
  public void fillingFieldOfVision (Cell[][] cWorld, int x, int y, int dir) {
    near = cWorld[x][y];
    switch (dir) {

      case 0: //loking up
	if (x == 0) forward = cWorld[WorldParams.worldXsize-1][y];
	else forward = cWorld[x-1][y];
	right = cWorld[x][(y+1)%WorldParams.worldYsize];
	if (y == 0) left = cWorld[x][WorldParams.worldYsize-1];
	else left = cWorld[x][y-1];
	break;

      case 1: // loking right
	forward = cWorld[x][(y+1)%WorldParams.worldYsize];
	right = cWorld[(x+1)%WorldParams.worldXsize][y];
	if (x == 0) left = cWorld[WorldParams.worldXsize-1][y];
	else left = cWorld[x-1][y];
	break;

      case 2: //loking down
	forward = cWorld[(x+1)%WorldParams.worldXsize][y];
	if (y == 0) right = cWorld[x][WorldParams.worldYsize-1];
	else right = cWorld[x][y-1];
	left = cWorld[x][(y+1)%WorldParams.worldYsize];
	break;

      case 3: // loking left
	if (y == 0) forward = cWorld[x][WorldParams.worldYsize-1];
	else forward = cWorld[x][y-1];
	if (x == 0) right = cWorld[WorldParams.worldXsize-1][y];
	else right = cWorld[x-1][y];
	left = cWorld[(x+1)%WorldParams.worldXsize][y];
	break;
    } // Field of vision has filled
  }
}