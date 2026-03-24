import java.util.Map;

class SearchService {

    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Read-only search
    public void searchAvailableRooms(Room[] rooms) {

        System.out.println("=== AVAILABLE ROOMS ===");

        Map<String, Integer> data = inventory.getAllInventory();

        for (Room room : rooms) {

            int available = data.getOrDefault(room.getRoomType(), 0);

            // Defensive Programming: filter unavailable
            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available);
                System.out.println("----------------------");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {

        // Inventory Setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 0); // Not available
        inventory.addRoomType("Suite Room", 2);

        // Room Objects (Domain)
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // Search Service
        SearchService searchService = new SearchService(inventory);

        // Guest triggers search
        searchService.searchAvailableRooms(rooms);
    }
}