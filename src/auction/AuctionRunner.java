package auction;

import jdk.nashorn.api.tree.Tree;

import java.util.*;

class User{
    public String id;

    public User(String id) {
        this.id = id;
    }
}
class Seller{
    public Seller(String id) {
        this.id = id;
    }

    public String id;
}
class Auction{
    public String id;
    public int highestBid;
    public int lowestBid;
    public int entryFee;
    public String company;
    public boolean isOpen;
    public Map<String, Integer> bids;
    public String winner;
    public int winAmount;
    public Set<String> usersParticipated;
    public Auction(String id, int highestBid, int lowestBid, int entryFee, String company) {
        this.id = id;
        this.highestBid = highestBid;
        this.lowestBid = lowestBid;
        this.entryFee = entryFee;
        this.company = company;
        this.isOpen = true;

        bids = new HashMap<>();
        usersParticipated = new HashSet<>();
    }
    public void addBid(String userId, int amount) throws Exception {
        if(amount < this.lowestBid || amount > this.highestBid){
            throw new Exception("Amount of current bid is not in between highest and lowest allowed bid");
        }
        bids.put(userId, amount);
        usersParticipated.add(userId);
    }

    public void withdrawBid(String userId){
        this.bids.remove(userId);
    }

    public void closeAuction(){
        if(!this.isOpen) {
            throw new IllegalStateException("Auction Already closed");
        }
        this.isOpen = false;

    }


    public String getId() {
        return id;
    }

    public int getHighestBid() {
        return highestBid;
    }

    public int getLowestBid() {
        return lowestBid;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public String getCompany() {
        return company;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Map<String, Integer> getBids() {
        return bids;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(int winAmount) {
        this.winAmount = winAmount;
    }

    public Set<String> getUsersParticipated() {
        return usersParticipated;
    }
}

class FooBar {
    public Map<String, User> users;
    public Map<String, Auction> auctions;
    public Map<String, Seller> sellers;

    public FooBar() {
        users = new HashMap<>();
        auctions = new HashMap<>();
        sellers = new HashMap<>();
    }
    public void addUser(String userId){
        if(!users.containsKey(userId)){
            User user = new User(userId);
            users.put(userId, user);
        }
    }
    public void addSeller(String sellerId){
        if(!sellers.containsKey(sellerId)){
            Seller seller = new Seller(sellerId);
            sellers.put(sellerId, seller);
        }

    }
    public void addAuction(String id, int minBid, int maxBid, int entry, String company) throws Exception {
        if(auctions.containsKey(id)){
            throw new Exception("Auction already started");
        }
        if(!sellers.containsKey(company)){
            throw new Exception("Seller not registered");
        }
        Auction auction = new Auction(id, maxBid, minBid, entry, company);
        auctions.put(id, auction);
    }
    public void addBid(String userId, String auctionId, int amount) throws Exception {
        if(!auctions.containsKey(auctionId)){
            throw new Exception("No Auction found");
        }
        if(!users.containsKey(userId)){
            throw new Exception("User not registered");
        }
        Auction auction = auctions.get(auctionId);

        auction.addBid(userId, amount);
    }
    public void withdrawBid(String userId, String auctionId) throws Exception {
        if(!auctions.containsKey(auctionId)){
            throw new Exception("No Auction found");
        }
        Auction auction = auctions.get(auctionId);
        auction.withdrawBid(userId);
    }
    public String closeAuction(String auctionId) throws Exception {
        if(!auctions.containsKey(auctionId)){
            throw new Exception("No Auction found");
        }
        Auction auction = auctions.get(auctionId);
        auction.closeAuction();
        TreeMap<Integer, ArrayList<String>> bidUsers = new TreeMap<>();
        for(Map.Entry<String, Integer> entry : auction.bids.entrySet()){
            String userId = entry.getKey();
            int amount = entry.getValue();
            if(!bidUsers.containsKey(amount)){
                bidUsers.put(amount, new ArrayList<>());
            }
            bidUsers.get(amount).add(userId);
        }
        String winner = null;
        for(Integer amount : bidUsers.descendingKeySet()){
            if(bidUsers.get(amount).size() == 1) {
                winner =  bidUsers.get(amount).get(0);
                auction.setWinner(winner);
                auction.setWinAmount(amount);
                break;
            }
        }
        return winner;
    }
    public double getProfit(String seller, String auctionId) throws Exception {
        if(!sellers.containsKey(seller)){
            throw new Exception("Seller not present");
        }
        if(!auctions.containsKey(auctionId)){
            throw new Exception("No Auction found");
        }
        Auction auction = auctions.get(auctionId);
        if(auction.isOpen()){
            throw new IllegalStateException("Auction not colosed");
        }
        if(!auction.company.equals(seller)) {
            throw new Exception("Seller is not owner of auction");
        }
        double totalProfit = 0;
        int usersParticipated = auction.getUsersParticipated().size();
        totalProfit += usersParticipated * 0.2 * auction.getEntryFee();
        if(auction.winner != null){
            totalProfit -= (auction.getHighestBid() + auction.getLowestBid()) / 2.0;
            totalProfit += auction.winAmount;
        }

        return totalProfit;
    }

}

public class AuctionRunner {
    public static void main() throws Exception {
        /*
        Test case 1:
• ADD_BUYER("buyer1")
• ADD_BUYER("buyer2")
• ADD_BUYER("buyer3")
• ADD_SELLER("seller1")
• CREATE_AUCTION ("A1", "10", "50", "1", "seller1")
• CREATE_BID("buyer1", "A1", "17")
• CREATE_BID("buyer2", "A1", "15")
• UPDATE_BID("buyer2", "A1", "19")
• CREATE_BID("buyer3", "A1", "19")
• CLOSE_AUCTION("A1") // Should give Buyer1 as winner
• GET_PROFIT("seller1", "A1") // (17 + (3 * 0.2 * 1) - 30) = -12.4


Test case 2:
• ADD_SELLER("seller2")
• CREATE_AUCTION("A2", "5", "20", "2", "seller2")
• CREATE_BID("buyer3", "A2", 25) //This should fail as highest bid limit is 20 for A2
• CREATE_BID("buyer2, "A2", 5)
• WITHDRAW_BID("buyer2", "A2")
• CLOSE_AUCTION("A2") // No winner
• GET_PROFIT("seller2", "A2") // (1 * 0.2 * 2) = 0.4 only consider profit from participation cost
         */
        FooBar fooBar = new FooBar();
        fooBar.addUser("buyer1");
        fooBar.addUser("buyer2");
        fooBar.addUser("buyer3");
        fooBar.addSeller("seller1");
        fooBar.addAuction("A1", 10, 50, 1, "seller1");
        fooBar.addBid("buyer1", "A1", 17);
        fooBar.addBid("buyer2", "A1", 15);
        fooBar.addBid("buyer2", "A1", 19);
        fooBar.addBid("buyer3", "A1", 19);
        System.out.println(fooBar.closeAuction("A1"));
        System.out.println(fooBar.getProfit("seller1", "A1"));

        try{
            fooBar.addSeller("seller2");
            fooBar.addAuction("A2", 5, 20, 2, "seller2");
            fooBar.addBid("buyer2", "A2", 5);
            fooBar.withdrawBid("buyer2", "A2");
            System.out.println(fooBar.closeAuction("A2"));
            System.out.println(fooBar.getProfit("seller2", "A2"));
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }
}
