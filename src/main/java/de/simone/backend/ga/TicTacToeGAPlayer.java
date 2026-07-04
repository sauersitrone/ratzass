package de.simone.backend.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.IntegerRange;
import org.apache.commons.lang3.tuple.ImmutablePair;

import de.simone.backend.ga.TicTacToeEngine.GameState;
import io.jenetics.AbstractChromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.stat.MinMax;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

public class TicTacToeGAPlayer {

    private List<ImmutablePair<Integer, Integer>> initialMoves;

    /**
     * Evaluate the fitness of a genotype by simulating a game of tic-tac-toe
     * against a random opponent. this function return higher fitness for genotypes that: 
     * - encodes a sequence of moves that lead to win
     * - encodes a sequence of moves that avoid defeat
     * 
     * @param gt the genotype
     * @return the fitness 
     */
    private Integer eval(Genotype<PointGene> gt) {
        TicTacToeEngine simulator = new TicTacToeEngine();
        GameState state = simulator.setInitialMoves(initialMoves);
        if (state != GameState.IN_PROGRESS) {
            System.out.println("ERROR: Game already finished with state: " + state);
            return 0; // somenthing went wrong, should not happen
        }

        int fitness = 0;
        int moves = 0;
        List<ImmutablePair<Integer, Integer>> allele = gt.chromosome().as(PointChromosome.class).toList();
        for (ImmutablePair<Integer, Integer> move : allele) {
            state = simulator.makeMove(move.getLeft(), move.getRight());
            // If the move is illegal, return a very low fitness score
            if (state == GameState.ILLEGAL_MOVE) {
                return 0;
            }
            moves++;
            if (state != GameState.IN_PROGRESS)
                break;
        }

        fitness = state == GameState.O_WINS ? 4 : fitness;
        fitness = state == GameState.TIE ? 2 : fitness;
        fitness = state == GameState.X_WINS ? 0 : fitness;
        fitness = fitness - moves;

        return fitness;
    }

    public ImmutablePair<Integer, Integer> makeMove(List<ImmutablePair<Integer, Integer>> initialMoves) {
        this.initialMoves = initialMoves;
        int length = 9 - initialMoves.size();

        Genotype<PointGene> gt = Genotype.of(PointChromosome.of(length));
        Engine<PointGene, Integer> engine = Engine.builder(this::eval, gt)
                .populationSize(100)
                .build();

        ISeq<EvolutionResult<PointGene, Integer>> results = engine.stream()
                .limit(1000)
                .flatMap(MinMax.toStrictlyIncreasing())
                .collect(ISeq.toISeq(3));

        for (EvolutionResult<PointGene, Integer> result : results) {
            PointChromosome chromosome = result.bestPhenotype().genotype().chromosome().as(PointChromosome.class);
            List<String> seq = chromosome.stream().map(g -> g.toString()).toList();
            System.out.println(result.bestFitness() + ": " + seq);
        }

        Genotype<PointGene> bestGenotype = results.get(0).bestPhenotype().genotype();
        List<ImmutablePair<Integer, Integer>> nextMoves = bestGenotype.chromosome().as(PointChromosome.class).toList();
        ImmutablePair<Integer, Integer> nextMove = nextMoves.get(0);
        System.out.println("Selected move: " + nextMove);
        return nextMove;

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
