#include <iostream>
using namespace std;

int size = 4;
double speed[3] = {80.0, 100.0, 120.0};
double sep = 2.0;
#define pi 3.1416;
double laneWidth= 2.6;
int ncars = 100;

void inverseMatrix(double *matIn, double *matOut, int size)
{
   /* This function calculates the inverse of a square matrix
    *
    * inverseMatrix(double *matIn, double *matOut, int size)
    *
    * matIn : Pointer to input square double matrix (size x size)
    * matOut: Pointer to output square double matrix (size x size)
    * size:   The number of rows and columns
    *
    * Notes: the matrix must be invertible
    *
    */

   /* Matrix size must be larger than one */
   if(size<=1)
      return;


   /* Loop variables */
   int i, j, k;
 
   /*  Copy the input matrix to output matrix */
   for(i=0; i<size*size; i++)
      matOut[i]=matIn[i];
    
   /* Add small value to diagonal if diagonal is zero */
   for(i=0; i<size; i++)
   { 
      j=i*size+i;
      if((matOut[j]<1e-12)&&(matOut[j]>-1e-12))
         matOut[j]=1e-12;
   }
 

   for(i=1; i<size; i++)
      matOut[i] /= matOut[0]; /* normalize row 0 */
    
   for (i=1; i<size; i++)
   {
      for(j=i; j<size; j++)   /* do a column of L */
      {
         double sum = 0.0;
         for(k=0; k<i; k++)
            sum += matOut[j*size+k] * matOut[k*size+i];

         matOut[j*size+i] -= sum;
      }

      if (i==size-1)
         continue;

      for(j=i+1; j<size; j++)  /* do a row of U */
      {
         double sum = 0.0;
         for (k=0; k<i; k++)
            sum += matOut[i*size+k]*matOut[k*size+j];

         matOut[i*size+j] = (matOut[i*size+j]-sum) / matOut[i*size+i];
       }
   }

   for(i = 0; i<size; i++)  /* invert L */
   {
      for(j=i; j<size; j++)
      {
         double tmp = 1.0;
         if(i!=j)
         {
            tmp = 0.0;
            for(k=i; k<j; k++)
               tmp -= matOut[j*size+k]*matOut[k*size+i];
         }
         matOut[j*size+i] = tmp / matOut[j*size+j];
      }
   }

   for(i=0; i<size; i++) /* invert U */
   {
      for(j=i; j<size; j++)
      {
         if(i==j) continue;
 
         double sum = 0.0;
         for(k=i; k<j; k++)
            sum += matOut[k*size+j]*( (i==k) ? 1.0 : matOut[i*size+k] );

         matOut[i*size+j] = -sum;
      }
   }

   for(i=0; i<size; i++) /* final inversion */
   {
      for(j=0; j<size; j++)
      {
         double sum = 0.0;
         for(k = ((i>j)?i:j); k<size; k++)
            sum += ((j==k)?1.0:matOut[j*size+k])*matOut[k*size+i];
         matOut[j*size+i] = sum;
      }
   }
}

////////////////////////////My Code///////////////////////////////////////

void writeMatrix(double *matrix)
{
	for(int i=0; i<size; i++)
	{
		for(int j=0; j<size; j++)
			printf("%f  ",matrix[i*size+j]);
			printf("\n");
	}
}


void  InitializeVectorRectangle (double *vector)
{	
	for (int n=0; n<size;n++)
	{
		if (n==size-1)
			vector[n]=ncars;
		else
			vector[n]=laneWidth*(8*n+4);
		//printf("%f\n",vector[n]);
	}
} 

void  InitializeMatrixRectangle (double *matrix)
{	
	for (int i=0; i<size;i++)
		for (int j=0; j<size;j++)
		{
			if ((i==j)&&(i!=size-1))
				matrix[i*size+j]=speed[j]*sep;
			else
			{
				if ((j==size-1)&&(i!=size-1))
				{
					matrix[i*size+j]=-6;
				}
				else
				{
					if ((i==size-1)&&(j!=size-1))
						matrix[i*size+j]=1;
					else					
						matrix[i*size+j]=0;						
				}
			}
		}
		//writeMatrix(matrix);
}

void  InitializeVectorCircle (double *vector)
{	
	for (int n=0; n<size;n++)
	{
		if (n==size-1)
			vector[n]=ncars;
		else
			vector[n]=laneWidth*(2*n+1)*pi;
		//printf("%f\n",vector[n]);
	}
} 

void  InitializeMatrixCircle (double *matrix)
{	
	for (int i=0; i<size;i++)
		for (int j=0; j<size;j++)
		{
			if ((i==j)&&(i!=size-1))
				matrix[i*size+j]=speed[j]*sep;
			else
			{
				if ((j==size-1)&&(i!=size-1))
				{
					matrix[i*size+j]=-2*pi;
				}
				else
				{
					if ((i==size-1)&&(j!=size-1))
						matrix[i*size+j]=1;
					else					
						matrix[i*size+j]=0;						
				}
			}
		}
		//writeMatrix(matrix);
}

void multiplication (double *inverse, double *vector, double *solution)
{
	for(int i=0; i<size; i++)
	{		
		double sum=0;
		for(int j=0; j<size; j++)
			sum=sum+(inverse[i*size+j]*vector[j]);//row*column									
		solution[i]=sum;		
		printf("%f \n",solution[i]);			
	}	
}

void show (double *matrix,double *inverse, double *vector, double *solution)
{
	printf("***************************************\n");								
	printf("Matrix: \n");								
	for(int i=0; i<size; i++)
	{				
		for(int j=0; j<size; j++)
			printf(" %f   ",matrix[i*size+j]);								
		printf("\n ");								

	}	
	printf("\nInverse:\n");
	for(int i=0; i<size; i++)
	{				
		for(int j=0; j<size; j++)
			printf(" %f   ",inverse[i*size+j]);								
		printf("\n ");
	}
	printf("\n ");
	for(int i=0; i<size; i++)
	{				
	  printf("Vector [%d] = %f \n",i,vector[i]);								
	}
	printf("\n ");
	for(int i=0; i<size; i++)
	{				
	  printf("solution [%d] = %f \n",i,solution[i]);								
	}
}

void Circle()
{
	double *matrix = new double[size*size];
	double *invMatrix=new double[size*size];
	double *vector = new double[size];
	double *solution = new double[size];
	
	InitializeMatrixCircle (matrix);
	InitializeVectorCircle (vector);
	inverseMatrix(matrix, invMatrix, size);
	multiplication (invMatrix, vector, solution);

	show(matrix,invMatrix,vector,solution);

	delete [] matrix;
	delete [] invMatrix;
	delete [] vector;
	delete [] solution;
}

void Rectangle()
{
	double *matrix = new double[size*size];
	double *invMatrix=new double[size*size];
	double *vector = new double[size];
	double *solution = new double[size];
	
	InitializeMatrixRectangle (matrix);
	InitializeVectorRectangle (vector);
	inverseMatrix(matrix, invMatrix, size);
	multiplication (invMatrix, vector, solution);

	delete [] matrix;
	delete [] invMatrix;
	delete [] vector;
	delete [] solution;
}

////////////////////////////My Code///////////////////////////////////////

int main()
{	
	printf("Vector Solucion Circle\n");		
	Circle ();
	//printf("\nVector Solucion Rectangle\n");		
	//Rectangle();	
	getchar();
	return 0;
}



