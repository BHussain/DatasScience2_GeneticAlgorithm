package main;

import models.Individual;

import java.util.*;

public class GeneticAlgorithm {
    private double crossOverRate;
    private double mutationRate;
    private boolean elitism;
    private int populationSize;
    private int amountOfRuns;
    private static Random rand = new Random();

    /**
     * Initialize the values needed to run the genetic algorithm.
     *
     * @param crossOverRate,  double, value between 0 and 1
     * @param mutationRate,   double value between 0 and 1
     * @param elitism,        boolean, whether or not the best individual should be retained through the loops.
     * @param populationSize, int, the size of the population
     * @param amountOfRuns,   the amount of repetitions
     */
    public GeneticAlgorithm(double crossOverRate, double mutationRate, boolean elitism,
                            int populationSize, int amountOfRuns){
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.populationSize = populationSize;
        this.amountOfRuns = amountOfRuns;
    }

    /**
     * Performs the genetic algorithm.
     *
     * @return List<Individual>, the final population
     */
    public List<Individual> run() {
        Random rand = new Random();

        /** Generate the first population */
        List<Individual> population = initPopulation(populationSize);

        while (amountOfRuns > 0) {
            /** Compute the fitness of every individual */
            population = computePopulationFitness(population);

            /** Apply elitism*/
            List<Individual> fittestList = new ArrayList<>();
            
            List<Individual> evalPop = new ArrayList<>();
            for(Individual ind:population){
            	evalPop.add(ind);
            }
            
            if (elitism) {
            	int elitismValue = 1;
                
                while(elitismValue!=0){
                	Individual ind = getFittest(population);
                	fittestList.add(ind);
                	evalPop.remove(ind);
                	elitismValue--;
                }
            }

            /** Select parents */
            Individual[] parents = getTwoParents(population);

            /** Perform crossover and mutation */
            Individual[] children = null;

            if (rand.nextDouble() < crossOverRate) {
                children = getChildren(parents);
            }

            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (rand.nextDouble() < mutationRate) {
                        children[i] = mutate(children[i]);
                    }
                }
            }

            /** Add the individuals to be preserved */
            List<Individual> newPop = new ArrayList<>();

            if(!fittestList.isEmpty()){
            	for(Individual ind: fittestList){
            		newPop.add(ind);
            	}
            }
            if (children != null) {
                Collections.addAll(newPop, children);
            }

            while (newPop.size() < populationSize) {
                //newPop.add(createIndividual());
            	addChildren(population,newPop);
            }
            population = newPop;
            amountOfRuns--;
        }

        return computePopulationFitness(population);
    }
    
    private void addChildren(List<Individual> population,List<Individual> pop){
    	/** Select parents */
        Individual[] parents = getTwoParents(population);

        /** Perform crossover and mutation */
        Individual[] children = null;

        if (rand.nextDouble() < crossOverRate) {
            children = getChildren(parents);
        }

        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                if (rand.nextDouble() < mutationRate) {
                    children[i] = mutate(children[i]);
                }
            }
            Collections.addAll(pop, children);
        }
  
    }

    /**
     * Create an new individial. Generates a number between 0 and 31.
     * Returns this as an array of integers representing the binary value.
     *
     * @return a new individual.
     */
    private Individual createIndividual() {
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
    private String binaryIntToString(int[] individual) {
        StringBuilder builder = new StringBuilder();
        for (int binary : individual) {
            builder.append(binary);
        }
        return builder.toString();
    }

    /**
     * Convert a binary string to a number.
     *
     * @param binary, String of 0 and 1 values
     * @return number, numeric value
     */
    private int binaryStringToNumber(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * Initialize a population based on an input parameter
     *
     * @param size, int
     * @return population, List<Individuals>
     */
    private List<Individual> initPopulation(int size) {
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
     * @param population, a list containing the individuals of the population.
     * @return population, the list updated with fitness values
     */
    private List<Individual> computePopulationFitness(List<Individual> population) {
        for (Individual ind : population) {
            double input = binaryStringToNumber(binaryIntToString(ind.getBinaryInt()));
            double solution = (Math.pow(input, 2) * -1) + (7 * input);
            ind.setFitness(solution);
        }
        return population;
    }

    /**
     * Given a list of individuals determine which two can be parents. Calculates probability
     * to be chosen based on the roulette wheel selection method.
     *
     * @param population, a list containing the individuals of the population.
     * @return Individual[], an array containing a pair of Individuals acting as parents
     */
    private Individual[] getTwoParents(List<Individual> population) {
        double totalFitness = 0;
        
        /**Scale the fitness up*/
        double lowest = 100;
        for(Individual ind: population){
        	if(ind.getFitness()<lowest){
        		lowest = ind.getFitness();
        	}
        }
        
        if(lowest<0){
        	lowest= lowest*-1;
        }
        
        for(Individual ind:population){
        	ind.setScaledFitness(ind.getFitness()+lowest);
        }

        /** calculate total fitness*/
        for (Individual ind : population) {
            totalFitness += ind.getScaledFitness();
        }

        /** calculate the probability to be selected for every individual*/
        for (Individual ind : population) {
            ind.setProbability(ind.getScaledFitness() / totalFitness);
        }

        /** Set the cumulative probability */
        double value = 0.0;
        for (Individual ind : population) {
            ind.setCumulativeProbability(value + ind.getProbability());
            value += ind.getProbability();
        }

        List<Individual> result = new ArrayList<>();

        while (result.size() < 2) {
            Individual newParent = getParent(population);
            if (newParent != null) { // && !result.contains(newParent)) {
                result.add(newParent);
            }
        }

        Individual[] parents = new Individual[2];
        for (int i = 0; i < result.size(); i++) {
            parents[i] = result.get(i);

        }

        return parents;
    }

    /**
     * Given a population select an individual to be a parent.
     *
     * @param population, a list containing the individuals of the population.
     * @return Individual, the parent
     */
    private Individual getParent(List<Individual> population) {
        
        double selectionValue = rand.nextDouble();

        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getCumulativeProbability() <= selectionValue
                    && population.get(i + 1).getCumulativeProbability() >= selectionValue) {
                return population.get(i + 1);
            } else if (population.get(i).getCumulativeProbability() >= selectionValue) {
                return population.get(i);
            }
        }
        System.out.println("I am here, this is strange");
        return null;
    }

    /**
     * Create two new Individuals based on the given pair of individuals.
     * This is done with a single point crossover procedure.
     *
     * @param parents, the parent to create children with.
     * @return Individual[], the new pair of individuals.
     */
    private Individual[] getChildren(Individual[] parents) {
        int length = parents[0].getBinaryInt().length;
        int halfLength = length / 2;

        int[] firstBinary = parents[0].getBinaryInt();
        int[] secondBinary = parents[1].getBinaryInt();

        int[] firstChildBinary = combineArray(Arrays.copyOfRange(firstBinary, 0, halfLength),
                Arrays.copyOfRange(secondBinary, halfLength, length));

        int[] secondChildBinary = combineArray(Arrays.copyOfRange(secondBinary, 0, halfLength),
                Arrays.copyOfRange(firstBinary, halfLength, length));

        Individual firstChild = new Individual(firstChildBinary);
        Individual secondChild = new Individual(secondChildBinary);

        return new Individual[]{firstChild, secondChild};
    }

    /**
     * Combine two int arrays.
     *
     * @param firstArray  the first int[].
     * @param secondArray the second int[].
     * @return a new array of both the arrays combined.
     */
    private int[] combineArray(int[] firstArray, int[] secondArray) {
        int[] newArray = new int[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, newArray, 0, firstArray.length);
        System.arraycopy(secondArray, 0, newArray, firstArray.length, secondArray.length);
        return newArray;
    }

    /**
     * Mutates an individual. Take a random value from the original and flip it.
     * I.E 0 ->1 and 1 ->0. This is done twice.
     *
     * @param ind, the individual to mutate.
     * @return Individual, the mutated individual
     */
    private Individual mutate(Individual ind) {
        Random rand = new Random();

        int[] genes = ind.getBinaryInt();
        int mutationValue = 2;

        while (mutationValue > 0) {
            int gene = rand.nextInt(5);
            if (genes[gene] == 0) {
                genes[gene] = 1;
            } else {
                genes[gene] = 0;
            }
            mutationValue--;
        }

        return new Individual(genes);
    }

    /**
     * Given a list of individuals looks up the individual with the highest fitness value.
     *
     * @param population, a list containing all the individuals in the population.
     * @return individual, the fittest
     */
    public Individual getFittest(List<Individual> population) {
        boolean first = true;
        Individual fittest = null;

        for (Individual ind : population) {
            if (first) {
                fittest = ind;
                first = false;
            } else if (ind.getFitness() > fittest.getFitness()) {
                fittest = ind;
            }
        }
        return fittest;
    }
}
