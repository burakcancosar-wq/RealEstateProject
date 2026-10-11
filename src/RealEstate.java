import java.util.Locale;

public class RealEstate implements PropertyInterface {

    public enum Genre { FAMILYHOUSE, CONDOMINIUM, FARM }

    protected String city;
    protected double price;          // price per square meter
    protected int sqm;
    protected double numberOfRooms;
    protected Genre genre;

    public RealEstate() { }

    public RealEstate(String city, double price, int sqm, double numberOfRooms, Genre genre) {
        this.city = city;
        this.price = price;
        this.sqm = sqm;
        this.numberOfRooms = numberOfRooms;
        this.genre = genre;
    }

    public String getCity() { return city; }
    public double getPrice() { return price; }
    public int getSqm() { return sqm; }
    public double getNumberOfRooms() { return numberOfRooms; }
    public Genre getGenre() { return genre; }

    @Override
    public void makeDiscount(int percent) {
        price = price * (100 - percent) / 100.0;
    }

    @Override
    public int getTotalPrice() {
        double rate = 0;
        switch (city) {
            case "Budapest":    rate = 0.30; break;
            case "Debrecen":    rate = 0.20; break;
            case "Nyíregyháza": rate = 0.15; break;
        }
        return (int) Math.round(price * sqm * (1 + rate));
    }

    @Override
    public double averageSqmPerRoom() {
        return sqm / numberOfRooms;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%s | %s | Price per sqm: %.0f | Area: %d sqm | Rooms: %.0f | Total price: %d | Avg sqm per room: %.2f",
                genre, city, price, sqm, numberOfRooms, getTotalPrice(), averageSqmPerRoom());
    }
}
