package main;

import java.util.ArrayList;
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
     * Given a list of individuals determine which two can be parents. Calculates probability
     * to be chosen based on the roulette wheel selection method. 
     * 
     * @param population
     * @return Individual[], an array containing a pair of Individuals acting as parents
     */
    public static Individual[] getTwoParents(List<Individual> population) {
        double totalFitness = 0;

        /**calculate total fitness*/
        for (Individual ind : population) {
            totalFitness += ind.getFitness();
        }

        /**calculate the probability to be selected for every individual*/
        for (Individual ind : population) {
            ind.setProbability(ind.getFitness() / totalFitness);
        }

        /**Set the cumulative probability */
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
        
        Individual[] parents = new Individual[2];
        for(int i=0;i<result.size();i++){
        	parents[i] = result.get(i);
        	
        }
        
        return parents;
    }

    /**
     * Given a population select an individual to be a parent.
     * 
     * @param population
     * @return Individual, the parent
     */
    private static Individual getParent(List<Individual> population){
        Random rand = new Random();
        double selectionValue = rand.nextDouble();

        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getCumalativeProbability() <= selectionValue
                    && population.get(i + 1).getCumalativeProbability() >= selectionValue) {
                return population.get(i + 1);
            } else if (population.get(i).getCumalativeProbability() >= selectionValue) {
                return population.get(i);
            }
        }
        return null;
    }
    
    /**
     * Create two new Individuals based on the given pair of individuals.
     * This is done with a two point crossover procedure.
     * 
     * @param parents
     * @return Individual[], the new pair of individuals.
     */
    public static Individual[] getChildren(Individual[] parents){
    	int[] firstBinary = parents[0].getBinaryInt();
    	int[] secondBinary = parents[1].getBinaryInt();
    	
    	int[] firstChildBinary = new int[5];
    	
    	firstChildBinary[0] = secondBinary[0];
    	firstChildBinary[1] = firstBinary[1];
    	firstChildBinary[2] = firstBinary[2];
    	firstChildBinary[3] = firstBinary[3];
    	firstChildBinary[4] = secondBinary[4];
    	
    	int[] secondChildBinary = new int[5];
    	
    	secondChildBinary[0] = firstBinary[0];
    	secondChildBinary[1] = secondBinary[1];
    	secondChildBinary[2] = secondBinary[2];
    	secondChildBinary[3] = secondBinary[3];
    	secondChildBinary[4] = firstBinary[4];
    	
    	Individual firstChild = new Individual(firstChildBinary);
    	Individual secondChild = new Individual(secondChildBinary);
    	
    	Individual[] children = new Individual[2];
    	children[0] = firstChild;
    	children[1] = secondChild;
    	return children;
    }

    /**
     * Mutates an individual. Take a random value from the original and flip it.
     * I.E 0 ->1 and 1 ->0. This is done twice.
     * @param ind
     * @return Individual, the mutated individual
     */
    public static Individual mutate(Individual ind){
    	Random rand = new Random();
    	
    	int[] genes = ind.getBinaryInt();
    	int mutationValue = 2;
    	
    	while(mutationValue>0){
    		int gene = rand.nextInt(5);
    		if(genes[gene]==0){
        		genes[gene] = 1;
        	}else{
        		genes[gene] = 0;
        	}
    		mutationValue --;
    	}

    	Individual mutant = new Individual(genes);
    	return mutant;
    }
    
    /**
     * Given a list of individuals looks up the individual with the highest fitness value.
     * @param population
     * @return individual, the fittest
     */
    public static Individual getFittest(List<Individual> population){
    	boolean first = true;
    	Individual fittest = null;
    	
    	for(Individual ind:population){
    		if(first == true){
    			fittest = ind;
    			first = false;
    		} else if(ind.getFitness()>fittest.getFitness()){
    			fittest = ind;
    		}    		
    	}
    	return fittest;
    }
    
    /**
     * Performs a genetic algortim with the following parameters.
     * 
     * @param crossOverRate, double, value between 0 and 1
     * @param mutationRate, double value between 0 and 1
     * @param elitism, boolean, whether or not the best individual should be retained through the loops.
     * @param populationSize, int, the size of the population
     * @param amountOfRuns, the amount of repetitions
     * 
     * @return List<Individual>, the final population
     */
    public static List<Individual> run(double crossOverRate, double mutationRate, boolean elitism,  
    		int populationSize,int amountOfRuns){
    	
    	Random rand = new Random();
    	
    	/**Generate the first population */
    	List<Individual> population = initPopulation(populationSize);
    	
    	while(amountOfRuns>0){
    		/**Compute the fitness of every individual */
    		population = computePopulationFitness(population);
    		  		
    		/**Apply elitism*/
    		Individual fittest = null;
    		if(elitism == true){
    			fittest = getFittest(population);
    		}
    		
    		/**Select parents */
    		Individual[] parents = getTwoParents(population);
    		
    		/**Perform crossover and mutation */
    		Individual[] children = null;
    		
    		if(rand.nextDouble()<crossOverRate){
    			children = getChildren(parents); 
    		}
    		
    		if(children!=null){
    			for(int i=0; i<children.length;i++){
    				if(rand.nextDouble()<mutationRate){
        				children[i] = mutate(children[i]);
        			}
    			}
    		}
    		
    		/**Add the individuals to be preserved */
    		List<Individual> newPop = new ArrayList<>();
    		if(fittest!=null){
    			newPop.add(fittest);
    		}
    		if(children!=null){
    			for(Individual ind:children){
    				newPop.add(ind);
    			}
    		}
    		
    		while(newPop.size()<populationSize){
    			newPop.add(createIndividual());
    		}
    		population = newPop;
    		amountOfRuns--;
    	}
    	
    	List<Individual> result = computePopulationFitness(population);
    	return result;
    }
    
    public static void main(String[] args) {
    	List<Individual> population = run(1,1,true,50,200);
    	double totalFitness = 0.0;
    	for(Individual ind:population){
    		totalFitness += ind.getFitness();
    	}
    	double averageFitness = totalFitness/population.size();
    	Individual fittest = getFittest(population);
    	double highestFitness = fittest.getFitness();
    	
    	System.out.println("The average fitness: " + averageFitness +"\n"
    				+ "The higest fitness: " + highestFitness+ "\n"
    				+ "The fittest individual: " + fittest.toString()
    			);
    }
}