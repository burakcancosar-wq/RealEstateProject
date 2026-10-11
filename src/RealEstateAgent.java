import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class RealEstateAgent {

    // Sorted by total price; toString breaks ties so equal-priced objects are not dropped
    public static TreeSet<RealEstate> estates = new TreeSet<>(
            Comparator.comparingInt(RealEstate::getTotalPrice)
                    .thenComparing(RealEstate::toString));

    static void load(String fileName) throws IOException {
        try (var br = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                String[] p = line.split("#");
                String city = p[1];
                double price = Double.parseDouble(p[2]);
                int sqm = Integer.parseInt(p[3]);
                double rooms = Double.parseDouble(p[4]);
                RealEstate.Genre genre = RealEstate.Genre.valueOf(p[5].trim().toUpperCase());

                if (p[0].equalsIgnoreCase("PANEL")) {
                    int floor = Integer.parseInt(p[6]);
                    boolean insulated = p[7].equalsIgnoreCase("yes");
                    estates.add(new Panel(city, price, sqm, rooms, genre, floor, insulated));
                } else {
                    estates.add(new RealEstate(city, price, sqm, rooms, genre));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            load("realestates.txt");
        } catch (IOException e) {
            System.out.println("Could not read file: " + e.getMessage());
            return;
        }

        StringBuilder out = new StringBuilder();

        // 1) Average price per square meter
        double avgSqmPrice = estates.stream().mapToDouble(RealEstate::getPrice).average().orElse(0);
        out.append(String.format(Locale.US, "Average price per square meter: %.2f%n", avgSqmPrice));

        // 2) Price of the cheapest property
        int cheapest = estates.stream().mapToInt(RealEstate::getTotalPrice).min().orElse(0);
        out.append("Price of the cheapest property: ").append(cheapest).append("\n");

        // 3) Average sqm per room of the most expensive property in Budapest
        estates.stream()
                .filter(e -> e.getCity().equals("Budapest"))
                .max(Comparator.comparingInt(RealEstate::getTotalPrice))
                .ifPresent(e -> out.append(String.format(Locale.US,
                        "Average sqm per room of the most expensive property in Budapest: %.2f%n",
                        e.averageSqmPerRoom())));

        // 4) Total price of the properties
        long total = estates.stream().mapToLong(RealEstate::getTotalPrice).sum();
        out.append("Total price of the properties: ").append(total).append("\n");

        // 5) Condominiums whose total price does not exceed the average price
        double avgTotal = (double) total / estates.size();
        out.append(String.format(Locale.US, "Average total price: %.2f%n", avgTotal));
        out.append("Condominiums not exceeding the average price:\n");
        List<RealEstate> list = estates.stream()
                .filter(e -> e.getGenre() == RealEstate.Genre.CONDOMINIUM)
                .filter(e -> e.getTotalPrice() <= avgTotal)
                .collect(Collectors.toList());
        for (RealEstate e : list) out.append("  ").append(e).append("\n");

        System.out.println(out);

        try {
            Files.writeString(Paths.get("outputRealEstate.txt"), out.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not write file: " + e.getMessage());
        }
    }
}
