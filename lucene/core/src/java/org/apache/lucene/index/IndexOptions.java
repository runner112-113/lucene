/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.index;

/**
 * Controls how much information is stored in the postings lists.
 *
 * @lucene.experimental
 */
public enum IndexOptions {
  // NOTE: order is important here; FieldInfo uses this
  // order to merge two conflicting IndexOptions (always
  // "downgrades" by picking the lowest).
  /** Not indexed
   * 作用：完全跳过倒排索引构建
   * 存储内容：仅保留字段原始值（需配合Store.YES）
   * 查询限制：无法通过TermQuery等词汇级查询检索
   * 适用场景：展示字段（如URL）、排序字段（需配合DocValues）
   * */
  NONE,
  /**
   * Only documents are indexed: term frequencies and positions are omitted. Phrase and other
   * positional queries on the field will throw an exception, and scoring will behave as if any term
   * in the document appears only once.
   *
   * 存储内容
   *  postings -> [docID1, docID2,...]
   * 功能特征：
   * ✓ 支持TermQuery存在性判断
   * ✗ 词频统计失效（所有TF=1）
   * ✗ 短语查询抛异常
   * 性能优势：索引体积减少30-50%（相比更高层级）
   */
  DOCS,
  /**
   * Only documents and term frequencies are indexed: positions are omitted. This enables normal
   * scoring, except Phrase and other positional queries will throw an exception.
   *
   * 数据结构扩展
   *  postings -> [docID1(freq=3), docID2(freq=5),...]
   * 功能增强：
   * ✓ 支持BM25/TF-IDF等词频敏感评分
   * ✗ 仍不支持PhraseQuery
   * 典型应用：日志聚合分析（统计错误码出现次数）
   */
  DOCS_AND_FREQS,
  /**
   * Indexes documents, frequencies and positions. This is a typical default for full-text search:
   * full scoring is enabled and positional queries are supported.
   *
   * 位置信息存储
   *  postings -> [docID1(pos1,pos2), docID2(pos1),...]
   * 新增能力：
   * ✓ 支持短语查询（"quick brown"）
   * ✓ 支持邻近查询（~5）
   * ✓ 支持词序敏感分析
   * 存储代价：索引体积增加40-60%（相比DOCS_AND_FREQS）
   */
  DOCS_AND_FREQS_AND_POSITIONS,
  /**
   * Indexes documents, frequencies, positions and offsets. Character offsets are encoded alongside
   * the positions.
   *
   * 偏移量记录
   *  postings -> [docID1(startOffset1-endOffset1),...]
   * 特殊用途：
   * ✓ 搜索结果高亮（需要字符级定位）
   * ✓ 自定义分词器调试
   * 性能影响：索引体积额外增加15-25%
   */
  DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS,
}
