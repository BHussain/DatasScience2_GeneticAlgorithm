package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import models.Individual;

public class Client {

    /**
     * Create an new individial. Generates a number between 0 and 31.
     * Returns this as an array of integers representing the binary value.
     *
     * @return
     */
    public static Individual createIndividual() {
        Random generator = new Random();
        int value = generator.nextInt(32);
        String binary = Integer.toBinaryString(value);
        int[] result = new int[5];

        StringBuilder string = new StringBuilder();
        int number = 5 - binary.length();
        for (int i = 0; i < number; i++) {
            string.append("0");
        }

        string.append(binary);

        for (int i = 0; i < 5; i++) {
            result[i] = Character.getNumericValue(string.charAt(i));
        }

        return new Individual(result);
    }

    /**
     * Convert an individual to a binary string.
     *
     * @param individual, int[]
     * @return binary string, String
     */
    public static String binaryIntToString(int[] individual) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < individual.length; i++) {
            builder.append(individual[i]);
        }
        return builder.toString();
    }

    /**
     * Convert a binary string to a number.
     *
     * @param binary, String of 0 and 1 values
     * @return number, numeric value
     */
    public static int binaryStringToNumber(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * Initialize a population based on an input parameter
     *
     * @param size, int
     * @return population, List<Individuals>
     */
    public static List<Individual> initPopulation(int size) {
        List<Individual> population = new ArrayList<>();
        while (size > 0) {
            population.add(createIndividual());
            size--;
        }
        return population;
    }

    /**
     * Compute the fitness of a population
     * <p>
     * Solve the target function with the given number of an individual
     * <p>
     * Function: f(x)= -x^2+7x
     *
     * @param population
     * @return population, the list updated with fitness values
     */
    public static List<Individual> computePopulationFitness(List<Individual> population) {
        for (Individual ind : population) {
            double input = binaryStringToNumber(binaryIntToString(ind.getBinaryInt())) * -1;
            double solution = (Math.pow(input, 2) + (7 * (input * -1)));
            ind.setFitness(solution);
        }
        return population;
    }

    /**
     * Select two individuals out of the population to become parents
     */
    public static List<Individual> getTwoParents(List<Individual> population) {
        double totalFitness = 0;

        /**calculate total fitness*/
        for (Individual ind : population) {
            totalFitness += ind.getFitness();
        }

        /**calculate the probability to be selected for every individual*/
        for (Individual ind : population) {
            ind.setProbability(ind.getFitness() / totalFitness);
        }

        double value = 0.0;
        for (Individual ind : population) {
            ind.setCumalativeProbability(value + ind.getProbability());
            value += ind.getProbability();
        }

        List<Individual> result = new ArrayList<>();

        while(result.size() < 2){
            Individual newParent = getParent(population);
            if(newParent != null && !result.contains(newParent)){
                result.add(newParent);
            }
        }

        return result;
    }

    private static Individual getParent(List<Individual> population){
        Random rand = new Random();
        double selectionValue = rand.nextDouble();

        System.out.println("----- Starting parent selection -----");
        System.out.println("Random value: "+selectionValue+"\n");
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getCumalativeProbability() <= selectionValue
                    && population.get(i + 1).getCumalativeProbability() >= selectionValue) {
                System.out.println("Current  : " + population.get(i));
                System.out.println("Next     : " + population.get(i + 1));
                System.out.println("Selected : " + population.get(i + 1)+"\n");
                return population.get(i + 1);
            } else if (population.get(i).getCumalativeProbability() >= selectionValue) {
                System.out.println("Current  : " + population.get(i));
                System.out.println("Next     : " + population.get(i + 1));
                System.out.println("Selected : " + population.get(i)+"\n");
                return population.get(i);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        List<Individual> population = initPopulation(10);
        population = computePopulationFitness(population);
        population = getTwoParents(population);
        System.out.println("Selected parents:");
        for (Individual ind : population) {
            System.out.println(ind.toString());
        }
    }
}
