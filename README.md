<p align="center">
  <img src="similar-execute.png" alt="nlp-hanzi-similar" width="600"/>
</p>

# nlp-hanzi-similar

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.houbb/nlp-hanzi-similar/badge.svg)](http://mvnrepository.com/artifact/com.github.houbb/nlp-hanzi-similar)
[![Build Status](https://www.travis-ci.org/houbb/nlp-hanzi-similar.svg?branch=master)](https://www.travis-ci.org/houbb/nlp-hanzi-similar?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/houbb/nlp-hanzi-similar/badge.svg?branch=master)](https://coveralls.io/github/houbb/nlp-hanzi-similar?branch=master)

汉字相似度计算工具。基于**四角编码 + 拼音 + 汉字结构 + 偏旁部首 + 笔画数 + 拆字**六个维度加权计算两个汉字的相似度。

> [在线体验](https://houbb.github.io/opensource/nlp-hanzi-similar)

---

## 创作背景

国内对于文本相似度的开源工具比较丰富，但两个汉字之间的相似度计算基本一片空白。

本项目旨在抛砖引玉，实现一个基本的汉字相似度计算工具，为汉字 NLP 贡献一点绵薄之力。

推荐阅读：

- [NLP 中文形近字相似度计算思路](https://mp.weixin.qq.com/s/i3h_15kYRb89MsApZ5nwPQ)
- [中文形近字相似度算法实现，为汉字 NLP 尽一点绵薄之力](https://mp.weixin.qq.com/s/pDt3R04-XWKSvo1hJpTSDg)
- [NLP 开源形近字算法补完计划（完结篇）](https://mp.weixin.qq.com/s/T4ubn_nCr2fkW8jui3anSA)
- [NLP 开源形近字算法之形近字列表（番外篇）](https://mp.weixin.qq.com/s/WF4PxVftBnaealOU7a06xQ)

---

## 特性

- **一行代码**：Fluent 风格，`HanziSimilarHelper.similar('末', '未')` 即可计算
- **高度自定义**：支持自定义权重、数据源、算法策略
- **词库自定义**：支持用户自定义相似度映射
- **多维度**：默认集成 6 种相似度策略
- **相似列表**：支持返回一个汉字的 Top-N 相似字列表

---

## 快速开始

### 环境要求

- JDK 1.7+
- Maven 3.x+

### Maven 引入

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>nlp-hanzi-similar</artifactId>
    <version>1.4.0</version>
</dependency>
```

### 基本用法

```java
double rate1 = HanziSimilarHelper.similar('末', '未');
// 0.9629629629629629
```

### 自定义权重

```java
double rate = HanziSimilarBs.newInstance()
                .jiegouRate(10)
                .sijiaoRate(8)
                .bushouRate(6)
                .bihuashuRate(2)
                .pinyinRate(1)
                .chaiziRate(8)
                .init()
                .similar('末', '未');
```

### 自定义相似度

在 classpath 根目录下创建 `hanzi_similar_define.txt`：

```
入人 0.9
人入 0.9
```

```java
double rate = HanziSimilarHelper.similar('人', '入');
// 0.9（优先使用用户自定义值）
```

### 相似字列表

```java
// 默认返回 Top-10
List<String> list = HanziSimilarHelper.similarList('爱');
// [爰, 爯, 受, 爭, 妥, 憂, 李, 爳, 叐, 雙]

// 指定返回数量
List<String> list = HanziSimilarHelper.similarList('爱', 5);
```

---

## 相似度维度

| 维度 | 权重 | 说明 |
|:---|:---:|:---|
| 汉字结构 | 10 | 结构相同返回满分（如左右结构、上下结构等） |
| 四角编码 | 8 | 逐位比较四角编码数字，相同位数占比作为分数 |
| 拆字 | 8 | 拆解为部件，比较共同部件的笔画数占比 |
| 偏旁部首 | 6 | 部首相同返回满分 |
| 笔画数 | 2 | 笔画差越少分数越高 |
| 拼音 | 1 | 拼音相同返回满分 |

---

## 完整配置

```java
double rate = HanziSimilarBs.newInstance()
    // 笔画数
    .bihuashuData(HanziSimilarDatas.bihuashu())
    .bihuashuSimilar(HanziSimilars.bihuashu())
    .bihuashuRate(HanziSimilarRateConst.BIAHUASHU)
    // 结构
    .jiegouData(HanziSimilarDatas.jiegou())
    .jiegouSimilar(HanziSimilars.jiegou())
    .jiegouRate(HanziSimilarRateConst.JIEGOU)
    // 部首
    .bushouData(HanziSimilarDatas.bushou())
    .bushouSimilar(HanziSimilars.bushou())
    .bushouRate(HanziSimilarRateConst.BUSHOU)
    // 四角编码
    .sijiaoData(HanziSimilarDatas.sijiao())
    .sijiaoSimilar(HanziSimilars.sijiao())
    .sijiaoRate(HanziSimilarRateConst.SIJIAO)
    // 拼音
    .pinyinRate(HanziSimilarRateConst.PINYIN)
    .pinyinSimilar(HanziSimilars.pinyin())
    // 拆字
    .chaiziRate(HanziSimilarRateConst.CHAIZI)
    .chaiziSimilar(HanziSimilars.chaizi())
    .init()
    .similar('末', '未');
```

### 可配置项

| 序号 | 属性 | 说明 |
|:---|:---|:---|
| 1 | bihuashuRate | 笔画数权重 |
| 2 | bihuashuData | 笔画数数据 |
| 3 | bihuashuSimilar | 笔画数相似度策略 |
| 4 | jiegouRate | 结构权重 |
| 5 | jiegouData | 结构数据 |
| 6 | jiegouSimilar | 结构相似度策略 |
| 7 | bushouRate | 部首权重 |
| 8 | bushouData | 部首数据 |
| 9 | bushouSimilar | 部首相似度策略 |
| 10 | sijiaoRate | 四角编码权重 |
| 11 | sijiaoData | 四角编码数据 |
| 12 | sijiaoSimilar | 四角编码相似度策略 |
| 13 | pinyinData | 拼音数据 |
| 14 | pinyinSimilar | 拼音相似度策略 |
| 15 | hanziSimilar | 汉字相似度核心策略 |
| 16 | userDefineData | 用户自定义数据 |
| 17 | chaiziRate | 拆字比例 |
| 18 | chaiziSimilar | 拆字相似度 |

所有配置均基于接口，用户可完全自定义实现。

---

## 实现原理

### 核心算法

```java
@Override
public double similar(final IHanziSimilarContext context) {
    final String charOne = context.charOne();
    final String charTwo = context.charTwo();

    // 1. 相同字
    if (charOne.equals(charTwo)) {
        return 1.0;
    }

    // 2. 用户自定义
    Map<String, Double> defineMap = context.userDefineData().dataMap();
    String defineKey = charOne + charTwo;
    if (defineMap.containsKey(defineKey)) {
        return defineMap.get(defineKey);
    }

    // 3. 加权计算各维度相似度
    double sijiaoScore = context.sijiaoSimilar().similar(context);
    double jiegouScore = context.jiegouSimilar().similar(context);
    double bushouScore = context.bushouSimilar().similar(context);
    double bihuashuScore = context.bihuashuSimilar().similar(context);
    double pinyinScore = context.pinyinSimilar().similar(context);

    // 4. 归一化
    double totalScore = sijiaoScore + jiegouScore + bushouScore + bihuashuScore + pinyinScore;
    if (totalScore <= 0) return 0;

    double limitScore = context.sijiaoRate() + context.jiegouRate()
            + context.bushouRate() + context.bihuashuRate() + context.pinyinRate();
    return totalScore / limitScore;
}
```

### 为什么不使用字典？

2 万汉字 × 2 万汉字的相似度字典接近亿级数据量，空间复杂度过高。本项目采用**实时计算**方案。

---

## Python 版本

为方便 Python 开发者学习和使用，提供了简易版 Python 实现：

> [python 版本下载](https://github.com/houbb/nlp-hanzi-similar/releases/tag/pythn)

---

## 快速体验（Windows exe）

> [下载 hanzi-similar.zip](https://github.com/houbb/nlp-hanzi-similar/releases/download/exe/hanzi-similar.zip)

解压后运行 `hanzi-similar.exe`，输入两个汉字即可计算相似度。

---

## 算法优缺点

### 优点

- 从汉字结构、编码、部首、笔画、拼音多维度综合计算
- 相比纯学术 paper 方案，更符合国内使用直觉

### 缺点

- 部首数据存在缺憾（受限于早期数据质量）
- 后续计划引入更完善的拆字字典，对比汉字所有组成部分

---

## NLP 开源矩阵

| 项目 | 说明 |
|:---|:---|
| [pinyin](https://github.com/houbb/pinyin) | 汉字转拼音 |
| [pinyin2hanzi](https://github.com/houbb/pinyin2hanzi) | 拼音转汉字 |
| [segment](https://github.com/houbb/segment) | 高性能中文分词 |
| [opencc4j](https://github.com/houbb/opencc4j) | 中文繁简体转换 |
| [nlp-hanzi-similar](https://github.com/houbb/nlp-hanzi-similar) | 汉字相似度 |
| [word-checker](https://github.com/houbb/word-checker) | 拼写检测 |
| [sensitive-word](https://github.com/houbb/sensitive-word) | 敏感词 |

---

## 后期 Roadmap

- [ ] 丰富相似度策略
- [ ] 优化默认权重
- [ ] 优化 exe 界面

---

## 变更日志

> [CHANGELOG.md](CHANGELOG.md)

## 开源协议

[Apache License 2.0](LICENSE.txt)