import java.io.Serializable;

public class LiftPass implements Serializable {
    private String passName;
    private double passCost;

    public LiftPass(String passName, double passCost) {
        this.passName = passName;
        this.passCost = passCost;
    }

    public String getPassName() {
        return passName;
    }

    public double getPassCost() {
        return passCost;
    }

    @Override
    public String toString() {
        return passName + " - $" + passCost;
    }
}