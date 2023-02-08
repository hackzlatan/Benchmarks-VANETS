
package circle1W1RSU;

import java.util.Random;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.Mac802_11;
import jist.swans.mac.MacAddress;
import jist.swans.misc.MessageBytes;
import jist.swans.misc.Location.Location2D;
import jist.swans.net.MessageQueue;
import jist.swans.net.NetAddress;
import jist.swans.net.NetIp;
import jist.swans.net.NetIpBase;
import jist.swans.radio.RadioNoiseIndep;
import jist.swans.route.RouteAodv;
import jist.swans.trans.TransUdp;
import ext.jist.swans.net.DropTailMessageQueue;


public class Car implements JistAPI.Entity
{
   private Random random = MobileAdHocNetworkCircle.random;

   private int carID;

   private double cx;
   private double cy;
   private double radiusCircle;
   private double angle;
   private double deltaAngle;
   private double speed;
   private double moveInterval;

   private int messageLength;
   private double messageFreqCar;

   private final int SRCPORT=3000;
   private final int DSTPORT=3002;

   private TransUdp carUdp;


   private String generateMessage(int size)
   {
   String result = "";

   for(int i = 0; i<size; i++)
      result = result + (char)('A'+(i%26));

   return result;
   }


   // Generated random sending interval
   private double getSendingTimeDelta()
   {
   double result = random.nextDouble();
   result /= 5.0;
   return (0.9+result)*messageFreqCar;
   }


   public void initCar(int carID, double cx, double cy,
               double radiusCircle, double startAngle, double speed,
               double moveInterval, int messageLength, double messageFreqCar)
   {
   this.carID = carID;
   this.cx = cx;
   this.cy = cy;
   this.radiusCircle = radiusCircle;
   this.angle = startAngle;
   this.speed = speed;
   this.moveInterval = moveInterval;
   this.messageLength = messageLength;
   this.messageFreqCar = messageFreqCar;

   double lengthLane = 2.0*Math.PI*radiusCircle;
   double timeForOneRound = lengthLane/speed*3.6;
   deltaAngle = moveInterval/timeForOneRound*360.0; // Angle in degrees

   RadioNoiseIndep carRadio = new RadioNoiseIndep(carID,
                                  MobileAdHocNetworkCircle.radioInfo);

   // Create a MAC layer for node 
   Mac802_11 carMac = new Mac802_11(new MacAddress(carID+1),
                                    carRadio.getRadioInfo());

   // Create a network layer for node
   NetAddress carNetAddr=new NetAddress(Parameters.IPCarNet+carID+1);
   NetIp carNet=new NetIp(carNetAddr, MobileAdHocNetworkCircle.protMap,
                MobileAdHocNetworkCircle.inLoss,
                MobileAdHocNetworkCircle.outLoss);
		   
   MessageQueue queue = new DropTailMessageQueue(Constants.NET_PRIORITY_NUM, 
                NetIpBase.MAX_QUEUE_LENGTH);

   // Create a UDP transport layer for node
   carUdp=new TransUdp();

   // Create an instance of AODV for node
   RouteAodv carAodv=new RouteAodv(carNetAddr);

   // Place node in the field at position (posX, posY)
   double posX=cx+radiusCircle*Math.cos(angle/360.0*2.0*Math.PI);
   double posY=cy+radiusCircle*Math.sin(angle/360.0*2.0*Math.PI);
   MobileAdHocNetworkCircle.field.addRadio(carRadio.getRadioInfo(),
           carRadio.getProxy(), new Location2D((float) posX, (float) posY));

   // Node entity hookup
   carRadio.setFieldEntity(MobileAdHocNetworkCircle.field.getProxy());
   carRadio.setMacEntity(carMac.getProxy());
   byte intID=carNet.addInterface(carMac.getProxy(), queue);
   carMac.setRadioEntity(carRadio.getProxy());
   carMac.setNetEntity(carNet.getProxy(), intID);
   carNet.setProtocolHandler(Constants.NET_PROTOCOL_UDP, carUdp.getProxy());
   carUdp.setNetEntity(carNet.getProxy());
   ReceptionOfUDPMsgCar carReceptionMessage;
   carReceptionMessage = new ReceptionOfUDPMsgCar(carNetAddr, DSTPORT);
   carUdp.addSocketHandler(DSTPORT, carReceptionMessage);
   carAodv.setNetEntity(carNet.getProxy());
   carNet.setProtocolHandler(Constants.NET_PROTOCOL_AODV, carAodv.getProxy());
   carNet.setRouting(carAodv.getProxy());
   carAodv.getProxy().start();

   JistAPI.sleep((long) (moveInterval*Constants.SECOND));
   this.movingCar();
   
   double deltaTimeFirst = random.nextDouble()*messageFreqCar;
   JistAPI.sleep((long) ((deltaTimeFirst-moveInterval)*Constants.SECOND));
   this.sendingMessage();
   }


   public void movingCar()
   {
   angle += deltaAngle;
   if(angle>=360.0)
      angle -= 360.0;

   double posX=cx+radiusCircle*Math.cos(angle/360.0*2.0*Math.PI);
   double posY=cy+radiusCircle*Math.sin(angle/360.0*2.0*Math.PI);
   MobileAdHocNetworkCircle.field.getProxy().moveRadio(carID,
                            new Location2D((float) posX, (float) posY));
   JistAPI.sleep((long) (moveInterval*Constants.SECOND));
   this.movingCar();
   }


   public void sendingMessage()
   {
   if(MobileAdHocNetworkCircle.random.nextDouble()<0.4)
      { // Send to a car
      int destCar = random.nextInt(MobileAdHocNetworkCircle.totalCars);
      while(destCar == carID)
         destCar = random.nextInt(MobileAdHocNetworkCircle.totalCars);

      carUdp.getProxy().send(new MessageBytes(generateMessage(messageLength)),
                        new NetAddress(Parameters.IPCarNet+destCar+1),
                        DSTPORT, SRCPORT, Constants.NET_PRIORITY_NORMAL);

//    System.out.printf("Out       (%.2f): from car %s to car %s\n",
//       (double) JistAPI.getTime()/Constants.SECOND,
//       ""+new NetAddress(Parameters.IPCarNet+carID+1)+":"+SRCPORT,
//       ""+new NetAddress(Parameters.IPCarNet+destCar+1)+":"+DSTPORT);
      }
   else
      { // Send to the RSU
      carUdp.getProxy().send(new MessageBytes(generateMessage(messageLength)),
                        new NetAddress(Parameters.IPRsuNet+1),
                        DSTPORT, SRCPORT, Constants.NET_PRIORITY_NORMAL);

//    System.out.printf("Out       (%.2f): from car %s to RSU %s\n",
//       (double) JistAPI.getTime()/Constants.SECOND,
//       ""+new NetAddress(Parameters.IPCarNet+carID+1)+":"+SRCPORT,
//       ""+new NetAddress(Parameters.IPRsuNet+1)+":"+DSTPORT);
      }

   MobileAdHocNetworkCircle.totalNumberOfPacketsSent++;

   JistAPI.sleep((long) (getSendingTimeDelta()*Constants.SECOND));
   this.sendingMessage();
   }
}

