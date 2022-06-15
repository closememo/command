package com.closememo.command.infra.diff;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
public class LineChunk {

  private final int number;
  private final List<WordChunk> wordChunks;

  public LineChunk(int number, List<WordChunk> wordChunks) {
    this.number = number;
    this.wordChunks = wordChunks;
  }

  public static LineChunk emptyLineElement() {
    return new LineChunk(0, null);
  }

  public LineChunk copy() {
    return new LineChunk(number, wordChunks);
  }

  public boolean isEmpty() {
    return number == 0;
  }

  public String getValue() {
    if (CollectionUtils.isEmpty(wordChunks)) {
      return StringUtils.EMPTY;
    }
    String value = wordChunks.stream()
        .map(WordChunk::getWord)
        .collect(Collectors.joining());
    return StringUtils.equals(value, "\n") ? StringUtils.EMPTY : value;
  }

  @Getter
  public static class WordChunk {

    private final String word;
    private final DeltaType type;

    public WordChunk(String word, DeltaType type) {
      this.word = word;
      this.type = type;
    }

    public boolean isEmpty() {
      return word == null;
    }

    public enum DeltaType {
      CHANGE,
      DELETE,
      INSERT,
      EQUAL
    }
  }
}
