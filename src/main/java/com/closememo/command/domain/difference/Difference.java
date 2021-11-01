package com.closememo.command.domain.difference;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.converters.LineChangesConverter;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "differences")
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Difference {

  @EmbeddedId
  private DifferenceId id;
  @AttributeOverride(name = "id", column = @Column(name = "documentId", nullable = false))
  private DocumentId documentId;
  @Column(nullable = false)
  private long documentVersion;
  @Column(nullable = false, columnDefinition = "JSON")
  @Convert(converter = LineChangesConverter.class)
  private List<LineDelta> lineDeltas;
  @Column(nullable = false)
  private ZonedDateTime createdAt;

  public Difference(DifferenceId id, DocumentId documentId, long documentVersion,
      List<LineDelta> lineDeltas, ZonedDateTime createdAt) {
    this.id = id;
    this.documentId = documentId;
    this.documentVersion = documentVersion;
    this.lineDeltas = lineDeltas;
    this.createdAt = createdAt;
  }

  public static Difference newOne(DifferenceId id, DocumentId documentId,
      long documentVersion, List<LineDelta> lineDeltas) {

    ZonedDateTime createdAt = ZonedDateTime.now();

    Events.register(new DifferenceCreatedEvent(id, documentId, documentVersion, lineDeltas, createdAt));
    return new Difference(id, documentId, documentVersion, lineDeltas, createdAt);
  }

  public void delete() {
    Events.register(new DifferenceDeletedEvent(this.id));
  }
}
