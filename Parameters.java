
package circle1W1RSU;

public interface Parameters
{
   // Duration of simulation (simulation time)
   public static final double SIM_TIME_LIMIT = 10.0;

   // Number of lanes
   public static final int NUM_LANES = 3;

   // Speed in each lane (km/h)
   public static final double SPEED[] = {80.0, 100.0, 120.0};

   // Interval between update of position of cars (seconds)
   public static final double MOVE_INTERVAL = 0.1;

   // Lane width (meters)
   public static final double LANE_WIDTH = 2.3;

   // Internal radius of circular road (meters)
   public static final double RADIUS_CIRCLE = 5000.0;

   // Space around circular road (meters)
   public static final double BORDER = 30.0;

   // Distance between cars (seconds)
   public static final double CAR_DISTANCE = 2.0;

   // Message length (bytes)
   public static final int MESSAGE_LENGTH = 1000;

   // Frequency messages sent by car (seconds)
   public static final double MESSAGE_FREQ_CAR = 3.0;

   // Frequency messages sent by RSU (seconds)
   public static final double MESSAGE_FREQ_RSU = 0.1;

   // Transmission range of radio (meters)
   public static final double TRANSMISSION_RANGE = 300.0;

   // Network address for the cars (190.169.0.0/16)
   public final static int IPCarNet = (190<<24)+(169<<16);

   // Network address for the RSUs (10.0.0.0/8)
   public final static int IPRsuNet = (10<<24);

   // Simulation time for showing memory consumption
   public final static double MEMORY_CONSUMPTION = 0.2;
}

