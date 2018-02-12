# Auction Mechanism
Ogni peer può vendere e acquistare beni utilizzando un meccanismo di Second-Price Auction.
Ogni partecipante all'asta fa un'offerta, il migliore offerente vince l'asta e paga il prezzo della seconda offerta più alta.
Il sistema permette agli utenti di:
1. Creare nuove aste
2. Verificare lo stato di un'asta
3. Effettuare una nuova offerta per un'asta
---
## Membri del gruppo
* Maria Elena Cammarano: **Leader**
* Luca Peppe
---
## Implementazione
### Classe Auction
La classe Auction è costituita dalle seguenti variabili di istanza:
* autore
* nome
* tempo di fine
* prezzo di vendita minimo
* descrizione
* lista di offerte

L'implementazione è riportata di seguito:
```
public class Auction implements Serializable{
    private int author;
    private String name;
    private Date endTime;
    private double reservedPrice;
    private String description;
    private HashMap<Integer, Double> bids;

    public Auction(int author, String name, Date endTime, double reservedPrice, String description) {
        this.author = author;
        this.name = name;
        this.endTime = endTime;
        this.reservedPrice = reservedPrice;
        this.description = description;
        this.bids = new HashMap<Integer, Double>();
    }

    public int getAuthor() {
        return author;
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

```

---
### Interfaccia AuctionMechanism
L'interfaccia AuctionMechanism è costituita dai seguenti metodi:
1. **createAuction**: per creare un'asta
2. **checkAuction**: per verificare lo stato dell'asta
3. **placeAbid**: per fare un'offerta

#### Metodo createAuction ####
Il metodo ha come parametri:
 * nome dell'asta
 * tempo di fine dell'asta
 * prezzo di vendita minimo
 * descrizione

Restituisce TRUE per indicare che l'asta è stata creata, FALSE altrimenti.

###### Passaggi ######
1. Si controlla che non siano già presenti aste con lo stesso nome
2. Si crea una nuova asta andando a specificarne autore, nome, tempo di fine, prezzo di vendita minimo, descrizione
3. Si inserisce l'asta nella DHT

###### Implementazione ######
```
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
```


#### Metodo checkAuction ####
Il metodo ha come parametro il nome dell'asta.

Restituisce una stringa contenente le informazioni relative allo stato dell'asta.

###### Passaggi checkAuction ######
1. Si prendono i dati dalla DHT relativi all'asta di interesse
2. Se ha successo il passo 1, si controlla che l'asta non sia scaduta
3. Se l'asta è scaduta, si elegge il vincitore
4. Se l'asta non è scaduta, vengono restituite in output le offerte relative

###### Implementazione ######
```
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
                    if (data != null) {
                        String[] data2 = data.split(" ");
                        return "status: ended\nwinner: Peer " + data2[0] + "\nprice: " + data2[1] + "\n";
                    }
                    return "status: ended\nwinner: none\n";
                }
                for (Map.Entry<Integer, Double> bid : auction.getBids().entrySet())
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
```

###### Passaggi findWinner ######
1. Si prendono le offerte relative all'asta
2. Si controlla che ci siano delle offerte
3. Si calcola l'offerta massima
4. Si salva l'id relativo al peer che ha fatto l'offerta più alta
5. Si salva la seconda offerta più alta
6. Si controlla che la seconda offerta più alta sia maggiore del prezzo minimo e in tal caso si restituiscono in output il winner e la seconda offerta più alta


```
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
```


#### Metodo placeAbid ####
Il metodo ha come parametro:
* nome dell'asta
* offerta

Restituisce una stringa indicante lo stato dell'asta

###### Passaggi ######
1. Si prende l'asta dalla DHT
2. Se ha successo il passo 1, si controlla che l'asta non sia terminata
3. Se l'asta non è terminata, si aggiunge la nuova offerta a quelle già presenti
4. Si aggiornano i dati nella DHT


###### Implementazione ######
```
public String placeAbid(String _auction_name, double _bid_amount) {
        try {
            FutureGet futureGet = dht.get(Number160.createHash(_auction_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Auction auction = ((Auction) futureGet.dataMap().values().iterator().next().object());
                Date date = new Date(System.currentTimeMillis());
                if (auction.getEndTime().before(date))
                    return checkAuction(_auction_name);
                auction.getBids().put(peerID, _bid_amount);
                dht.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
            }
        } catch (Exception e) {
            return null;
        }
        return checkAuction(_auction_name);
    }
```

---
## Testing
I casi di test analizzati sono i seguenti:
1. Due aste con lo stesso nome
2. Asta senza offerte
3. Asta con un'unica offerta
4. Offerte dopo chiusura dell'asta
5. Più offerte da parte di uno stesso peer
6. Leave di un peer durante l'asta
7. Più offerte per una stessa asta (3 offerte al di sopra del prezzo minimo richiesto)
8. Più offerte per una stessa asta e nessun vincitore (2 delle 3 offerte al di sotto del prezzo minimo richiesto)


#### 1. createDuplicateAuction() ####
```
void createDuplicateAuction() {
        try {
            assertTrue(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
            assertFalse(peer0.createAuction("TV", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 2. checkAuctionNoBids() ####
```
void checkAuctionNoBids() {
        try {
            peer0.createAuction("TV2", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 1000, "LG OLED 4K");
            assertEquals(peer0.checkAuction("TV2"), "status: open\nbids: ");
            Thread.sleep(1500);
            assertEquals(peer0.checkAuction("TV2"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 3. placeAbid() ####
```
void placeAbid() {
        try {
            peer0.createAuction("Auto", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 10000, "Alfa Giulia Quadrifoglio");
            assertEquals(peer1.placeAbid("Auto", 200000), "status: open\nbids: [Peer 1: 200000.0] ");
            Thread.sleep(1500);
            assertEquals(peer0.checkAuction("Auto"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 4. closedAuction() ####
```
void closedAuction() {
        try {
            peer0.createAuction("PC", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 3000, "Macbook Pro");
            Thread.sleep(1500);
            assertEquals(peer1.placeAbid("PC", 4000), "status: ended\nwinner: none\n");
            assertEquals(peer1.placeAbid("PC", 40), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 5. multipleBidsSamePeer() ####
```
void multipleBidsSamePeer() {
        try {
            peer0.createAuction("Borsa", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 300, "Chanel");
            peer1.placeAbid("Borsa", 1200);
            peer1.placeAbid("Borsa", 1300);
            assertEquals(peer0.checkAuction("Borsa"), "status: open\nbids: [Peer 1: 1300.0] ");
            Thread.sleep(1500);
            assertEquals(peer0.checkAuction("Borsa"), "status: ended\nwinner: none\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 6. leaveAuction() ####
```
void leaveAuction() {
        try {
            peer0.createAuction("Forno", new Date(Calendar.getInstance().getTimeInMillis() + 2000), 3000, "Microonde");
            peer1.placeAbid("Forno", 12000);
            assertEquals(peer1.checkAuction("Forno"), "status: open\nbids: [Peer 1: 12000.0] ");
            peer2.placeAbid("Forno", 130000);
            assertEquals(peer0.checkAuction("Forno"), "status: open\nbids: [Peer 1: 12000.0] [Peer 2: 130000.0] ");
            assertTrue(peer2.leave());
            Thread.sleep(2500);
            assertEquals(peer0.checkAuction("Forno"), "status: ended\nwinner: Peer 2\nprice: 12000.0\n");
            peer2 = new AuctionMechanismImpl(2);
            assertEquals(peer2.checkAuction("Forno"), "status: ended\nwinner: Peer 2\nprice: 12000.0\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
```

#### 7. checkMultipleBids() ####
```
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
```

#### 8. checkMultipleBidsNoWinner() ####
```
  void checkMultipleBidsNoWinner() {
      try {
          peer0.createAuction("Lavatrice", new Date(Calendar.getInstance().getTimeInMillis() + 1000), 200, "Lavatrice Bosch");
          peer1.placeAbid("Lavatrice", 100);
          assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] ");
          peer2.placeAbid("Lavatrice", 500);
          assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] [Peer 2: 500.0] ");
          peer3.placeAbid("Lavatrice", 50);
          assertEquals(peer0.checkAuction("Lavatrice"), "status: open\nbids: [Peer 1: 100.0] [Peer 2: 500.0] [Peer 3: 50.0] ");
          Thread.sleep(1500);
          assertEquals(peer0.checkAuction("Lavatrice"), "status: ended\nwinner: none\n");
      }
      catch(Exception e) {
          e.printStackTrace();
      }
  }
```
