import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class AuctionMechanismImpl implements AuctionMechanism {

    final private PeerDHT peer;
    final private int DEFAULT_MASTER_PORT = 4000;
    final private int id;
    private ScheduledTask st;
    private ArrayList<String> myAuctions;

    public AuctionMechanismImpl(final int peerId) throws Exception {
        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(DEFAULT_MASTER_PORT + peerId).start()).start();
        id = peerId;
        FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if (fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }

        peer.peer().objectDataReply(new ObjectDataReply() {
            public Object reply(PeerAddress sender, Object request) throws Exception {
                System.err.println("[Peer " + peerId + "] " + request);
                return null;
            }
        });
        myAuctions = new ArrayList<String>();
        Timer time = new Timer();
        st = new ScheduledTask();
        time.schedule(st, 0, 10000);
    }

    public boolean createAuction(String _auction_name, Date _end_time, double _reserved_price, String _description) {
        try {
            if (checkAuction(_auction_name) == null) {
                Auction auction = new Auction(peer.peer().peerAddress(), _auction_name, _end_time, _reserved_price, _description);
                peer.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
                synchronized (myAuctions) {
                    myAuctions.add(_auction_name);
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String checkAuction(String _auction_name) {
        try {
            FutureGet futureGet = peer.get(Number160.createHash(_auction_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                String bids = "";
                for (double bid : auction.getBids().values())
                    bids += bid + "\n";
                return "Descrizione: " + auction.getDescription() + "\nOfferte: " + bids;
            }
        } catch (NoSuchElementException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String placeAbid(String _auction_name, double _bid_amount) {
        try {
            FutureGet futureGet = peer.get(Number160.createHash(_auction_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                auction.getBids().put(peer.peer().peerAddress(), _bid_amount);
                peer.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
                peer.peer().sendDirect(auction.getAuthor()).object(_auction_name + " offerta: " + _bid_amount).start().awaitUninterruptibly();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "lmao";
    }

    public class ScheduledTask extends TimerTask {
        List<String> removedItems = new ArrayList<String>();

        public void run() {
            synchronized (myAuctions) {
                try {
                    for (String auctionName : myAuctions) {
                        FutureGet futureGet = peer.get(Number160.createHash(auctionName)).start();
                        futureGet.awaitUninterruptibly();
                        if (futureGet.isSuccess()) {
                            Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                            if (auction.getEndTime().before(new Date())) {
                                HashMap<PeerAddress, Double> bids = auction.getBids();
                                double secondMax = 0;
                                PeerAddress winner = null;
                                if (bids.values().size() > 0) {
                                    double max = Collections.max(bids.values());
                                    for (PeerAddress addr : bids.keySet()) {
                                        double bid = bids.get(addr);
                                        if (bid == max)
                                            winner = addr;
                                        else if (bid > secondMax)
                                            secondMax = bid;
                                    }
                                    removedItems.add(auctionName);
                                    peer.remove(Number160.createHash(auctionName)).start().awaitUninterruptibly();
                                    if (secondMax > auction.getReservedPrice()) {
                                        peer.peer().sendDirect(winner).object("Hai vinto l'asta " + auction.getName() + " e devi pagare " + (secondMax == 0 ? max : secondMax)).start().awaitUninterruptibly();
                                        System.out.println("[Peer " + id + "] L'asta " + auction.getName() + " è stata chiusa. Hai guadagnato " + (secondMax == 0 ? max : secondMax));
                                    } else {
                                        System.out.println("[Peer " + id + "] L'asta " + auction.getName() + " è stata chiusa. Non è stato raggiunto il prezzo minimo");
                                    }
                                } else {
                                    removedItems.add(auctionName);
                                    peer.remove(Number160.createHash(auctionName)).start().awaitUninterruptibly();
                                    System.out.println("[Peer " + id + "] L'asta " + auction.getName() + " è stata chiusa. Non ci sono state offerte");
                                }
                            }
                        }
                    }
                    myAuctions.removeAll(removedItems);
                    removedItems.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
