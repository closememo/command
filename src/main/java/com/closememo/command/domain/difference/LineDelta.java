package com.closememo.command.domain.difference;

import com.closememo.command.domain.ValueObject;
import com.closememo.command.infra.diff.DocumentDiffUtils;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class LineDelta implements ValueObject {

  private static final long serialVersionUID = -6312614134094358625L;

  private DeltaType type;
  private Line source;
  private Line target;
  private List<ChangePatch> changePatches;

  public LineDelta(DeltaType type,
      Line source, Line target) {

    this.type = type;
    this.source = source;
    this.target = target;

    String sourceValue = source.getValue();
    String targetValue = target.getValue();
    if (type.equals(DeltaType.CHANGE)) {
      this.changePatches = DocumentDiffUtils.getWordChanges(sourceValue, targetValue);
    } else {
      this.changePatches = Collections.emptyList();
    }
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @ToString
  public static class Line {

    private int position;
    private String value;

    public Line(int position, String value) {
      this.position = position;
      this.value = value;
    }
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @ToString
  public static class ChangePatch {

    private DeltaType type;
    private String value;
    private String changed;

    public ChangePatch(DeltaType type, String value) {
      this(type, value, null);
    }

    public ChangePatch(DeltaType type, String value, String changed) {
      this.type = type;
      this.value = value;
      this.changed = changed;
    }
  }
}
