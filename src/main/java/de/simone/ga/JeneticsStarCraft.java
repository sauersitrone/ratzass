package de.simone.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.IntegerRange;
import org.apache.commons.lang3.tuple.ImmutablePair;

import io.jenetics.AbstractChromosome;
import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

public class JeneticsStarCraft {

    private static final String TEXT = "Hello World";

    private static Integer buildMarine(Genotype<CharacterGene> gt) {
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
        Engine<CharacterGene, Integer> engine = Engine.builder(JeneticsStarCraft::buildMarine, gt)
                .build();

        System.out.println(engine.offspringSelector().getClass().getSimpleName());
        System.out.println(engine.survivorsSelector().getClass().getSimpleName());
        System.out.println(engine.alterer().getClass().getSimpleName());

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

    private static class PointGene implements Gene<ImmutablePair<Integer, Integer>, PointGene> {
        private static final IntegerRange range = IntegerRange.of(0, 2);
        private final ImmutablePair<Integer, Integer> value;

        public PointGene(int row, int col) {
            this.value = new ImmutablePair<>(row, col);
        }

        @Override
        public ImmutablePair<Integer, Integer> allele() {
            return value;
        }

        @Override
        public PointGene newInstance(ImmutablePair<Integer, Integer> allele) {
            return new PointGene(allele.getLeft(), allele.getRight());
        }

        @Override
        public PointGene newInstance() {
            int row = RandomRegistry.random().nextInt(0, 3);
            int col = RandomRegistry.random().nextInt(0, 3);
            return new PointGene(row, col);
        }

        @Override
        public boolean isValid() {
            return range.contains(value.getKey()) && range.contains(value.getValue());
        }

        @Override
        public String toString() {
            return "(" + value.left + ", " + value.right + ')';
        }
    }

    public static class PointChromosome extends AbstractChromosome<PointGene> {
        private PointChromosome(ISeq<PointGene> genes) {
            super(genes);
        }

        @Override
        public AbstractChromosome<PointGene> newInstance(ISeq<PointGene> genes) {
            return new PointChromosome(genes);
        }

        @Override
        public AbstractChromosome<PointGene> newInstance() {
            return of(length());
        }

        public List<ImmutablePair<Integer, Integer>> toList() {
            List<ImmutablePair<Integer, Integer>> list = _genes.stream().map(PointGene::allele).toList();
            return list;
        }

        public static PointChromosome of(int length) {
            List<PointGene> genes = new ArrayList<>();
            PointGene gene = new PointGene(0, 0);
            for (int i = 0; i < length; i++) {
                genes.add(gene.newInstance());
            }
            ISeq<PointGene> iSeq = ISeq.of(genes);
            return new PointChromosome(iSeq);
        }
    }
}

