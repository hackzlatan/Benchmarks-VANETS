
package circle1W1RSU;

import java.util.Random;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.Mac802_11;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.misc.MessageBytes;
import jist.swans.misc.Location.Location2D;
import jist.swans.net.MessageQueue;
import jist.swans.net.NetAddress;
import jist.swans.net.NetIp;
import jist.swans.net.NetIpBase;
import jist.swans.radio.RadioNoiseIndep;
import jist.swans.route.RouteAodv;
import jist.swans.trans.TransUdp;
import jist.swans.trans.TransInterface;
import ext.jist.swans.net.DropTailMessageQueue;


public class Rsu implements JistAPI.Entity
{
   private Random random = MobileAdHocNetworkCircle.random;

   private int messageLength;
   private double messageFreqRSU;

   private final int SRCPORT=3000;
   private final int DSTPORT=3002;

   private TransUdp rsuUdp;


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
   return (0.9+result)*messageFreqRSU;
   }


   public void initRsu(int rsuID, double posX, double posY,
                       int messageLength, double messageFreqRSU)
   {
   this.messageLength = messageLength;
   this.messageFreqRSU = messageFreqRSU;

   RadioNoiseIndep rsuRadio = new RadioNoiseIndep(rsuID,
                                  MobileAdHocNetworkCircle.radioInfo);

   // Create a MAC layer for node 
   Mac802_11 rsuMac = new Mac802_11(new MacAddress(rsuID+1),
                                    rsuRadio.getRadioInfo());

   // Create a network layer for node
   NetAddress rsuNetAddr=new NetAddress(Parameters.IPRsuNet+1);
   NetIp rsuNet=new NetIp(rsuNetAddr, MobileAdHocNetworkCircle.protMap,
                MobileAdHocNetworkCircle.inLoss,
                MobileAdHocNetworkCircle.outLoss);
		   
   MessageQueue queue = new DropTailMessageQueue(Constants.NET_PRIORITY_NUM, 
                NetIpBase.MAX_QUEUE_LENGTH);

   // Create a UDP transport layer for node
   rsuUdp=new TransUdp();

   // Create an instance of AODV for node
   RouteAodv rsuAodv=new RouteAodv(rsuNetAddr);

   // Place node in the field at position (posX, posY)
   MobileAdHocNetworkCircle.field.addRadio(rsuRadio.getRadioInfo(),
           rsuRadio.getProxy(), new Location2D((float) posX, (float) posY));

   // Node entity hookup
   rsuRadio.setFieldEntity(MobileAdHocNetworkCircle.field.getProxy());
   rsuRadio.setMacEntity(rsuMac.getProxy());
   byte intID=rsuNet.addInterface(rsuMac.getProxy(), queue);
   rsuMac.setRadioEntity(rsuRadio.getProxy());
   rsuMac.setNetEntity(rsuNet.getProxy(), intID);
   rsuNet.setProtocolHandler(Constants.NET_PROTOCOL_UDP, rsuUdp.getProxy());
   rsuUdp.setNetEntity(rsuNet.getProxy());
   ReceptionOfUDPMsgRsu rsuReceptionMessage;
   rsuReceptionMessage = new ReceptionOfUDPMsgRsu(rsuNetAddr, DSTPORT);
   rsuUdp.addSocketHandler(DSTPORT, rsuReceptionMessage);
   rsuAodv.setNetEntity(rsuNet.getProxy());
   rsuNet.setProtocolHandler(Constants.NET_PROTOCOL_AODV, rsuAodv.getProxy());
   rsuNet.setRouting(rsuAodv.getProxy());
   rsuAodv.getProxy().start();

   double deltaTimeFirst = random.nextDouble()*messageFreqRSU;
   JistAPI.sleep((long) (deltaTimeFirst*Constants.SECOND));
   this.sendingMessage();
   }


   public void sendingMessage()
   {
   int destCar = random.nextInt(MobileAdHocNetworkCircle.totalCars);

   MobileAdHocNetworkCircle.totalNumberOfPacketsSent++;
   rsuUdp.getProxy().send(new MessageBytes(generateMessage(messageLength)),
                     new NetAddress(Parameters.IPCarNet+destCar+1),
                     DSTPORT, SRCPORT, Constants.NET_PRIORITY_NORMAL);

// System.out.printf("Out       (%.2f): from RSU %s to car %s\n",
//    (double) JistAPI.getTime()/Constants.SECOND,
//    ""+new NetAddress(Parameters.IPRsuNet+1)+":"+SRCPORT,
//    ""+new NetAddress(Parameters.IPCarNet+destCar+1)+":"+DSTPORT);

   JistAPI.sleep((long) (getSendingTimeDelta()*Constants.SECOND));
   this.sendingMessage();
   }
}

