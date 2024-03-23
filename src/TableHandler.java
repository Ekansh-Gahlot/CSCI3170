public class TableHandler {
    public static BaseTableHandler bookTableHandler = new BaseTableHandler("book",
            new String[] { "ISBN", "title", "unit_price", "no_of_copies" }, new String[] { "ISBN" });
    public static BaseTableHandler customerTableHandler = new BaseTableHandler("customer",
            new String[] { "customer_id", "name", "shipping_address", "credit_card_no" },
            new String[] { "customer_id" });
    public static BaseTableHandler orderTableHandler = new BaseTableHandler("orders",
            new String[] { "order_id", "o_date", "shipping_status", "charge", "customer_id" },
            new String[] { "order_id" });
    public static BaseTableHandler orderingTableHandler = new BaseTableHandler("ordering",
            new String[] { "order_id", "ISBN", "quantity" }, new String[] { "order_id", "ISBN" });
    public static BaseTableHandler bookAuthorTableHandler = new BaseTableHandler("book_author",
            new String[] { "ISBN", "author_name" }, new String[] { "ISBN", "author_name" });
}
