package main;

import java.util.*;


import models.Individual;

public class Client {
    public static void main(String[] args) {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0.85, 0.01, true, 10, 50);
        List<Individual> population = geneticAlgorithm.run();

        double totalFitness = 0.0;
        for (Individual ind : population) {
            totalFitness += ind.getFitness();
        }
        double averageFitness = totalFitness / population.size();
        Individual fittest = geneticAlgorithm.getFittest(population);
        double highestFitness = fittest.getFitness();

        System.out.println("The average fitness: " + averageFitness + "\n"
                        + "The highest fitness: " + highestFitness + "\n"
                        + "The fittest individual: " + fittest.toString()
        );
    }
}