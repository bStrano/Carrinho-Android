package br.com.stralom.entities;

/**
 * Created by Bruno Strano on 24/01/2018.
 */


public class ItemStock extends Item {
    private Stock stock;
    private int stockPercentage;
    private Status status;
    private int actualAmount;


    public ItemStock() {

    }

    public enum Status{
        EMPTY,BAD,NEUTRAL,GOOD,FULL
    }

    public ItemStock(Long id, int amount, double total, Product product, int stockPercentage, Status status, int actualAmount, Stock stock) {
        super(id, amount, total, product);
        this.stockPercentage = stockPercentage;
        this.status = status;
        this.actualAmount = actualAmount;
        this.stock = stock;
    }

    public ItemStock(int amount, Product product, int actualAmount) {
        super(amount, product);
        this.actualAmount = actualAmount;
        setStockPercentage(amount, actualAmount);
        setStatus();
    }



    /**
     * This method cheks the value of the stockPercentage attribute and updates the status with the correspondent value
     */
    public void setStatus(){
        if(stockPercentage == 0){
            this.status = Status.EMPTY;
        } else if (stockPercentage < 30) {
            this.status = Status.BAD;
        } else if (stockPercentage < 65){
            this.status = Status.NEUTRAL;
        } else if(stockPercentage <= 99) {
            this.status = Status.GOOD;
        } else if(stockPercentage == 100){
            this.status = Status.FULL;
        }

    }

    private void setStockPercentage(int amount, int actualAmount) {
        this.stockPercentage = ((100 * actualAmount)/amount);
    }

    public void setAmounts( int actualAmount, int maxAmount){
        this.actualAmount = actualAmount;
        this.amount = maxAmount;
        setStockPercentage((int) this.amount,this.actualAmount);
        setStatus();
    }


    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(int atualAmount) {
        this.actualAmount = atualAmount;
    }

    public int getStockPercentage() {
        return stockPercentage;
    }

    public void setStockPercentage(int stockPercentage) {
        this.stockPercentage = stockPercentage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ItemStock{" +
                "stock=" + stock +
                ", stockPercentage=" + stockPercentage +
                ", status=" + status +
                ", atualAmount=" + actualAmount +
                '}';
    }
}
