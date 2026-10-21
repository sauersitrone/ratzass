package de.simone.utilai;

public class ActionScores {
    private double score;

    public void addScore(double score) {
        if (!(score >= 0 && score <= 1))
            throw new IllegalArgumentException("Score must be between 0 and 1");

        this.score += score;
    }

    public double getScore() {
        return this.score;
    }
}
