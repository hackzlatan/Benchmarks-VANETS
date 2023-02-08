
package circle1W1RSU;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.trans.TransInterface;


class ReceptionOfUDPMsgRsu implements TransInterface.SocketHandler
{
   private NetAddress dst;
   private int dstPort;

   public ReceptionOfUDPMsgRsu(NetAddress dst, int dstPort)
   {
   this.dst = dst;
   this.dstPort = dstPort;
   }

   public void receive(Message msg, NetAddress src, int srcPort)
   {
   MobileAdHocNetworkCircle.totalNumberOfPacketsReceived++;

// System.out.printf("Reception (%.2f): from car %s to RSU %s\n",
//                   (double) JistAPI.getTime()/Constants.SECOND,
//                   ""+src+":"+srcPort, ""+dst+":"+dstPort);
   }
}


