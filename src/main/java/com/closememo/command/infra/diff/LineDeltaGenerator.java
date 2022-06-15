package com.closememo.command.infra.diff;

import com.closememo.command.domain.difference.DeltaType;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.difference.LineDelta.Line;
import com.closememo.command.infra.diff.LineChunk.WordChunk;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.EqualDelta;
import com.github.difflib.patch.InsertDelta;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class LineDeltaGenerator {

  public static List<LineDelta> generateLineDeltaList(List<AbstractDelta<String>> deltas) {
    List<DiffChunk> diffChunks = new ArrayList<>();
    for (AbstractDelta<String> delta : deltas) {
      addChunk(diffChunks, delta);
    }

    Pair<List<LineChunk>, List<LineChunk>> pair = generateLineChunkPair(diffChunks);
    List<LineChunk> sourceElements = pair.getLeft();
    List<LineChunk> targetElements = pair.getRight();

    return generateLineDeltaList(sourceElements, targetElements);
  }

  private static void addChunk(List<DiffChunk> diffChunks, AbstractDelta<String> delta) {
    com.github.difflib.patch.DeltaType deltaType = delta.getType();
    switch (deltaType) {
      case CHANGE:
        addChunk(diffChunks, (ChangeDelta<String>) delta);
        break;
      case DELETE:
        addChunk(diffChunks, (DeleteDelta<String>) delta);
        break;
      case INSERT:
        addChunk(diffChunks, (InsertDelta<String>) delta);
        break;
      case EQUAL:
        addChunk(diffChunks, (EqualDelta<String>) delta);
        break;
      default:
        throw new RuntimeException();
    }
  }

  private static void addChunk(List<DiffChunk> diffChunks, ChangeDelta<String> delta) {
    List<String> sourceList = delta.getSource().getLines();
    List<String> targetList = delta.getTarget().getLines();
    for (int i = 0; i < Math.max(sourceList.size(), targetList.size()); i++) {
      String source = i < sourceList.size() ? sourceList.get(i) : null;
      String target = i < targetList.size() ? targetList.get(i) : null;

      if (source != null && target != null) {
        diffChunks.add(new DiffChunk(source, target, DiffChunk.DeltaType.CHANGE));
      }

      if (source == null) {
        diffChunks.add(new DiffChunk(null, target, DiffChunk.DeltaType.INSERT));
      }

      if (target == null) {
        diffChunks.add(new DiffChunk(source, null, DiffChunk.DeltaType.DELETE));
      }
    }
  }

  private static void addChunk(List<DiffChunk> diffChunks, DeleteDelta<String> delta) {
    List<String> sourceList = delta.getSource().getLines();
    for (String word : sourceList) {
      diffChunks.add(new DiffChunk(word, null, DiffChunk.DeltaType.DELETE));
    }
  }

  private static void addChunk(List<DiffChunk> diffChunks, InsertDelta<String> delta) {
    List<String> targetList = delta.getTarget().getLines();
    for (String word : targetList) {
      diffChunks.add(new DiffChunk(null, word, DiffChunk.DeltaType.INSERT));
    }
  }

  private static void addChunk(List<DiffChunk> diffChunks, EqualDelta<String> delta) {
    List<String> sourceList = delta.getSource().getLines();
    for (String word : sourceList) {
      diffChunks.add(new DiffChunk(word, word, DiffChunk.DeltaType.EQUAL));
    }
  }

  private static Pair<List<LineChunk>, List<LineChunk>> generateLineChunkPair(
      List<DiffChunk> diffChunks) {

    LineChunkGenerator sourceLineChunkGenerator = new LineChunkGenerator();
    LineChunkGenerator targetLineChunkGenerator = new LineChunkGenerator();

    for (DiffChunk diffChunk : diffChunks) {
      String source = diffChunk.getSource();
      String target = diffChunk.getTarget();
      DiffChunk.DeltaType deltaType = diffChunk.getDeltaType();

      switch (deltaType) {
        case CHANGE:
          if (StringUtils.equals(source, "\n")) {
            targetLineChunkGenerator.empty();
          }
          if (StringUtils.equals(target, "\n")) {
            sourceLineChunkGenerator.empty();
          }
          sourceLineChunkGenerator.add(new WordChunk(source, WordChunk.DeltaType.CHANGE));
          targetLineChunkGenerator.add(new WordChunk(target, WordChunk.DeltaType.CHANGE));
          break;
        case DELETE:
          if (StringUtils.equals(source, "\n")) {
            targetLineChunkGenerator.empty();
          }
          sourceLineChunkGenerator.add(new WordChunk(source, WordChunk.DeltaType.DELETE));
          break;
        case INSERT:
          if (StringUtils.equals(target, "\n")) {
            sourceLineChunkGenerator.empty();
          }
          targetLineChunkGenerator.add(new WordChunk(target, WordChunk.DeltaType.INSERT));
          break;
        case EQUAL:
          sourceLineChunkGenerator.add(new WordChunk(source, WordChunk.DeltaType.EQUAL));
          targetLineChunkGenerator.add(new WordChunk(target, WordChunk.DeltaType.EQUAL));
          break;
        default:
          throw new RuntimeException();
      }
    }

    return Pair.of(sourceLineChunkGenerator.get(), targetLineChunkGenerator.get());
  }

  private static List<LineDelta> generateLineDeltaList(
      List<LineChunk> sourceElements, List<LineChunk> targetElements) {

    int sourceLinePosition = 0;
    int targetLinePosition = 0;

    List<LineDelta> lineDeltas = new ArrayList<>();

    for (int i = 0; i < sourceElements.size(); i++) {
      LineChunk sourceElement = sourceElements.get(i);
      LineChunk targetElement = targetElements.get(i);

      DeltaType sourceType = getDeltaType(sourceElement);
      DeltaType targetType = getDeltaType(targetElement);

      if (needInsert(sourceType, targetType)) {
        lineDeltas.add(new LineDelta(
            DeltaType.INSERT,
            new Line(sourceLinePosition, null),
            new Line(targetLinePosition++, targetElement.getValue())
        ));
        continue;
      }

      if (needDelete(sourceType, targetType)) {
        lineDeltas.add(new LineDelta(
            DeltaType.DELETE,
            new Line(sourceLinePosition++, sourceElement.getValue()),
            new Line(targetLinePosition, null)
        ));
        continue;
      }

      if (Objects.equals(sourceType, DeltaType.EQUAL)
          && Objects.equals(targetType, DeltaType.EQUAL)) {
        lineDeltas.add(new LineDelta(
            DeltaType.EQUAL,
            new Line(sourceLinePosition++, sourceElement.getValue()),
            new Line(targetLinePosition++, targetElement.getValue())
        ));
        continue;
      }

      lineDeltas.add(new LineDelta(
          DeltaType.CHANGE,
          new Line(sourceLinePosition++, sourceElement.getValue()),
          new Line(targetLinePosition++, targetElement.getValue())
      ));
    }

    return lineDeltas;
  }

  private static boolean needInsert(DeltaType sourceType, DeltaType targetType) {
    if (sourceType != null) {
      return false;
    }
    if (Objects.equals(targetType, DeltaType.EQUAL)) {
      return true;
    }
    return Objects.equals(targetType, DeltaType.INSERT)
        || Objects.equals(targetType, DeltaType.CHANGE);
  }

  private static boolean needDelete(DeltaType sourceType, DeltaType targetType) {
    if (targetType != null) {
      return false;
    }
    if (Objects.equals(sourceType, DeltaType.EQUAL)) {
      return true;
    }
    return Objects.equals(sourceType, DeltaType.CHANGE)
        || Objects.equals(sourceType, DeltaType.DELETE);
  }

  private static DeltaType getDeltaType(LineChunk lineChunk) {
    if (lineChunk.getWordChunks() == null) {
      return null;
    }
    List<WordChunk.DeltaType> types = lineChunk.getWordChunks().stream()
        .map(WordChunk::getType)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    if (types.size() == 1) {
      return convert(types.get(0));
    } else {
      return DeltaType.CHANGE;
    }
  }

  private static DeltaType convert(WordChunk.DeltaType type) {
    switch (type) {
      case CHANGE:
        return DeltaType.CHANGE;
      case DELETE:
        return DeltaType.DELETE;
      case INSERT:
        return DeltaType.INSERT;
      case EQUAL:
        return DeltaType.EQUAL;
      default:
        throw new RuntimeException();
    }
  }

  @Getter
  private static class DiffChunk {

    private final String source;
    private final String target;
    private final DeltaType deltaType;

    public DiffChunk(String source, String target, DeltaType deltaType) {
      this.source = source;
      this.target = target;
      this.deltaType = deltaType;
    }

    public enum DeltaType {
      CHANGE,
      DELETE,
      INSERT,
      EQUAL
    }
  }
}
