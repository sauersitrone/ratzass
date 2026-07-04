package de.simone.backend.ga;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

public class JeneticsHelloWold {

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome().as(BitChromosome.class).bitCount();
    }

    public static void main(String[] args) {
        System.out.println("--------- Start Hello World with Jenetics - ---------");

        Genotype<BitGene> gt = Genotype.of(BitChromosome.of(20, 0.5));
        Engine<BitGene, Integer> engine = Engine.builder(JeneticsHelloWold::eval, gt)
                .build();

        Genotype<BitGene> result = engine.stream()
                .limit(100)
                .collect(EvolutionResult.toBestGenotype());

        System.out.println(result);
        System.out.println("--------- End Hello World with Jenetics - ---------");

    }
}
