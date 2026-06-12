# 四角编码模糊匹配算法

> **Workspace:** /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14

---

**User**

实现一个「四角编码模糊匹配」算法，替代现有逐位精确比较。新算法应容忍相邻角位的偏移（如 7121 与 7211 应得部分分），使用 Smith-Waterman 局部对齐算法计算编码相似度，并保持与现有  IHanziSimilar  接口兼容。

**Assistant**

我来帮你实现四角编码模糊匹配算法。首先让我了解一下代码库的结构，找到相关的接口和现有实现。

让我继续搜索更多相关内容，特别关注四角编码相关的实现。



### Relevant Code Snippets
1. /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/api/IHanziSimilar.java:L1-L16
  — IHanziSimilar 接口定义，包含相似度计算方法 similar

2. /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/SijiaoSimilar.java:L1-L52
  — 四角编码相似度实现类，包含核心的相似度计算逻辑

3. /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/HanziSimilarContext.java:L77-L89
  — HanziSimilarContext 类中定义了四角编码相关的属性和方法，包括 sijiaoRate() 和 sijiaoData() 等

4. /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/HanziSimilarContext.java:L257-L265
  — HanziSimilarContext 类中 sijiaoData() 方法的具体实现，用于获取四角编码数据

5. /home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/HanziSimilarContext.java:L267-L275
  — HanziSimilarContext 类中 sijiaoSimilar() 方法的具体实现，用于获取四角编码相似度实现类

现在让我读取关键文件，了解现有接口和实现：


让我查看测试文件和相关上下文，以便更好地理解使用方式：




让我查看一下项目结构和其他Similar实现类，了解代码风格：






现在我已经了解了代码结构。让我创建任务计划并开始实现：



现在创建基于 Smith-Waterman 算法的四角编码模糊匹配实现：





现在更新 HanziSimilars 工厂类，添加新的工厂方法：







现在创建单元测试类。先查看一下测试目录结构和现有测试的风格：








让我查看一下四角编码数据，找到一些实际的编码样例：









让我查看 HanziSimilarBs 的实现，以及查找更多具有相似编码的汉字：



现在创建单元测试类，测试算法的核心逻辑（包括 7121 与 7211 的场景）和实际汉字对比：











我需要为测试添加辅助方法。让我修改 SijiaoFuzzySimilar 和 SijiaoSimilar，添加包级私有测试方法：

























现在让我更新任务状态并运行测试验证实现：


























我发现了问题！对于 7121 与 7211 这种相邻偏移的情况，当前空位罚分（-2）过于严厉。需要调整参数使其更宽容。让我修改算法参数并重新测试：



























参数调整后还不够理想。让我重新设计算法，加入对"相邻位置转置"（即位置交换）的显式处理，这是四角编码偏移最常见的场景：































让我实现一个增强版本，加入**转置（相邻字符交换）**操作的支持，这是处理四角编码相邻偏移最有效的方法：

































现在运行测试看看改进版本的效果：




































太棒了！所有测试通过！现在让我运行完整的测试套件，确保与现有功能完全兼容：










































让我查看完整的测试输出：











































让我用另一种方式运行测试来获取详细输出：












































所有测试都通过了！现在让我检查代码诊断，确保没有遗留问题：














































任务完成！我已经成功实现了四角编码模糊匹配算法。以下是实现的详细总结：

## 📋 实现内容

### 1. 新增文件：[SijiaoFuzzySimilar.java](file:///home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/SijiaoFuzzySimilar.java)

核心实现类，基于 **Smith-Waterman 局部对齐算法** 并增强了对**相邻位置转置（Transposition）**的支持：

| 参数 | 值 | 说明 |
|------|-----|------|
| MATCH_SCORE | +3 | 字符完全匹配得分 |
| MISMATCH_SCORE | -1 | 字符不匹配罚分 |
| GAP_PENALTY | -2 | 空位插入/删除罚分 |
| TRANSPOSITION_SCORE | +2 | **相邻字符位置交换**得分（关键增强） |

**算法核心特性**：
- 标准 Smith-Waterman 动态规划矩阵构建
- 额外支持 **转置操作**：当 `seq1[i-1]==seq2[j-2]` 且 `seq1[i-2]==seq2[j-1]` 时，给予部分分数
- 得分归一化到 `[0, 1]` 区间，与现有精确匹配兼容

### 2. 修改文件：[HanziSimilars.java](file:///home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/main/java/com/github/houbb/nlp/hanzi/similar/support/similar/HanziSimilars.java#L53-L60)

添加工厂方法 `sijiaoFuzzy()`：
```java
public static IHanziSimilar sijiaoFuzzy() {
    return new SijiaoFuzzySimilar();
}
```

### 3. 新增测试文件：[SijiaoFuzzySimilarTest.java](file:///home/jiangyong/ws/bytedance/solo0601/JY-067/JY-067-14/src/test/java/com/github/houbb/nlp/hanzi/similar/support/similar/SijiaoFuzzySimilarTest.java)

## 📊 测试结果

**全部 12 个测试通过**（含新增 4 个），0 失败，0 错误。

### 关键场景对比：

| 测试场景 | 编码示例 | 精确匹配得分 | 模糊匹配得分 | 提升 |
|---------|---------|------------|------------|-----|
| **相邻位置偏移** | 7121 vs 7211 | 0.50 | **0.667** | +33% ✅ |
| **完全相同** | 7121 vs 7121 | 1.00 | 1.00 | 一致 ✅ |
| **完全不同** | 1234 vs 5678 | 0.00 | 0.00 | 一致 ✅ |
| **末位不同** | 50900 vs 50908 | 0.80 | 0.80 | 一致 ✅ |
| **实际汉字（相同编码）** | 末 vs 未(50900) | 1.00 | 1.00 | 一致 ✅ |
| **实际汉字（相同编码）** | 土 vs 士(40100) | 1.00 | 1.00 | 一致 ✅ |

## 🔧 使用方式

通过 `HanziSimilarBs` 配置使用新算法：

```java
double rate = HanziSimilarBs.newInstance()
        .sijiaoSimilar(HanziSimilars.sijiaoFuzzy())  // 使用模糊匹配
        .sijiaoRate(8)
        .jiegouRate(10)
        // ... 其他配置
        .init()
        .similar('末', '未');
```

接口完全兼容 `IHanziSimilar`，可以无缝替换现有的 `SijiaoSimilar` 实现。

---

