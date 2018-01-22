import net.tomp2p.peers.Number160;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionMechanismImplTest {

    @Test
    void createDuplicateAuction() {
        try {
            AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            assertTrue(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
            assertFalse(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuction() {
        try {
            AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            AuctionMechanismImpl peer1 = new AuctionMechanismImpl(1);
            AuctionMechanismImpl peer2 = new AuctionMechanismImpl(2);
            peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K");
            peer1.createAuction("Auto", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 28000, "Abarth 124 spyder");
            peer1.createAuction("Lavatrice", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 200, "Lavatrice Bosch");
            assertEquals(peer0.checkAuction("TV"), "status: open\nbids: ");
            peer1.placeAbid("TV", 2000);
            assertEquals(peer0.checkAuction("TV"), "status: open\nbids: [Peer 1: 2000.0] ");
            peer2.placeAbid("TV", 1500);
            peer0.placeAbid("Auto", 5000);
            Thread.sleep(1000);
            assertEquals(peer0.checkAuction("TV"), "status: ended\nwinner: Peer 1\nprice: 1500.0\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void placeAbid() {
    }
}