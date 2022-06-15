package com.closememo.command.infra.diff;

import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.difference.LineDelta.ChangePatch;
import com.closememo.command.domain.difference.LineDelta.Line;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.EqualDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class DocumentDiffUtils {

  private static final String NEW_LINE = "\n";
  private static final Pattern WORD_PATTERN =
      Pattern.compile("[_\\dA-Za-zㄱ-ㆎ가-힣ힰ-ퟆퟋ-ퟻＡ-Ｚａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ]+");
  private static final int PADDING = 1;

  public static List<ChangePatch> getWordChanges(@NonNull String before, @NonNull String after) {
    List<String> beforeWords = getWords(before);
    List<String> afterWords = getWords(after);

    Patch<String> patch = DiffUtils.diff(beforeWords, afterWords, true);
    List<AbstractDelta<String>> deltas = patch.getDeltas();

    return ChangePatchGenerator.generateWordDeltaList(deltas);
  }

  public static List<LineDelta> getLineChanges(String before, String after) {
    // 둘다 비어 있으면 바로 반환
    if (StringUtils.isBlank(before) && StringUtils.isBlank(after)) {
      return Collections.emptyList();
    }
    // 한 쪽만 비어있으면 로직 탈 필요 없이 '\n' 로 쪼개서 반환.
    // 여기서 따로 return 하지 않고 아래쪽 로직 타면 결과가 좀 이상해짐.
    if (StringUtils.isBlank(before)) {
      List<LineDelta> lineDeltas = new ArrayList<>();
      String[] targetChunks = after.split("\n");
      for (int i = 0; i < targetChunks.length; i++) {
        lineDeltas.add(new LineDelta(
            com.closememo.command.domain.difference.DeltaType.INSERT,
            new Line(0, null),
            new Line(i, targetChunks[i])
        ));
      }
      return lineDeltas;
    } else if (StringUtils.isBlank(after)) {
      List<LineDelta> lineDeltas = new ArrayList<>();
      String[] targetChunks = before.split("\n");
      for (int i = 0; i < targetChunks.length; i++) {
        lineDeltas.add(new LineDelta(
            com.closememo.command.domain.difference.DeltaType.DELETE,
            new Line(i, targetChunks[i]),
            new Line(0, null)
        ));
      }
      return lineDeltas;
    }

    // 끝에 NEW_LINE 을 추가하지 않으면 LineDeltaGenerator 에서 기대했던 대로 라인이 생성되지 않음
    List<String> beforeWords = getWords(before + NEW_LINE);
    List<String> afterWords = getWords(after + NEW_LINE);

    Patch<String> patch = DiffUtils.diff(beforeWords, afterWords, true);
    List<AbstractDelta<String>> deltas = patch.getDeltas();
    List<AbstractDelta<String>> adjusted = getAdjusted(deltas);

    List<LineDelta> lineDeltas = LineDeltaGenerator.generateLineDeltaList(adjusted);

    // PADDING 에 따라 필터링
    return filter(lineDeltas);
  }

  /**
   * 라인에 걸쳐서 나타나는 같은 문자열(특히 공백)에 의해 라인별로 분리되지 않는 것을 보정.
   * 보정이 없으면 깔끔하게 안나옴.
   * EX)
   * 보정 전: [{EQUAL, [" "]}, {INSERT, ["A", "B", " "]}, {EQUAL, ["C"]}]
   * 보정 후: [{INSERT, [" ", "A", "B"}, {EQUAL, [" ", "C"]}]
   *
   * TODO: 아래 참고
   * 그런데, 꼭 이걸 해야 할 지 모르겠다. 이거 없이 돌려도 라인별 구분은 정상적으로 되긴 한다.
   * 타입이 기대한 대로 안나오는 것이 있긴 한데, 일단 라인별로 쪼개고 나서 직접 비교하면 될 것 같음.
   */
  private static List<AbstractDelta<String>> getAdjusted(List<AbstractDelta<String>> orig) {
    List<AbstractDelta<String>> adjusted = adjustDeltas(orig);
    List<AbstractDelta<String>> result = new ArrayList<>(adjusted.size());

    for (int i = 0; i < adjusted.size(); i++) {
      AbstractDelta<String> current = adjusted.get(i);
      // 다음, 다다음 delta 가 없으면 해당하지 않음
      if (i >= adjusted.size() - 2) {
        result.add(current);
        continue;
      }
      AbstractDelta<String> next = adjusted.get(i + 1);
      AbstractDelta<String> afterNext = adjusted.get(i + 2);

      // 조건: 현재와 다다음 delta 가 EQUAL 이고, 다음 delta 가 INSERT 나 DELETE 인 경우(CHANGE 제외)
      boolean condition = current.getType().equals(DeltaType.EQUAL)
          && (next.getType().equals(DeltaType.INSERT) || next.getType().equals(DeltaType.DELETE))
          && afterNext.getType().equals(DeltaType.EQUAL);
      if (!condition) {
        result.add(current);
        continue;
      }

      // 현재 delta 의 마지막 문자가 NewLind('\n') 이면 해당하지 않음
      if (isLastWordNewLine(current)) {
        result.add(current);
        continue;
      }

      List<String> currentWords = current.getSource().getLines();
      List<String> nextWords = next.getTarget().getLines();
      List<String> afterNextWords = afterNext.getSource().getLines();

      int currentLength = currentWords.size();
      int nextLength = nextWords.size();

      if (currentLength > nextLength) {
        result.add(current);
        continue;
      }

      List<String> nextWordsWithoutLast = nextWords.subList(0, nextLength - currentLength);
      List<String> lastWordsInWords1 = nextWords.subList(nextLength - currentLength, nextLength);

      if (!currentWords.equals(lastWordsInWords1)) {
        result.add(current);
        continue;
      }

      // 여기서부터 adjusting
      int sourcePosition = current.getSource().getPosition();
      int targetPosition = current.getTarget().getPosition();
      List<String> newWords = ListUtils.union(currentWords, nextWordsWithoutLast);

      Chunk<String> sourceChunk;
      Chunk<String> targetChunk;
      if (next.getType().equals(DeltaType.INSERT)) {
        sourceChunk = new Chunk<>(sourcePosition, ArrayUtils.EMPTY_STRING_ARRAY);
        targetChunk = new Chunk<>(targetPosition, newWords);
        result.add(new InsertDelta<>(sourceChunk, targetChunk));
      } else {
        sourceChunk = new Chunk<>(sourcePosition, newWords);
        targetChunk = new Chunk<>(targetPosition, ArrayUtils.EMPTY_STRING_ARRAY);
        result.add(new DeleteDelta<>(sourceChunk, targetChunk));
      }

      int nextSourcePosition = sourcePosition + sourceChunk.size();
      int nextTargetPosition = targetPosition + targetChunk.size();
      List<String> newWords2 = ListUtils.union(currentWords, afterNextWords);

      Chunk<String> nextSourceChunk = new Chunk<>(nextSourcePosition, newWords2);
      Chunk<String> nextTargetChunk = new Chunk<>(nextTargetPosition, newWords2);

      result.add(new EqualDelta<>(nextSourceChunk, nextTargetChunk));
      i += 2;
    }

    return result;
  }

  /**
   * EqualDelta 의 경우 조건에 맞게 쪼개서 붙여 보정한 후 반환.
   */
  private static List<AbstractDelta<String>> adjustDeltas(List<AbstractDelta<String>> deltas) {
    List<AbstractDelta<String>> result = new ArrayList<>();
    for (AbstractDelta<String> delta : deltas) {
      if (delta.getType().equals(DeltaType.EQUAL)) {
        result.addAll(splitEqualDelta((EqualDelta<String>) delta));
      } else {
        result.add(delta);
      }
    }
    return result;
  }

  /**
   * EqualDelta 의 경우 '\n' 의 단위로 쪼개어 리스트로 반환한다.
   * 예를 들어 ['ab', '\n', 'cd', '\n', 'ef']
   * [['ab', '\n'], ['cd', '\n'], ['ef']]
   * 이렇게 쪼개 줘야 getAdjusted() 에서 검사 조건에 맞게 됨.
   */
  private static List<AbstractDelta<String>> splitEqualDelta(EqualDelta<String> delta) {
    Chunk<String> chunk = delta.getSource();
    int sourcePosition = delta.getSource().getPosition();
    int targetPosition = delta.getTarget().getPosition();
    List<String> words = chunk.getLines();

    if (words.size() == 1) {
      return List.of(delta);
    }

    List<AbstractDelta<String>> deltas = new ArrayList<>();

    List<String> temp = new ArrayList<>();
    for (int i = 0; i < words.size(); i++) {
      String word = words.get(i);
      temp.add(word);

      if (StringUtils.equals(word, "\n") || i == words.size() - 1) {
        Chunk<String> sourceChunk = new Chunk<>(sourcePosition, temp);
        Chunk<String> targetChunk = new Chunk<>(targetPosition, temp);
        deltas.add(new EqualDelta<>(sourceChunk, targetChunk));

        int size = temp.size();
        sourcePosition += size;
        targetPosition += size;
        temp = new ArrayList<>();
      }
    }

    return deltas;
  }

  private static boolean isLastWordNewLine(AbstractDelta<String> delta) {
    Assert.isInstanceOf(EqualDelta.class, delta);
    int size = delta.getSource().size();
    String lastWord = delta.getSource().getLines().get(size - 1);
    return StringUtils.equals(lastWord, "\n");
  }

  /**
   * word 단위로 쪼개서 리스트로 반환한다. (WORD_PATTERN) WORD_PATTERN 에 해당하지 않는 경우 문자 하나를 word 라고 판단한다. ex)
   * "alpha\n  beta" => ['alpha', '\n', ' ', ' ', 'beta']
   */
  private static List<String> getWords(String orig) {
    Matcher matcher = WORD_PATTERN.matcher(orig);

    List<String> words = new ArrayList<>();
    int cursor = 0;

    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();

      while (cursor < start) {
        words.add(orig.substring(cursor, cursor + 1));
        cursor += 1;
      }

      String word = orig.substring(matcher.start(), matcher.end());
      words.add(word);
      cursor = end;
    }
    // 마지막 word 이후 처리
    while (cursor < orig.length()) {
      words.add(orig.substring(cursor, cursor + 1));
      cursor += 1;
    }
    return words;
  }

  /**
   * EQUAL 의 경우 기본적으로 필요가 없어서 제거하지만,
   * EQUAL 이 아닌 Delta 의 앞뒤로 존재하는 EQUAL 은 PADDING 만큼 저장한다.
   */
  private static List<LineDelta> filter(List<LineDelta> orig) {
    List<LineDelta> result = new ArrayList<>();

    for (int i = 0; i < orig.size(); i++) {
      LineDelta lineDelta = orig.get(i);
      // EQUAL 이 아니면 바로 추가
      if (!lineDelta.getType().equals(com.closememo.command.domain.difference.DeltaType.EQUAL)) {
        result.add(lineDelta);
        continue;
      }
      // PADDING 이 0 이면 EQUAL 을 추가하지 않음
      if (PADDING == 0) {
        continue;
      }
      // 앞뒤로 PADDING 만큼 스캔하여 EQUAL 이 아닌 Delta 가 있는지 체크
      int start = Math.max(0, i - PADDING);
      int end = Math.min(i + PADDING, orig.size() - 1);
      for (int j = start; j <= end; j++) {
        if (i == j) {
          continue;
        }
        LineDelta temp = orig.get(j);
        if (!temp.getType().equals(com.closememo.command.domain.difference.DeltaType.EQUAL)) {
          result.add(lineDelta);
          break;
        }
      }
    }

    return result;
  }
}
