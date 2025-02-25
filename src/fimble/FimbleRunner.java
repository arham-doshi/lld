package fimble;

import java.util.*;

class User{
    String userId;
    String name;
    int age;
    String gender;
    Set<String> interests;

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", interests=" + interests +
                ", acceptedProfile=" + acceptedProfile +
                ", rejectedProfile=" + rejectedProfile +
                ", matchedProfile=" + matchedProfile +
                ", preference=" + preference +
                '}';
    }

    Set<String> acceptedProfile;
    Set<String> rejectedProfile;
    Set<String> matchedProfile;
    PartnerPreference preference;


    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public Set<String> getInterests() {
        return interests;
    }

    public Set<String> getAcceptedProfile() {
        return acceptedProfile;
    }

    public Set<String> getRejectedProfile() {
        return rejectedProfile;
    }

    public Set<String> getMatchedProfile() {
        return matchedProfile;
    }

    public PartnerPreference getPreference() {
        return preference;
    }

    public User(String userId, String name, int age, String gender) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.interests = new HashSet<>();
        this.acceptedProfile = new HashSet<>();
        this.rejectedProfile = new HashSet<>();
        this.preference = new PartnerPreference();
        this.matchedProfile = new HashSet<>();
    }

    public void setPreference(PartnerPreference preference) {
        this.preference = preference;
    }

    public void addInterest(String interest){
        this.interests.add(interest);
    }
    public void acceptProfile(String acceptedUserId){
        this.acceptedProfile.add(acceptedUserId);
    }
    public void rejectedProfile(String rejectedUserId){
        this.rejectedProfile.add(rejectedUserId);
    }
    public void matchProfile(String matchedUserId){
        this.matchedProfile.add(matchedUserId);
    }
}



class PartnerPreference{
    int minAge = 18;

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getGender() {
        return gender;
    }

    int maxAge = 50;
    String gender = "any";

    public PartnerPreference() {
    }

    public PartnerPreference(int minAge, int maxAge, String gender) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
    }

}

class Fimble{
    Map<String, User> users;

    public Fimble() {
        this.users = new HashMap<>();
    }

    public void createProfile(String userId, String name, int age, String gender) throws Exception {
        if(users.containsKey(userId)){
            throw new Exception("User already present");
        }
        User user = new User(userId, name, age, gender);
        users.put(userId, user);
    }
    public void addInterest(String userId, String interest) throws Exception {
        if(!users.containsKey(userId)){
            throw new Exception("User not present");
        }
        users.get(userId).addInterest(interest);
    }
    public void setPartnerPreference(String userId, int minAge, int maxAge, String gender) throws Exception {
        if(!users.containsKey(userId)){
            throw new Exception("User not present");
        }
        PartnerPreference preference = new PartnerPreference(minAge, maxAge, gender);
        users.get(userId).setPreference(preference);
    }
    public void acceptProfile(String userId, String targetId) throws Exception {
        if(!users.containsKey(userId)) {
            throw new Exception("User not present");
        }
        if(!users.containsKey(targetId)) {
            throw new Exception("Target user not present");
        }
        users.get(userId).acceptProfile(targetId);
        if(users.get(targetId).getAcceptedProfile().contains(userId)){
            users.get(userId).matchProfile(targetId);
            users.get(targetId).matchProfile(userId);
        }
    }

    public void rejectProfile(String userId, String targetId) throws Exception {
        if(!users.containsKey(userId)) {
            throw new Exception("User not present");
        }
        if(!users.containsKey(targetId)) {
            throw new Exception("Target user not present");
        }
        users.get(userId).rejectedProfile(targetId);
    }
    public ArrayList<String> listMatchedProfiles(String userId) throws Exception {
        if(!users.containsKey(userId)) {
            throw new Exception("User not present");
        }
        return new ArrayList<String>(users.get(userId).getMatchedProfile());
    }
    public User getBestProfile(String userId) throws Exception {
        if(!users.containsKey(userId)) {
            throw new Exception("User not present");
        }
        User user = users.get(userId);
        System.out.println("user get best profile : "+ user.toString());
        User bestProfile = users.values().stream().peek(candidate -> {
            System.out.println(candidate.getName());
        }).filter(candidate -> (!Objects.equals(candidate.getUserId(), userId)
                 && candidate.getAge() >= user.getPreference().getMinAge()
                 && candidate.getAge() <= user.getPreference().getMaxAge()
                 && (Objects.equals(candidate.getGender(), user.getPreference().getGender()) || user.getPreference().getGender().equals("any"))
                 && !user.getAcceptedProfile().contains(candidate.getUserId())
                 && !user.getRejectedProfile().contains(candidate.getUserId())
                 && !candidate.getRejectedProfile().contains(userId)
        )).max( Comparator.comparingInt(candidate -> this.getMutuals(user, candidate))).orElse(null);
        System.out.println(bestProfile);
        return bestProfile;
    }
    private int getMutuals(User user, User target){
        System.out.println("get mutuals : "+ user.getName()+target.getName());
        Set<String> interests = target.getInterests();
        interests.retainAll(user.getInterests());
        return interests.size();
    }



}

public class FimbleRunner {
    public static void main() throws Exception {
        Fimble fimble = new Fimble();
        fimble.createProfile("user1", "user1", 20, "male");
        fimble.createProfile("user2", "user2", 30, "female");
        fimble.createProfile("user3", "user3", 24, "male");
        fimble.createProfile("user4", "user4", 29, "female");
        fimble.createProfile("user5", "user5", 35, "female");
        fimble.createProfile("user6", "user6", 32, "male");
        fimble.createProfile("user7", "user7", 28, "male");
        fimble.createProfile("user8", "user8", 24, "female");

        fimble.setPartnerPreference("user1", 18, 25, "female");
        fimble.setPartnerPreference("user2", 26, 35, "male");
        fimble.setPartnerPreference("user3", 24, 40, "female");
        fimble.setPartnerPreference("user4", 27, 35, "male");
        fimble.setPartnerPreference("user5", 20, 30, "male");

        fimble.addInterest("user1", "pets");
        fimble.addInterest("user1", "coffee");

        fimble.addInterest("user2", "pets");

        fimble.addInterest("user3", "gym");

        fimble.addInterest("user4", "gym");

        fimble.addInterest("user5", "gym");

        fimble.addInterest("user6", "coffe");

        fimble.addInterest("user7", "pets");

        System.out.println("best profile for user1 : "+ fimble.getBestProfile("user1").toString());
        System.out.println("best profile for user2 : "+fimble.getBestProfile("user2").toString());
    }
}
