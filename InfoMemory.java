
package circle1W1RSU;


public class InfoMemory
{
   private Runtime runtime = Runtime.getRuntime();
   private int numTimes = 0;
   private long maximum = 0L;
   private long sum = 0L;


   public long getHeapUsage()
   {
   // runtime.gc();
   long result = runtime.totalMemory()-runtime.freeMemory();
   numTimes++;
   sum += result;
   if(result > maximum)
      maximum = result;

   return result/1024L;
   }


   public long getHeapMax()
   {
   return maximum/1024L;
   }


   public long getHeapAverage()
   {
   return sum/numTimes/1024L;
   }
}

