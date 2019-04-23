public class Client implements IClient{
    private String id;

    public Client(String id) {
        this.id = id;
    }

    public boolean verifyClient() {
        return this.id.matches("[A-Z]([0-4]\\d{3}|50{3})");
    }
}
