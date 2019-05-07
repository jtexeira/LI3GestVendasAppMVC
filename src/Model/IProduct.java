public interface IProduct {
    boolean verifyProduct();

    String getId();

    IProduct clone();
}
