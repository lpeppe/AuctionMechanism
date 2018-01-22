import net.tomp2p.peers.Number160;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionMechanismImplTest {

    private AuctionMechanismImpl peer0;
    private AuctionMechanismImpl peer1;
    private AuctionMechanismImpl peer2;

    @BeforeEach
    void setup() throws Exception{
        peer0 = new AuctionMechanismImpl(0);
        peer1 = new AuctionMechanismImpl(1);
        peer2 = new AuctionMechanismImpl(2);
    }

    @AfterEach
    void tearDown() {
        peer0.leave();
        peer1.leave();
        peer2.leave();
    }

    @Test
    void createDuplicateAuction() {
        try {
            //AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            assertTrue(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
            assertFalse(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
            //assertTrue(peer0.leave());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuctionNoBids() {
        try {
            //AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K");
            assertEquals(peer0.checkAuction("TV"), "status: open\nbids: ");
            Thread.sleep(1000);
            assertEquals(peer0.checkAuction("TV"), "status: ended\nwinner: none\n");
            //assertTrue(peer0.leave());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuctionMultipleBids() {
        try {
            //AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            //AuctionMechanismImpl peer1 = new AuctionMechanismImpl(1);
            //AuctionMechanismImpl peer2 = new AuctionMechanismImpl(2);
            peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K");
            peer1.placeAbid("TV", 2000);
            peer2.placeAbid("TV", 1500);
            assertEquals(peer0.checkAuction("TV"), "status: open\nbids: [Peer 1: 2000.0] [Peer 2: 1500.0] ");
            Thread.sleep(1000);
            assertEquals(peer0.checkAuction("TV"), "status: ended\nwinner: Peer 1\nprice: 1500.0\n");
            //assertTrue(peer0.leave());
            //assertTrue(peer1.leave());
            //assertTrue(peer2.leave());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuction() {
        try {
            //AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            //AuctionMechanismImpl peer1 = new AuctionMechanismImpl(1);
            //AuctionMechanismImpl peer2 = new AuctionMechanismImpl(2);
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
            //assertTrue(peer0.leave());
            //assertTrue(peer1.leave());
            //assertTrue(peer2.leave());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void placeAbid() {
    }
}