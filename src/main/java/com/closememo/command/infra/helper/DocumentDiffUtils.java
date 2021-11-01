package com.closememo.command.infra.helper;

import com.closememo.command.domain.difference.DeltaType;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.difference.LineDelta.LineChunk;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDiffUtils {

  public static List<LineDelta> getLineChanges(String before, String after) {

    String[] beforeLines = before.split("\n");
    String[] afterLines = after.split("\n");

    BidiMap<String, Long> beforeHashedLineBidiMap = getHashedLineBidiMap(beforeLines);
    BidiMap<String, Long> afterHashedLineBidiMap = getHashedLineBidiMap(afterLines);

    List<Long> beforeHashedLines = Arrays.stream(beforeLines)
        .map(beforeHashedLineBidiMap::get).collect(Collectors.toList());
    List<Long> afterHashedLines = Arrays.stream(afterLines)
        .map(afterHashedLineBidiMap::get).collect(Collectors.toList());

    Patch<Long> patch = DiffUtils.diff(beforeHashedLines, afterHashedLines);
    List<AbstractDelta<Long>> abstractDeltas = patch.getDeltas();

    return abstractDeltas.stream()
        .map(abstractDelta ->
            convert(abstractDelta, beforeHashedLineBidiMap, afterHashedLineBidiMap))
        .collect(Collectors.toList());
  }

  private static BidiMap<String, Long> getHashedLineBidiMap(String[] lines) {
    return Arrays.stream(lines)
        .collect(Collectors.toMap(
            Function.identity(),
            DocumentDiffUtils::getCRC32,
            (a, b) -> a,
            DualHashBidiMap::new));
  }

  private static long getCRC32(String str) {
    CRC32 crc32 = new CRC32();
    crc32.update(str.getBytes(StandardCharsets.UTF_8));
    return crc32.getValue();
  }

  private static LineDelta convert(AbstractDelta<Long> abstractDelta,
      BidiMap<String, Long> beforeHashedLineBidiMap, BidiMap<String, Long> afterHashedLineBidiMap) {
    DeltaType type = convert(abstractDelta.getType());
    LineChunk source = convert(beforeHashedLineBidiMap, abstractDelta.getSource());
    LineChunk target = convert(afterHashedLineBidiMap, abstractDelta.getTarget());

    return new LineDelta(type, source, target);
  }

  private static DeltaType convert(com.github.difflib.patch.DeltaType deltaType) {
    switch (deltaType) {
      case CHANGE:
        return DeltaType.CHANGE;
      case DELETE:
        return DeltaType.DELETE;
      case INSERT:
        return DeltaType.INSERT;
      case EQUAL:
        return DeltaType.EQUAL;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static LineChunk convert(BidiMap<String, Long> lineMap, Chunk<Long> chunk) {
    int position = chunk.getPosition();

    List<String> lines = chunk.getLines().stream()
        .map(lineMap::getKey)
        .collect(Collectors.toList());

    return new LineChunk(position, lines);
  }
}
