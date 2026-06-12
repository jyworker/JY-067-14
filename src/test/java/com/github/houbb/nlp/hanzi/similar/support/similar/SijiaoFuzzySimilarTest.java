package com.github.houbb.nlp.hanzi.similar.support.similar;

import com.github.houbb.nlp.hanzi.similar.bs.HanziSimilarBs;
import com.github.houbb.nlp.hanzi.similar.support.data.HanziSimilarDatas;
import org.junit.Assert;
import org.junit.Test;

public class SijiaoFuzzySimilarTest {

    @Test
    public void testFuzzySimilarExactMatch() {
        double rate = HanziSimilarBs.newInstance()
                .sijiaoSimilar(HanziSimilars.sijiaoFuzzy())
                .sijiaoRate(1.0)
                .jiegouRate(0)
                .bushouRate(0)
                .bihuashuRate(0)
                .pinyinRate(0)
                .chaiziRate(0)
                .init()
                .similar('末', '未');

        System.out.println("末 vs 未 (相同编码 50900): " + rate);
        Assert.assertTrue("相同编码的相似度应接近1", rate > 0.99);
    }

    @Test
    public void testFuzzySimilarWithOffset() {
        SijiaoFuzzySimilar fuzzySimilar = new SijiaoFuzzySimilar();
        HanziSimilarContext context = new HanziSimilarContext();
        context.sijiaoData(HanziSimilarDatas.sijiao());
        context.sijiaoRate(1.0);

        double rate1 = fuzzySimilar.similar(context, "土", "士");
        System.out.println("土 vs 士 (相同编码 40100): " + rate1);
        Assert.assertTrue("土 vs 士 编码相同", rate1 > 0.99);

        double rate2 = fuzzySimilar.similar(context, "己", "已");
        System.out.println("己 vs 已 (相同编码 17717): " + rate2);
        Assert.assertTrue("己 vs 已 编码相同", rate2 > 0.99);
    }

    @Test
    public void testSmithWatermanAlgorithmCore() {
        SijiaoFuzzySimilar fuzzySimilar = new SijiaoFuzzySimilar();
        SijiaoSimilar exactSimilar = new SijiaoSimilar();

        MockContext mockCtx7121 = new MockContext("7121", "7211");
        MockContext mockCtxSame = new MockContext("7121", "7121");
        MockContext mockCtxDiff = new MockContext("1234", "5678");
        MockContext mockCtx50900 = new MockContext("50900", "50908");

        double fuzzy7121 = fuzzySimilar.calcSmithWatermanScoreForTest("7121", "7211");
        double exact7121 = exactSimilar.calcScoreForTest("7121", "7211");
        System.out.println("7121 vs 7211 (相邻偏移) - 精确匹配: " + exact7121 + ", 模糊匹配: " + fuzzy7121);
        Assert.assertTrue("模糊匹配对相邻偏移应得部分分 (> 0.2)", fuzzy7121 > 0.2);
        Assert.assertTrue("模糊匹配得分应高于精确匹配", fuzzy7121 > exact7121);

        double fuzzySame = fuzzySimilar.calcSmithWatermanScoreForTest("7121", "7121");
        double exactSame = exactSimilar.calcScoreForTest("7121", "7121");
        System.out.println("7121 vs 7121 (完全相同) - 精确匹配: " + exactSame + ", 模糊匹配: " + fuzzySame);
        Assert.assertEquals("完全相同的编码模糊匹配得分应为1.0", 1.0, fuzzySame, 0.001);

        double fuzzyDiff = fuzzySimilar.calcSmithWatermanScoreForTest("1234", "5678");
        double exactDiff = exactSimilar.calcScoreForTest("1234", "5678");
        System.out.println("1234 vs 5678 (完全不同) - 精确匹配: " + exactDiff + ", 模糊匹配: " + fuzzyDiff);
        Assert.assertTrue("完全不同的编码得分应很低", fuzzyDiff < 0.3);

        double fuzzy509 = fuzzySimilar.calcSmithWatermanScoreForTest("50900", "50908");
        double exact509 = exactSimilar.calcScoreForTest("50900", "50908");
        System.out.println("50900 vs 50908 (末位不同) - 精确匹配: " + exact509 + ", 模糊匹配: " + fuzzy509);
        Assert.assertTrue("末位不同的模糊匹配应高于0.6", fuzzy509 > 0.6);
    }

    @Test
    public void testInterfaceCompatibility() {
        double rateFuzzy = HanziSimilarBs.newInstance()
                .sijiaoSimilar(HanziSimilars.sijiaoFuzzy())
                .sijiaoRate(8)
                .jiegouRate(10)
                .bushouRate(6)
                .bihuashuRate(2)
                .pinyinRate(1)
                .chaiziRate(8)
                .init()
                .similar('末', '未');

        double rateExact = HanziSimilarBs.newInstance()
                .sijiaoSimilar(HanziSimilars.sijiao())
                .sijiaoRate(8)
                .jiegouRate(10)
                .bushouRate(6)
                .bihuashuRate(2)
                .pinyinRate(1)
                .chaiziRate(8)
                .init()
                .similar('末', '未');

        System.out.println("综合评分 - 模糊匹配: " + rateFuzzy + ", 精确匹配: " + rateExact);
        Assert.assertTrue("综合评分应为正数", rateFuzzy > 0);
    }

    private static class MockContext extends HanziSimilarContext {
        private final String code1;
        private final String code2;

        MockContext(String code1, String code2) {
            this.code1 = code1;
            this.code2 = code2;
            this.sijiaoData(new com.github.houbb.nlp.hanzi.similar.api.IHanziData<String>() {
                @Override
                public java.util.Map<String, String> dataMap() {
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    map.put("A", code1);
                    map.put("B", code2);
                    return map;
                }
            });
            this.sijiaoRate(1.0);
        }
    }

}
