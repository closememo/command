package com.closememo.command.infra.diff;

import com.closememo.command.infra.diff.LineChunk.WordChunk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class LineChunkGenerator {

  private final List<LineChunk> lines;
  private int num;
  private boolean open;
  private LineChunk tempLine;

  public LineChunkGenerator() {
    this.lines = new ArrayList<>();
    this.num = 1;
    this.open = false;
    this.tempLine = null;
  }

  public void empty() {
    lines.add(LineChunk.emptyLineElement());
  }

  public void add(WordChunk chunk) {
    if (chunk.isEmpty()) {
      return;
    }

    if (StringUtils.equals(chunk.getWord(), "\n")) {
      if (open) {
        // 열려 있으면, 라인을 끝낸다는 의미이다. 닫는다.
        open = false;
        pushTemp();
      } else {
        // 닫혀 있으면, 한 줄 추가한다. 열지 않는다. temp 없이 바로 추가.
        lines.add(new LineChunk(num++, newWordChunkList(chunk)));
      }
      return;
    }

    // chunk 가 '\n' 이 아닌 경우
    // open 인 경우 열려 있는 라인에 추가한다.
    if (open) {
      // 앞에서 다른쪽 '\n' 에 의해 empty 가 추가되었지만 새로 chunk 가 들어 올 수 있다. 이경우 empty 는 덮어쓴다.
      if (tempLine.isEmpty()) {
        tempLine = new LineChunk(num++, newWordChunkList(chunk));
      } else {
        tempLine.getWordChunks().add(chunk);
      }
    } else {
      // 새 라인을 시작한다.
      open = true;
      pushTemp();
      tempLine = new LineChunk(num++, newWordChunkList(chunk));
    }
  }

  private void pushTemp() {
    if (tempLine != null) {
      lines.add(tempLine.copy());
      tempLine = null;
    }
  }

  private static List<WordChunk> newWordChunkList(WordChunk... chunks) {
    return new ArrayList<>(Arrays.asList(chunks));
  }

  public List<LineChunk> get() {
    pushTemp();
    return lines;
  }
}
