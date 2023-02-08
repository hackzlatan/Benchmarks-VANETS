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

int main (int argc, char **argv)
{
  int frequency = 0;
  double memoryUsage = 0.0;
  int cpuUsage = 0;
  
  int cpuUsageAcum = 0;
  double memoryUsageAcum = 0.0;
  
  ifstream infile;
  
  infile.open("params.out", ifstream::in);

  char bufferIn[256];
  memset(bufferIn, 0x00, 256);
  
  if(infile.good())
  {
    while(!infile.eof())
    {
      infile.getline(bufferIn, 256);
      sscanf(bufferIn, "%*s %*s     %*s   %*s %*s  %*s  %*s %*s   %d  %lf   %*s %*s",&cpuUsage, &memoryUsage);
      
      cpuUsageAcum += cpuUsage;
      memoryUsageAcum += memoryUsage;
      
      frequency++;
    }
  }
  
  cout << "Average CPU usage = " << cpuUsageAcum/frequency << "%" << endl;
  cout << "Average Memory usage = " << memoryUsageAcum/frequency << "%" << endl;
  
  infile.close();
  return 0;
}