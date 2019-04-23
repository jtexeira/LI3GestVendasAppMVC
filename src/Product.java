public class Product implements IProduct{
    private String id;

    public Product(String id) {
        this.id = id;
    }

    public boolean verifyProduct() {
        return this.id.matches("[A-Z]{2}\\d{4}");
    }
}
