package com.closememo.command.domain.difference;

import com.closememo.command.domain.ValueObject;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LineDelta implements ValueObject {

  private static final long serialVersionUID = 2249134511000417124L;

  private DeltaType deltaType;
  private LineChunk source;
  private LineChunk target;

  public LineDelta(DeltaType deltaType,
      LineChunk source, LineChunk target) {
    this.deltaType = deltaType;
    this.source = source;
    this.target = target;
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LineChunk {
    int position;
    List<String> lines;

    public LineChunk(int position, List<String> lines) {
      this.position = position;
      this.lines = lines;
    }
  }
}
