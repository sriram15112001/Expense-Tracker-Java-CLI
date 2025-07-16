import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTrackerApplication {
    private static Map<Integer, Product> products = new HashMap<>();
    private int size;

    public int addProduct(String description, int amount, LocalDate date) {
        Product product = new Product(++size, date == null ? LocalDate.now() : date, description, amount);
        products.put(size, product);
        return size;
    }

    public void listProducts() {
        products.forEach((key, val) -> System.out.println(val));
    }

    public int summary(Integer month) {
        List<Product> summaryProducts;
        if (month == null) {
            summaryProducts = (List<Product>) products.values();
        } else {
            summaryProducts = products.values().stream()
                    .filter(product -> product.getDate().getMonth().getValue() == month)
                    .toList();
        }
        int tot = 0;
        for (Product p : summaryProducts) {
            tot += p.getAmount();
        }
        return tot;
    }

    public boolean delete(int id) {
        if (products.containsKey(id)) {
            products.remove(id);
            return true;
        }
        return false;
    }

    public void saveProducts() throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter("products.txt");
        products.forEach((key, val) -> {
            printWriter.println(val.getId() + "-_" + val.getDate() + "-_" + val.getDescription() + "-_" + val.getAmount());
        });
        printWriter.close();
    }

    public void loadProducts() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("products.txt"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] words = line.split("-_");
            int id = Integer.parseInt(words[0]);
            LocalDate date = LocalDate.parse(words[1]);
            String description = words[2];
            int amount = Integer.parseInt(words[3]);
            addProduct(description, amount, date);
        }
        bufferedReader.close();

    }

    public static void main(String[] args) throws IOException {
        ExpenseTrackerApplication app = new ExpenseTrackerApplication();
        app.loadProducts();
        switch (args[0]) {
            case "add":
                String description = null;
                int amount = 0;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equalsIgnoreCase("--description")) {
                        description = args[i + 1];
                    }
                    if (args[i].equalsIgnoreCase("--amount")) {
                        amount = Integer.parseInt(args[i + 1]);
                    }
                }
                int id = app.addProduct(description, amount, null);
                System.out.println("Expense added successfully (ID: " + id + ")");
                break;

            case "list":
                app.listProducts();
                break;
            case "summary":
                int tot;
                if (args.length == 1) {
                    tot = app.summary(null);
                } else {
                    int month = -1;
                    for (int i = 1; i < args.length; i++) {
                        if (args[i].equalsIgnoreCase("--month")) {
                            month = Integer.parseInt(args[i + 1]);
                        }
                    }
                    tot = app.summary(month);
                }
                System.out.println("Total expenses: $" + tot);
                break;
            case "delete":
                int deleteId = -1;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equalsIgnoreCase("--id")) {
                        deleteId = Integer.parseInt(args[i + 1]);
                    }
                }
                boolean deleteSuccess = app.delete(deleteId);
                if (deleteSuccess) {
                    System.out.println("Expense deleted successfully");
                } else {
                    System.out.println("Product id not available");
                }

        }
        app.saveProducts();

    }
}
