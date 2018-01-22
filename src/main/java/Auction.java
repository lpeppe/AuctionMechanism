import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Auction implements Serializable{
    private int author;
    private String name;
    private Date endTime;
    private double reservedPrice;
    private String description;
    private HashMap<Integer, Double> bids;

    public int getAuthor() {
        return author;
    }

    public Auction(int author, String name, Date endTime, double reservedPrice, String description) {
        this.author = author;
        this.name = name;
        this.endTime = endTime;
        this.reservedPrice = reservedPrice;
        this.description = description;
        this.bids = new HashMap<Integer, Double>();
    }

    public void setBids(HashMap<Integer, Double> bids) {
        this.bids = bids;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<Integer, Double> getBids() {
        return bids;
    }

    public Date getEndTime() {

        return endTime;
    }

    public double getReservedPrice() {
        return reservedPrice;
    }
}
