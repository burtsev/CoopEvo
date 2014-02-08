/**
 * <p>Title: Non Linear Fractal Interpolation Functions (FIF)</p>
 * <p>Description: calculating nlFIF</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ReAud</p>
 * @author Mikhail Burtsev
 * @version 1.0
 */

public class Sorter {
  public Sorter() {
  }

  public int[] sortInd(int[] m, int n) { //returns indexes of n maximal values in m[]
    int l = m.length;
    int[] sorted = new int[n];
    int[] order = new int[l];
    for (int i = 0; i < l; i++) {
      order[i] = i;
    }

    int swpF;
    for (int c = 0; c < n; c++) {
      for (int i = 0; i < (l - c - 1); i++) {
        if (m[i] > m[i + 1]) {
          swpF = m[i + 1];
          m[i + 1] = m[i];
          m[i] = swpF;
          swpF = order[i];
          order[i] = order[i+1];
          order[i+1] = (int) swpF;
        }
      }
      sorted[c] = order[l - c - 1];
    }
    return sorted;
  }

// returns frequences of distances between genotypes
  public double[] distDistr (int popSize, int[][] weights, int l){
    double[] distr = new double[250];
    //int maxR = (int) Math.sqrt(l)*2*WorldParams.maxWeight;
    for (int i = 0; i < (popSize-1); i++){
      for (int j = (i+1); j < popSize; j++){
        int d = 0;
        for (int k = 0; k < l; k++){
          d+=(weights[k][i]-weights[k][j])*(weights[k][i]-weights[k][j]);
        }
        d = (int) Math.sqrt(d)/(50);
        if (d >= 250) d = 249;
        distr[d]++;
      }
    }
    l = distr.length;
    double k = (popSize*(popSize-1))/2;
    for (int i = 0; i < l; i++){
        distr[i] = distr[i] / k;
    }
    return distr;
  }

// returns cov(x,y)/cov(x,x) of genotypes
  public double[] covDistr (int popSize, int[][] weights, int L){
    double[] distr = new double[250];
    //int maxR = (int) Math.sqrt(l)*2*WorldParams.maxWeight;
    for (int i = 0; i < (popSize-1); i++){
      for (int j = (i+1); j < popSize; j++){
        //calculating means for x and y
        int m1 = 0;
        for (int k = 0; k < L; k++){
          m1 += weights[k][i];
        }
        m1 = (int) (m1/L);
        int m2 = 0;
        for (int k = 0; k < L; k++){
          m2 += weights[k][j];
        }
        m2 = (int) (m2/L);
        int covXY = 0;
        int covXX = 0;
        for (int k = 0; k < L; k++){
          covXY += (weights[k][i] - m1)*(weights[k][j] - m2);
          covXX += (weights[k][i] - m1)*(weights[k][i] - m1);
        }
        int r = (int) ((covXY/covXX + 1) * 100);
        if (r >= 250) r = 249;
        if (r < 0) r = 0;
        distr[r]++;
      }
    }
    L = distr.length;
    double k = (popSize*(popSize-1))/2;
    for (int i = 0; i < L; i++){
        distr[i] = distr[i] / k;
    }
    return distr;
  }


  // returns frequences of values in array
  public double[] freq(int[] array, int bin, int size){
    double[] freq = new double[size];
    int l = array.length;
    for (int i = 0; i < l; i++) {
      if ( (array[i] / bin) < size)
        freq[ (int) (array[i] / bin)]++;
      else
        freq[size-1]++;
    }
    for (int i = 0; i < size; i++){
      freq[i] = freq[i]/l;
    }
    return freq;
  }
}