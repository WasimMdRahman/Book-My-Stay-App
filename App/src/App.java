public class App {

    // RoomInventory Class (Core of UC1)
import java.util.HashMap;
import java.util.Map;

    class RoomInventory {

        // Centralized Storage
        private Map<String, Integer> inventory;

        // Constructor (Initialize Inventory)
        public RoomInventory() {
            inventory = new HashMap<>();
        }

        // Register Room Type
        public void addRoomType(String roomType, int count) {
            inventory.put(roomType, count);
        }

        // Get Availability
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        // Update Availability (Controlled)
        public void updateAvailability(String roomType, int newCount) {
            if (inventory.containsKey(roomType)) {
                inventory.put(roomType, newCount);
            } else {
                System.out.println("Room type not found!");
            }
        }

        // Display Inventory
        public void displayInventory() {
            System.out.println("=== ROOM INVENTORY ===");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println("Room Type: " + entry.getKey() +
                        " | Available: " + entry.getValue());
            }
        }
    }

}
