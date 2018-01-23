import net.tomp2p.peers.Number160;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionMechanismImplTest {

    private static AuctionMechanismImpl peer0;
    private static AuctionMechanismImpl peer1;
    private static AuctionMechanismImpl peer2;

    @BeforeAll
    static void setup() throws Exception{
        peer0 = new AuctionMechanismImpl(0);
        peer1 = new AuctionMechanismImpl(1);
        peer2 = new AuctionMechanismImpl(2);
    }

    @AfterAll
    static void tearDown() {
        peer0.leave();
        peer1.leave();
        peer2.leave();
    }

     @Test
    void createDuplicateAuction() {
        try {
            assertTrue(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
            assertFalse(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuctionNoBids() {
        try {

            peer0.createAuction("TV2", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K");
            assertEquals(peer0.checkAuction("TV2"), "status: open\nbids: ");
            Thread.sleep(1000);
            assertEquals(peer0.checkAuction("TV2"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

   @Test
    void checkMultipleAuctions() {
        try {
            peer0.createAuction("Persiana", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "cdedf");
            //peer1.createAuction("Auto", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 28000, "Abarth 124 spyder");
            //peer1.createAuction("Lavatrice", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 200, "Lavatrice Bosch");
            peer1.placeAbid("Persiana", 2000);
            assertEquals(peer0.checkAuction("Persiana"), "status: open\nbids: [Peer 1: 2000.0] ");
            peer2.placeAbid("Persiana", 1500);
            assertEquals(peer0.checkAuction("Persiana"), "status: open\nbids: [Peer 1: 2000.0] [Peer 2: 1500.0] ");
            Thread.sleep(2000);
            assertEquals(peer0.checkAuction("Persiana"), "status: ended\nwinner: Peer 1\nprice: 1500.0\n");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void placeAbid() {
    }
}