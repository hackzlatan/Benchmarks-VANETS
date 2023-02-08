//
// This program is a benchmark to evaluate the scalability of JiST/SWANS
// for VANET applications. Cars are created and are moving in a circular
// on-way road. The internal radius of the road is radiusCircle (meters).
// There are several lanes as specified by numLanes. There is one RSU in
// the right side of the circular road, 5 meters from the external lane.
// Each car is equiped with an OBU. Each car sends a unicast UDP datagram
// every messageFreqCar seconds to: (1) the RSU with a probability of 60%
// or (2) a random selected car with a probability of 40%. The RSU sends
// UDP datagrams every messageFreqRSU seconds to a random selected car.
// All the datagrams have a same payload length (messageLength in bytes).
// The distance between two cars in the same lane is carDistance (seconds).
// All the cars in a lane have the same speed as specified in the array
// speed (in km/h). The first speed is the speed of the cars in lane 0
// (internal lane), the second speed is the speed of the cars in lane 1
// (the one next to the internal lane), and so on.
//


package circle1W1RSU;

import java.util.Random;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.field.Fading;
import jist.swans.field.Field;
import jist.swans.field.Mobility;
import jist.swans.field.MovementListenerInterface;
import jist.swans.field.PathLoss;
import jist.swans.field.Spatial;
import jist.swans.misc.Location;
import jist.swans.misc.Mapper;
import jist.swans.misc.Util;
import jist.swans.net.PacketLoss;
import jist.swans.radio.RadioInfo;


class ShowMovementOfRadio implements MovementListenerInterface
{
   public void move(long time, Location loc, int nodeID)
   {
      System.out.printf("Movement Listener (time=%.2f): "+
                        "Radio %d in position: (%.2f, %.2f)\n",
                        (double) time/Constants.SECOND,
                        nodeID, loc.getX(), loc.getY());
   }
}


public class MobileAdHocNetworkCircle
{

   // Counting the total number of packets sent
   public static int totalNumberOfPacketsSent = 0;

   // Counting the total number of packets received
   public static int totalNumberOfPacketsReceived = 0;

   // The total number of cars
   public static int totalCars;

   // Number of cars per lane
   public static int numCarsLane[];

   public static Random random = new Random();
	 
   // Field
   public static Field field;
	 
   public static RadioInfo.RadioInfoShared radioInfo;
	   
   // For layer 3 network layer
   public static PacketLoss inLoss;
   public static PacketLoss outLoss;
   public static Mapper protMap;
 

   public static final double simTimeLimit = Parameters.SIM_TIME_LIMIT;
   public static final int numLanes = Parameters.NUM_LANES;
   public static final double speed[] = Parameters.SPEED;
   public static final double moveInterval = Parameters.MOVE_INTERVAL;
   public static final double laneWidth = Parameters.LANE_WIDTH;
   public static final double radiusCircle = Parameters.RADIUS_CIRCLE;
   public static final double border = Parameters.BORDER;
   public static final double carDistance = Parameters.CAR_DISTANCE;
   public static final int messageLength = Parameters.MESSAGE_LENGTH;
   public static final double messageFreqCar = Parameters.MESSAGE_FREQ_CAR;
   public static final double messageFreqRSU = Parameters.MESSAGE_FREQ_RSU;
   public static final double transRange = Parameters.TRANSMISSION_RANGE;
   public static final double memConsumption = Parameters.MEMORY_CONSUMPTION;

 
   public static void main(String args[])
   {
   if(numLanes != speed.length)
      {
      System.err.printf("Problem with Parameters.NUM_LANES and "+
                        "Parameters.SPEED");
      System.exit(1);
      }

   ShowResults results = new ShowResults();
   results.setStartTime();

   double tmp = Constants.SPEED_OF_LIGHT
                /(4.0*Math.PI*transRange*Constants.FREQUENCY_DEFAULT);
   double thresholdRange = Constants.TRANSMIT_DEFAULT
                +20.0*Util.log((float) tmp)/Constants.log10;

   // Mobility model
   Mobility mobility = null;

   float sizeX = (float) (2*(border+numLanes*laneWidth+radiusCircle));
   float sizeY = (float) (2*(border+numLanes*laneWidth+radiusCircle));

   // Spatial binning
   Spatial spatial;
   spatial = new Spatial.HierGrid(new Location.Location2D(sizeX, sizeY), 5);

   field = new Field(spatial, new Fading.None(), new PathLoss.FreeSpace(),
                     mobility, Constants.PROPAGATION_LIMIT_DEFAULT);

   // Just to track the movement of the radio
   // field.addMovementListener(new ShowMovementOfRadio());
 
   // Initialize shared radio information
   radioInfo = RadioInfo.createShared(
                  Constants.FREQUENCY_DEFAULT,
                  Constants.BANDWIDTH_DEFAULT,
                  Constants.TRANSMIT_DEFAULT,
                  Constants.GAIN_DEFAULT,
                  Util.fromDB(Constants.SENSITIVITY_DEFAULT), 
                  Util.fromDB(thresholdRange),
                  Constants.TEMPERATURE_DEFAULT, 
                  Constants.TEMPERATURE_FACTOR_DEFAULT, 
                  Constants.AMBIENT_NOISE_DEFAULT);

   inLoss = new PacketLoss.Zero();
   outLoss = new PacketLoss.Zero();
   protMap = new Mapper(Constants.NET_PROTOCOL_MAX);
   protMap.mapToNext(Constants.NET_PROTOCOL_UDP);
   protMap.mapToNext(Constants.NET_PROTOCOL_AODV);

   totalCars = 0;
   numCarsLane = new int[numLanes];
   for(int i=0; i<numLanes; i++)
      {
      // speed[i] is in km/h.
      double lengthLane = 2.0*Math.PI*
                             (radiusCircle+laneWidth/2.0+laneWidth*i);
      numCarsLane[i] = (int) (lengthLane/(speed[i]/3.6)/carDistance);
      totalCars += numCarsLane[i];
      }

   int indexCar=0;
   for(int lane=0; lane<numLanes; lane++)
      for(int i=0; i<numCarsLane[lane]; i++)
         {
         Car car = new Car();
         double cx = border+numLanes*laneWidth+radiusCircle;
         double cy = border+numLanes*laneWidth+radiusCircle;
         double r = radiusCircle+laneWidth/2.0+laneWidth*lane;
         double angle = 360.0/numCarsLane[lane]*i;  // Angle in degrees
         car.initCar(indexCar, cx, cy, r, angle, speed[lane], moveInterval,
                     messageLength, messageFreqCar);

         indexCar++;
         }

   Rsu rsu = new Rsu();
   double xPos = border+2*(radiusCircle+numLanes*laneWidth)+5.0;
   double yPos = border+radiusCircle+numLanes*laneWidth;
   rsu.initRsu(totalCars, xPos, yPos, messageLength, messageFreqRSU);

   JistAPI.sleep((long) (memConsumption*Constants.SECOND-JistAPI.getTime()));
   results.getProxy().showPartialResults();

   JistAPI.endAt((long) (simTimeLimit*Constants.SECOND));

   JistAPI.sleep(JistAPI.END-JistAPI.getTime());
   results.getProxy().showResults();
   }
}


