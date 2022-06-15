package com.closememo.command.infra.diff;

import com.closememo.command.domain.difference.DeltaType;
import com.closememo.command.domain.difference.LineDelta.ChangePatch;
import com.github.difflib.patch.AbstractDelta;
import java.util.ArrayList;
import java.util.List;

public class ChangePatchGenerator {

  public static List<ChangePatch> generateWordDeltaList(List<AbstractDelta<String>> deltas) {

    List<ChangePatch> changePatches = new ArrayList<>();
    for (AbstractDelta<String> delta : deltas) {
      String source = String.join("", delta.getSource().getLines());
      String target = String.join("", delta.getTarget().getLines());

      switch (delta.getType()) {
        case CHANGE:
          changePatches.add(new ChangePatch(DeltaType.CHANGE, source, target));
          break;
        case DELETE:
          changePatches.add(new ChangePatch(DeltaType.DELETE, source));
          break;
        case INSERT:
          changePatches.add(new ChangePatch(DeltaType.INSERT, target));
          break;
        case EQUAL:
          changePatches.add(new ChangePatch(DeltaType.EQUAL, source));
          break;
        default:
          throw new RuntimeException();
      }
    }

    return changePatches;
  }
}
