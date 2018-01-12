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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuctionMechanismImpl implements AuctionMechanism {

    final private PeerDHT peer;
    final private int DEFAULT_MASTER_PORT = 4000;

    public AuctionMechanismImpl(int peerId) throws Exception {
        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(DEFAULT_MASTER_PORT + peerId).start()).start();
        FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if (fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
    }

    public static void main(String[] args) throws Exception {
        AuctionMechanismImpl impl = new AuctionMechanismImpl(Integer.parseInt(args[0]));
        generateCLI(impl);
    }

    private static void generateCLI(AuctionMechanismImpl impl) throws InputMismatchException {
        Scanner scanner = new Scanner(System.in);
        String auctionName, endTime, description;
        Date endTimeDate;
        double reservedPrice;
        while (true) {
            try {
                System.out.println("1 - Vendi qualcosa");
                System.out.println("2 - Fai un'offerta");
                System.out.println("3 - Controlla asta");
                System.out.println("0 - Esci");
                int selection = Integer.parseInt(scanner.nextLine());
                if (selection == 0)
                    System.exit(0);
                switch (selection) {
                    case 1:
                        System.out.println("Inserisci il nome dell'asta");
                        auctionName = scanner.nextLine();
                        System.out.println("Inserisci l'end time");
                        endTime = scanner.nextLine();
                        endTimeDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(endTime);
                        System.out.println("Inserisci il prezzo di riserva");
                        reservedPrice = Double.parseDouble(scanner.nextLine());
                        System.out.println("Inserisci la descrizione");
                        description = scanner.nextLine();
                        if (impl.createAuction(auctionName, endTimeDate, reservedPrice, description))
                            System.out.println("Asta creata!");
                        else
                            System.out.println("Errore nella creazione dell'asta!");
                        break;
                    case 2:
                        System.out.println("Inserisci il nome dell'asta");
                        auctionName = scanner.nextLine();
                        System.out.println("Inserisci l'offerta");
                        impl.placeAbid(auctionName, Double.parseDouble(scanner.nextLine()));
                        break;
                    case 3:
                        System.out.println("Inserisci il nome dell'asta");
                        auctionName = scanner.nextLine();
                        System.out.println(impl.checkAuction(auctionName));
                        break;
                }
            } catch (InputMismatchException err) {
                System.out.println("Input non valido!");
                //scanner.next();
            } catch (ParseException e) {
                System.out.println("Input non valido!");
                //scanner.next();
            }
        }
    }

    public boolean createAuction(String _auction_name, Date _end_time, double _reserved_price, String _description) {
        try {
            if (checkAuction(_auction_name) == null) {
                peer.put(Number160.createHash(_auction_name)).data(new Data(new Auction(peer.peer().peerAddress(), _auction_name, _end_time, _reserved_price, _description))).start().awaitUninterruptibly();
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
                for(double bid : auction.getBids().values())
                    bids += bid + "\n";
                System.out.println(auction.getBids().values().size());
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "lmao";
    }
}
