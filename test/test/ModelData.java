package test;

/**
 *
 * @author RAVEN
 */
public class ModelData {

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public ModelData(String month, double amount, double cost, double profit) {
        this.month = month;
        this.amount = amount;
        this.cost = cost;
        this.profit = profit;
    }

    public ModelData() {
    }

    private String month;
    private double amount;
    private double cost;
    private double profit;
}
