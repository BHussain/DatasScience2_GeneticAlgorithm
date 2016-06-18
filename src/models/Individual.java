package models;

public class Individual {
    private double fitness;
    private int[] binaryInt;
    private double probability;
    private double cumalativeProbability;

    public Individual(int[] binaryInt) {
        this.binaryInt = binaryInt;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int[] getBinaryInt() {
        return binaryInt;
    }

    public void setBinaryInt(int[] binaryInt) {
        this.binaryInt = binaryInt;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getCumulativeProbability() {
        return cumalativeProbability;
    }

    public void setCumulativeProbability(double cumalativeProbability) {
        this.cumalativeProbability = cumalativeProbability;
    }

    public String toString() {
        String output = "Binary number: ";
        for (int value : binaryInt) {
            output += value;
        }
        return output + ", fitness: " + this.fitness + ", probability: " + this.getProbability()
                + ", cumulative probability: " + this.cumalativeProbability;
    }
}
