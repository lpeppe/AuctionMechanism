import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class TestAuctionMechanismImpl {

    static final long TEN_SECONDS = 10000;

    public static void main(String[] args) {
        try {
            AuctionMechanismImpl peer0 = new AuctionMechanismImpl(0);
            AuctionMechanismImpl peer1 = new AuctionMechanismImpl(1);
            AuctionMechanismImpl peer2 = new AuctionMechanismImpl(2);
            AuctionMechanismImpl peer3 = new AuctionMechanismImpl(3);
            peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K");
            peer0.createAuction("T2", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS + 10000), 1000, "LG OLED 4K");
            //peer2.placeAbid("TV", 2000);
            peer1.leave();
            peer1 = new AuctionMechanismImpl(1);
            System.out.println(peer1.placeAbid("TV", 1200));
            //peer0.leave();
            //System.out.println(peer1.placeAbid("T2", 1500));
            //peer2.placeAbid("T2", 700);
            //peer3.placeAbid("T2", 800);
            Thread.sleep(TEN_SECONDS + 2000);
            System.out.println(peer0.checkAuction("TV"));
            //System.out.println(peer0.checkAuction("TV"));
            //Thread.sleep(TEN_SECONDS + 10000);
            //System.out.println(peer0.createAuction("TV2", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
