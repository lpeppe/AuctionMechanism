import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class AuctionMechanismImpl implements AuctionMechanism {

    final private PeerDHT dht;
    final private int DEFAULT_MASTER_PORT = 4000;
    final private int peerID;

    public AuctionMechanismImpl(final int peerId) throws Exception {
        dht = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(DEFAULT_MASTER_PORT + peerId).start()).start();
        FutureBootstrap fb = this.dht.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if (fb.isSuccess())
            dht.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        peerID = peerId;
    }

    public boolean createAuction(String _auction_name, Date _end_time, double _reserved_price, String _description) {
        try {
            if (checkAuction(_auction_name) == null) {
                Auction auction = new Auction(peerID, _auction_name, _end_time, _reserved_price, _description);
                dht.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String checkAuction(String _auction_name) {
        boolean isEnded;
        String bids = "";
        try {
            FutureGet futureGet = dht.get(Number160.createHash(_auction_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                isEnded = auction.getEndTime().before(new Date());
                if (isEnded) {
                    String data = findWinner(auction);
                    if(data != null) {
                        String[] data2 = data.split(" ");
                        return "status: ended\nwinner: Peer " + data2[0] + "\nprice: " + data2[1] + "\n";
                    }
                    return "status: ended\nwinner: none\n";
                }
                for(Map.Entry<Integer, Double> bid : auction.getBids().entrySet())
                    bids += "[Peer " + bid.getKey() + ": " + bid.getValue() + "] ";
                return "status: open\nbids: " + bids;
            }
        } catch (NoSuchElementException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String findWinner(Auction auction) {
        HashMap<Integer, Double> bids = auction.getBids();
        double secondMax = 0;
        Integer winner = null;
        if (bids.values().size() > 0) {
            double max = Collections.max(bids.values());
            for (Integer peerID : bids.keySet()) {
                double bid = bids.get(peerID);
                if (bid == max)
                    winner = peerID;
                else if (bid > secondMax)
                    secondMax = bid;
            }
            if (secondMax > auction.getReservedPrice())
                return winner + " " + secondMax;
        }
        return null;
    }


    public String placeAbid(String _auction_name, double _bid_amount) {
        try {
            FutureGet futureGet = dht.get(Number160.createHash(_auction_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                if(auction.getEndTime().before(new Date()))
                    return checkAuction(_auction_name);
                auction.getBids().put(peerID, _bid_amount);
                dht.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
            }
        } catch (Exception e) {
            return null;
        }
        return checkAuction(_auction_name);
    }

    public boolean leave() {
        try {
            dht.peer().announceShutdown().start().awaitUninterruptibly();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
