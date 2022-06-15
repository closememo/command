package com.closememo.command.infra.diff;

import static org.junit.jupiter.api.Assertions.*;

import com.closememo.command.domain.difference.DeltaType;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.difference.LineDelta.ChangePatch;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentDiffUtilsTest {

  private void printLineInfos(String str1, String str2, List<LineDelta> lineDeltas) {
    System.out.println("==================================================");
    System.out.println("str1=" + str1);
    System.out.println("str2=" + str2);
    for (LineDelta lineDelta : lineDeltas) {
      System.out.println("-----");
      System.out.println("TYPE=" + lineDelta.getType());
      System.out.println("source=" + lineDelta.getSource());
      System.out.println("target=" + lineDelta.getTarget());
      System.out.println("patches=");
      for (ChangePatch changePatch : lineDelta.getChangePatches()) {
        System.out.println("  " + changePatch);
      }
    }
    System.out.println("==================================================");
  }

  @Test
  public void test1() {
    String str1 = "            news_type, title, content, posted_date = html_reader.get_news_info(url)\n";
    String str2 = "            report_status, http_status, news_type, title, content, posted_date = html_reader.get_news_info(url)\n";
    List<LineDelta> lineDeltas = DocumentDiffUtils.getLineChanges(str1, str2);

    printLineInfos(str1, str2, lineDeltas);

    assertEquals(lineDeltas.size(), 1);
    LineDelta firstLineDelta = lineDeltas.get(0);
    assertEquals(firstLineDelta.getType(), DeltaType.CHANGE);
  }

  @Test
  public void test2() {
    String str1 = "for url in url_list:\n"
        + "news_type, title, content, posted_date = html_reader.get_news_info(url)";
    String str2 = "for url in url_list:\n"
        + "            report_status, http_status, news_type, title, content, posted_date = html_reader.get_news_info(url)";
    List<LineDelta> lineDeltas = DocumentDiffUtils.getLineChanges(str1, str2);

    printLineInfos(str1, str2, lineDeltas);

    assertEquals(lineDeltas.size(), 1);
    LineDelta firstLineDelta = lineDeltas.get(0);
    assertEquals(firstLineDelta.getType(), DeltaType.CHANGE);
  }
}
