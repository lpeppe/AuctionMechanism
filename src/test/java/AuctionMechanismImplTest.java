import net.tomp2p.peers.Number160;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionMechanismImplTest {

    private static AuctionMechanismImpl peer0;
    private static AuctionMechanismImpl peer1;
    private static AuctionMechanismImpl peer2;
    private static AuctionMechanismImpl peer3;

    @BeforeAll
    static void setup() throws Exception{
        peer0 = new AuctionMechanismImpl(0);
        peer1 = new AuctionMechanismImpl(1);
        peer2 = new AuctionMechanismImpl(2);
        peer3 = new AuctionMechanismImpl(3);
    }

    @AfterAll
    static void tearDown() {
        peer0.leave();
        peer1.leave();
        peer2.leave();
        peer3.leave();
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
    void placeAbid() {
        try {
            peer0.createAuction("Auto", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 10000, "Alfa Giulia Quadrifoglio");
            assertEquals(peer1.placeAbid("Auto", 200000), "status: open\nbids: [Peer 1: 200000.0] ");
            Thread.sleep(1000);
            assertEquals(peer0.checkAuction("Auto"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void closedAuction() {
        try {
            peer0.createAuction("PC", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 3000, "Macbook Pro");
            Thread.sleep(1000);
            assertEquals(peer1.placeAbid("PC", 4000), "status: ended\nwinner: none\n");
            assertEquals(peer1.placeAbid("PC", 40), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {

        }
    }

    @Test
    void multipleBidsSamePeer() {
        try {
            peer0.createAuction("Borsa", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 300, "Chanel");
            peer1.placeAbid("Borsa", 1200);
            peer1.placeAbid("Borsa", 1300);
            assertEquals(peer0.checkAuction("Borsa"), "status: open\nbids: [Peer 1: 1300.0] ");
        }
        catch(Exception e) {

        }
    }

    @Test
    void leaveAuction() {
        try {
            peer0.createAuction("Forno", new Date(Calendar.getInstance().getTimeInMillis() + 7000), 3000, "Microonde");
            peer1.placeAbid("Forno", 12000);
            assertTrue(peer1.leave());
            assertEquals(peer1.checkAuction("Forno"), "status: open\nbids: [Peer 1: 12000.0] ");
            peer1 = new AuctionMechanismImpl(1);
            peer2.placeAbid("Forno", 130000);
            assertEquals(peer0.checkAuction("Forno"), "status: open\nbids: [Peer 1: 12000.0] [Peer 2: 130000.0] ");
            assertTrue(peer2.leave());
            Thread.sleep(5000);
            assertEquals(peer0.checkAuction("Forno"), "status: ended\nwinner: Peer 2\nprice: 12000.0\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkMultipleBids() {
        try {
            peer0.createAuction("Persiana", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "Veneziane");
            peer1.placeAbid("Persiana", 2000);
            assertEquals(peer0.checkAuction("Persiana"), "status: open\nbids: [Peer 1: 2000.0] ");
            peer2.placeAbid("Persiana", 1500);
            assertEquals(peer0.checkAuction("Persiana"), "status: open\nbids: [Peer 1: 2000.0] [Peer 2: 1500.0] ");
            peer3.placeAbid("Persiana", 3000);
            assertEquals(peer0.checkAuction("Persiana"), "status: open\nbids: [Peer 1: 2000.0] [Peer 2: 1500.0] [Peer 3: 3000.0] ");
            Thread.sleep(2000);
            assertEquals(peer0.checkAuction("Persiana"), "status: ended\nwinner: Peer 3\nprice: 2000.0\n");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkMultipleBidsNoWinner() {
        try {
            peer0.createAuction("Lavatrice", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 200, "Lavatrice Bosch");
            peer1.placeAbid("Lavatrice", 100);
            assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] ");
            peer2.placeAbid("Lavatrice", 500);
            assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] [Peer 2: 500.0] ");
            peer3.placeAbid("Lavatrice", 50);
            assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] [Peer 2: 500.0] [Peer 3: 50.0] ");
            Thread.sleep(4000);
            assertEquals(peer0.checkAuction("Lavatrice"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}