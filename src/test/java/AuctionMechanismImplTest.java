import net.tomp2p.peers.Number160;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionMechanismImplTest {

    final long TEN_SECONDS = 10000;

    @Test
    void createAuction() {
        try {
            AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            assertTrue(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K"));
            assertFalse(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K"));

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkAuction() {
    }

    @Test
    void placeAbid() {
    }
}