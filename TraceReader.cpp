#include <iostream>
#include <fstream>
#include <string>
using namespace std;

void LineReader(double &X, double &Y,string line) //Tokenize the line, get x and y values and check if x or y values are maximum or not
{   
  bool setdest = false;
  bool setX = false;
  bool setY = false;    
  
  char *str=(char*)line.c_str();  
  char * pch;  
  pch = strtok (str," ,_");  
  while (pch != NULL)
  {
	string token(pch);
	if (setX) //Si estoy leyendo X, entonces almaceno en X
	{		
		double x=atof(pch);	
		if (X<x)
			X=x;
		setX=false;
	}
	if (setY) //Si estoy leyendo Y, entonces almaceno en Y
	{		
		double y=atof(pch);	
		if (Y<y)
			Y=y;
		setY=false;	
	}
	if (setdest) // Si estoy leyendo X e Y, entonces procedo a leer Y ya que acabo de leer X
	{
		setY=true;
		setdest=false;
	}
	if (token == "X")
		setX=true;
	if (token == "Y")
		setY=true;
	if (token == "setdest")
	{
		setdest= true;
		setX=true;
	}   
    pch = strtok (NULL, " ,_");
  }  
}


void TraceReader () { //Get the maximun X and Y of the trace file to define the constraint area.
  double maxX=0.0;
  double maxY=0.0;
  
  string line;
  char filepath[] = "traces/alfonzo2.tcl";
  string filepath2 = "traces/alfonzo2.tcl"; 
  ifstream myfile (filepath2);  
  if (myfile.is_open())
  {
    while ( myfile.good() )
    {
      getline (myfile,line);      	  
	  LineReader(maxX,maxY,line);
    }
    myfile.close();
	
	printf("X maximo: %f\n", maxX);
	printf("Y maximo: %f\n", maxY);
  }

  else printf("Unable to open file"); 
}

int main () {        
  TraceReader();
  return 0;
}