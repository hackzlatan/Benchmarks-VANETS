
package circle1W1RSU;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.trans.TransInterface;


class ReceptionOfUDPMsgCar implements TransInterface.SocketHandler
{
   private NetAddress dst;
   private int dstPort;

   public ReceptionOfUDPMsgCar(NetAddress dst, int dstPort)
   {
   this.dst = dst;
   this.dstPort = dstPort;
   }

   public void receive(Message msg, NetAddress src, int srcPort)
   {
   MobileAdHocNetworkCircle.totalNumberOfPacketsReceived++;

// if(src.equals(new NetAddress(Parameters.IPRsuNet+1)))
//    {
//    System.out.printf("Reception (%.2f): from RSU %s to car %s\n",
//                      (double) JistAPI.getTime()/Constants.SECOND,
//                      ""+src+":"+srcPort, ""+dst+":"+dstPort);
//    }
// else
//    {
//    System.out.printf("Reception (%.2f): from car %s to car %s\n",
//                      (double) JistAPI.getTime()/Constants.SECOND,
//                      ""+src+":"+srcPort, ""+dst+":"+dstPort);
//    }
   }
}


