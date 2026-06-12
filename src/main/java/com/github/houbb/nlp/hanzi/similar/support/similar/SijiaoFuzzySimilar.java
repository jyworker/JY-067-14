package com.github.houbb.nlp.hanzi.similar.support.similar;

import com.github.houbb.nlp.hanzi.similar.api.IHanziSimilar;
import com.github.houbb.nlp.hanzi.similar.api.IHanziSimilarContext;

public class SijiaoFuzzySimilar implements IHanziSimilar {

    private static final int MATCH_SCORE = 3;
    private static final int MISMATCH_SCORE = -1;
    private static final int GAP_PENALTY = -2;
    private static final int TRANSPOSITION_SCORE = 2;

    @Override
    public double similar(IHanziSimilarContext similarContext, String charOne, String charTwo) {
        String codeOne = similarContext.sijiaoData().dataMap().get(charOne);
        String codeTwo = similarContext.sijiaoData().dataMap().get(charTwo);
        if (codeOne != null && codeTwo != null) {
            double score = calcSmithWatermanScore(codeOne, codeTwo);
            return score * similarContext.sijiaoRate();
        }
        return 0;
    }

    private double calcSmithWatermanScore(String seq1, String seq2) {
        int len1 = seq1.length();
        int len2 = seq2.length();

        if (len1 == 0 || len2 == 0) {
            return 0;
        }

        int[][] scoreMatrix = new int[len1 + 1][len2 + 1];
        int maxScore = 0;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int matchOrMismatch = scoreMatrix[i - 1][j - 1]
                        + similarity(seq1.charAt(i - 1), seq2.charAt(j - 1));
                int delete = scoreMatrix[i - 1][j] + GAP_PENALTY;
                int insert = scoreMatrix[i][j - 1] + GAP_PENALTY;

                int transposition = 0;
                if (i > 1 && j > 1
                        && seq1.charAt(i - 1) == seq2.charAt(j - 2)
                        && seq1.charAt(i - 2) == seq2.charAt(j - 1)) {
                    transposition = scoreMatrix[i - 2][j - 2] + TRANSPOSITION_SCORE;
                }

                scoreMatrix[i][j] = Math.max(0,
                        Math.max(matchOrMismatch,
                                Math.max(delete,
                                        Math.max(insert, transposition))));

                if (scoreMatrix[i][j] > maxScore) {
                    maxScore = scoreMatrix[i][j];
                }
            }
        }

        int maxPossibleScore = Math.min(len1, len2) * MATCH_SCORE;
        if (maxPossibleScore == 0) {
            return 0;
        }

        return (double) maxScore / (double) maxPossibleScore;
    }

    private int similarity(char c1, char c2) {
        if (c1 == c2) {
            return MATCH_SCORE;
        }
        return MISMATCH_SCORE;
    }

    double calcSmithWatermanScoreForTest(String seq1, String seq2) {
        return calcSmithWatermanScore(seq1, seq2);
    }

}
