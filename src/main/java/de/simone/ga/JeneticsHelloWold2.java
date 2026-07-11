package de.simone.ga;

import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;

public class JeneticsHelloWold2 {

    private static final String TEXT = "Hello World"; 

    private static Integer eval(Genotype<CharacterGene> gt) {
        char[] chars = gt.chromosome().as(CharacterChromosome.class).toArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == TEXT.charAt(i)) {
                count++;
            }
        }
        // System.out.println(new String(chars) + " - Matches: " + count);
        return count;
    }

    public static void main(String[] args) {
        System.out.println("--------- Start Hello World with Jenetics - ---------");
 
        Genotype<CharacterGene> gt = Genotype.of(CharacterChromosome.of(TEXT.length()));
        Engine<CharacterGene, Integer> engine = Engine.builder(JeneticsHelloWold2::eval, gt)
                .build();

               System.out.println( engine.offspringSelector().getClass().getSimpleName());
               System.out.println( engine.survivorsSelector().getClass().getSimpleName());
               System.out.println( engine.alterer().getClass().getSimpleName());


        EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
        Genotype<CharacterGene> result = engine.stream()
                // .limit(Limits.bySteadyFitness(10))
                .limit(3000)
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype());

        System.out.println(result);
        System.out.println(statistics);
        System.out.println("--------- End Hello World with Jenetics - ---------");

    }
}
