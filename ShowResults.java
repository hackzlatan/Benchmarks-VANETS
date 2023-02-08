package circle1W1RSU;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.NumberFormat;


public class ShowResults implements ShowResultsInt
{
   // The variable for the time results
   private long startMillis;
   private long stopMillis;
   private GregorianCalendar startCal;
   private GregorianCalendar stopCal;

   private ShowResultsInt proxy;
   private InfoMemory infoMem = new InfoMemory();
   private double memConsumption = MobileAdHocNetworkCircle.memConsumption;


   public ShowResults()
   {
   proxy=(ShowResultsInt) JistAPI.proxy(this, ShowResultsInt.class);
   }

   public ShowResultsInt getProxy()
   {
   return proxy;
   }

   public void setStartTime()
   {
   startMillis = System.currentTimeMillis();
   startCal = new GregorianCalendar();
   }

   public void showResults()
   {
   int hour, minute, second;
   stopMillis = System.currentTimeMillis();
   stopCal = new GregorianCalendar();

   System.out.printf("-------------------------------------------------\n");
   hour=startCal.get(Calendar.HOUR_OF_DAY);
   minute=startCal.get(Calendar.MINUTE);
   second=startCal.get(Calendar.SECOND);
   System.out.printf("Start time is %02d:%02d:%02d\n", hour, minute, second);

   hour=stopCal.get(Calendar.HOUR_OF_DAY);
   minute=stopCal.get(Calendar.MINUTE);
   second=stopCal.get(Calendar.SECOND);
   System.out.printf("Stop time is %02d:%02d:%02d\n", hour, minute, second);

   double totalTime = (stopMillis-startMillis)/1000.0;
   System.out.printf("Total real time for simulation: %.4f seconds\n",
                     totalTime);

   System.out.printf("Total number of cars: %d\n",
                      MobileAdHocNetworkCircle.totalCars);
   for(int i=0; i<MobileAdHocNetworkCircle.numCarsLane.length; i++)
         System.out.printf("Car in lane %d: %d\n", i,
                      MobileAdHocNetworkCircle.numCarsLane[i]);

   System.out.printf("Number of package sent: %d\n",
                     MobileAdHocNetworkCircle.totalNumberOfPacketsSent);

   System.out.printf("Number of package received: %d\n",
                      MobileAdHocNetworkCircle.totalNumberOfPacketsReceived);
   }


   public void showPartialResults()
   {
   System.out.printf("-------------------------------------------------\n");

   System.out.printf("At simulation time: %.2f\n",
                     (double) JistAPI.getTime()/Constants.SECOND);

   System.out.printf("Number of package sent: %d\n",
                     MobileAdHocNetworkCircle.totalNumberOfPacketsSent);

   System.out.printf("Number of package received: %d\n",
                      MobileAdHocNetworkCircle.totalNumberOfPacketsReceived);

   NumberFormat comaFormat = NumberFormat.getNumberInstance();
   comaFormat.setGroupingUsed(true);
   String heapUsage = comaFormat.format(infoMem.getHeapUsage());
   System.out.printf("Actual heap memory: %s kB\n", heapUsage);
   String heapMax = comaFormat.format(infoMem.getHeapMax());
   System.out.printf("Maximum heap memory: %s kB\n", heapMax);
   String heapAverage = comaFormat.format(infoMem.getHeapAverage());
   System.out.printf("Average heap memory: %s kB\n", heapAverage);

   JistAPI.sleep((long) (memConsumption*Constants.SECOND));
   getProxy().showPartialResults();
   }
}


