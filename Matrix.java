/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

/**
 *
 * @author Larry
 */
public class Matrix {

    /**
     * @param args the command line arguments
     */
    private double []matrix;
    private double []invMatrix;
    private double []vector;
    private double []solution;
        
    private static int size;
    private static final double carDistance=2.0;
    private static final double laneWidth=2.6;
    private static final int totalCars=100;
    private static final int speed[]={80,100,120};
    
    public Matrix(int size)
    {
        this.size=size;       
        matrix = new double[size*size];
        invMatrix=new double[size*size];
        vector = new double[size];
        solution = new double[size];
        
        for (int i=0; i<size;i++)
        {
            solution[i]=0.0;
        }
                 
    }
    
    public double[] getsolution () {
      return solution;
   }
    
    public void inverseMatrix()
{
   /* This function calculates the inverse of a square matrix
    *
    *
    * Notes: the matrix must be invertible
    *
    */

   /* Matrix size must be larger than one */
   if(size<=1){
      System.err.printf("Matrix size must be larger than one");
      System.exit(1);
   }

   /* Loop variables */
   int i, j, k;

   /*  Copy the input matrix to output matrix */
   for(i=0; i<size*size; i++){
      invMatrix[i]=matrix[i];
   }
   /* Add small value to diagonal if diagonal is zero */
   for(i=0; i<size; i++)
   {
      j=i*size+i;
      if((invMatrix[j]<1e-12)&&(invMatrix[j]>-1e-12))
      {
         invMatrix[j]=1e-12;
      }
   }


   for(i=1; i<size; i++) {
        invMatrix[i] /= invMatrix[0];
    } /* normalize row 0 */

   for (i=1; i<size; i++)
   {
      for(j=i; j<size; j++)   /* do a column of L */
      {
         double sum = 0.0;
         for(k=0; k<i; k++) {
              sum += invMatrix[j*size+k] * invMatrix[k*size+i];
          }

         invMatrix[j*size+i] -= sum;
      }

      if (i==size-1) {
           continue;
       }

      for(j=i+1; j<size; j++)  /* do a row of U */
      {
         double sum = 0.0;
         for (k=0; k<i; k++) {
              sum += invMatrix[i*size+k]*invMatrix[k*size+j];
          }

         invMatrix[i*size+j] = (invMatrix[i*size+j]-sum) / invMatrix[i*size+i];
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
            for(k=i; k<j; k++) {
                 tmp -= invMatrix[j*size+k]*invMatrix[k*size+i];
             }
         }
         invMatrix[j*size+i] = tmp / invMatrix[j*size+j];
      }
   }

   for(i=0; i<size; i++) /* invert U */
   {
      for(j=i; j<size; j++)
      {
         if(i==j) {
              continue;
          }

         double sum = 0.0;
         for(k=i; k<j; k++) {
              sum += invMatrix[k*size+j]*( (i==k) ? 1.0 : invMatrix[i*size+k] );
          }

         invMatrix[i*size+j] = -sum;
      }
   }

   for(i=0; i<size; i++) /* final inversion */
   {
      for(j=0; j<size; j++)
      {
         double sum = 0.0;
         for(k = ((i>j)?i:j); k<size; k++) {
              sum += ((j==k)?1.0:invMatrix[j*size+k])*invMatrix[k*size+i];
          }
         invMatrix[j*size+i] = sum;
      }
   }
}
    
public  void initializeVectorCircle ()
{
   for (int i=0; i<size-1; i++) 
   {
        vector[i]=laneWidth*(2.0*i+1)*Math.PI;
   }

   vector[size-1]=totalCars;
}

   public void  initializeMatrixCircle ()
   {
   for (int i=0; i<size; i++)
   {
      for (int j=0; j<size; j++)
      {
         if ((i==j) && (i!=size-1))
         {
             matrix[i*size+j]=(Math.abs(speed[j])/3.6)*carDistance;
             //matrix[i*size+j]=speed[j]*carDistance;
         }
         else if ((j==size-1)&&(i!=size-1))
         {
            matrix[i*size+j]=-2.0*Math.PI;
         }
         else if ((i==size-1)&&(j!=size-1))
         {
            matrix[i*size+j]=1;
         }
         else
         {
              matrix[i*size+j]=0.0;
         }
      }
   }
   }

   public void multiplication ()
   {
      for(int i=0; i<size; i++)
      {
         double sum=0.0;
         for(int j=0; j<size; j++) 
         {
              sum += (invMatrix[i*size+j]*vector[j]);
         } //row*column
         solution[i]=sum;
      }      
    }

    public void circleCalcule()
    {       
       this.initializeMatrixCircle();
       this.initializeVectorCircle();
       this.inverseMatrix();
       this.multiplication();
    }
    
    public void printSolucion()
    {
        for(int i=0;i<size-1;i++)
        {
            System.out.printf("Numcar Lane %d: %d\n",i, Math.round(solution[i]));
        }
        System.out.printf("Circle Radio : %f\n",solution[size-1]);
    }
    
    public void show()
    {
        System.out.printf("***************************************\n");								
	System.out.printf("Matrix: \n");								
	for(int i=0; i<size; i++)
	{				
		for(int j=0; j<size; j++) {
                System.out.printf(" %f   ",matrix[i*size+j]);
            }								
		System.out.printf("\n ");								

	}	
	System.out.printf("\nInverse:\n");
	for(int i=0; i<size; i++)
	{				
		for(int j=0; j<size; j++) {
                System.out.printf(" %f   ",invMatrix[i*size+j]);
            }								
		System.out.printf("\n ");
	}
	System.out.printf("\n ");
	for(int i=0; i<size; i++)
	{				
	  System.out.printf("Vector [%d] = %f \n",i,vector[i]);								
	}
	System.out.printf("\n ");
	for(int i=0; i<size; i++)
	{				
	  System.out.printf("solution [%d] = %f \n",i,solution[i]);								
	}
        
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        int numLanes=3;
        Matrix A= new Matrix(numLanes+1);
        A.circleCalcule();    
        A.show();
        //A.printSolucion();
        
    }
}
