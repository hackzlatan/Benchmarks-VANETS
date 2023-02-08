#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <fstream>
#include <time.h>
#include <map>
#include <vector>
#include <iomanip>
#include <limits>
#include <float.h>

using namespace std;
int numRSU=10;

void CoordRSU(double *x, double *y, double *z)
{
  int count = 0;  
  ifstream infile;
  
  infile.open("rsu.input", ifstream::in);  

  char bufferIn[256];
  memset(bufferIn, 0x00, 256);  
  if(infile.good())
  {
    while((!infile.eof()) && count<numRSU)
    {
	  
      infile.getline(bufferIn, 256);      	  
	  sscanf(bufferIn, "%lf  %lf  %lf",&x[count], &y[count],&z[count]);
      count++;	  
    }
  }      
  infile.close();
}

int main (int argc, char **argv)
{
  
  double *coordY= new double[numRSU];
  double *coordX= new double[numRSU];
  double *coordZ= new double[numRSU];
  CoordRSU(coordX,coordY,coordZ);
  for (int i=0;i<numRSU;i++)
  {
	  printf("x: %f y: %f z: %f \n",coordX[i],coordY[i],coordZ[i]);
  }
  return 0;
}