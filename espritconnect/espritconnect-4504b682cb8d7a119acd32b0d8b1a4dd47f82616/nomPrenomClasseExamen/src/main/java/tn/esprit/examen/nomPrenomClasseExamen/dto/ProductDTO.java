package tn.esprit.examen.nomPrenomClasseExamen.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO {

    private Long idProduct;
    private String name;
    private String category;
    private BigDecimal askingPrice;
    private String description;
    private LocalDateTime deadline;
    private boolean itemState;
    private String sellerName;
    private String buyerName;
    private String image;

    // Constructor
    public ProductDTO(Long idProduct, String name, String category, BigDecimal askingPrice, String description,
                      LocalDateTime deadline, boolean itemState, String sellerName, String buyerName, String image) {
        this.idProduct = idProduct;
        this.name = name;
        this.category = category;
        this.askingPrice = askingPrice;
        this.description = description;
        this.deadline = deadline;
        this.itemState = itemState;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.image = image;
    }

    // Getters and Setters
    public Long getIdProduct() { return idProduct; }
    public void setIdProduct(Long idProduct) { this.idProduct = idProduct; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAskingPrice() { return askingPrice; }
    public void setAskingPrice(BigDecimal askingPrice) { this.askingPrice = askingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public boolean isItemState() { return itemState; }
    public void setItemState(boolean itemState) { this.itemState = itemState; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
