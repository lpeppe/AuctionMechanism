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
            //AuctionMechanismImpl peer3 = new AuctionMechanismImpl(3);
            peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K");
            peer1.placeAbid("TV", 1200);
            peer2.placeAbid("TV", 2000);
            peer0.checkAuction("TV");
            Thread.sleep(TEN_SECONDS + 10000);
            System.out.println(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + TEN_SECONDS), 1000, "LG OLED 4K"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
