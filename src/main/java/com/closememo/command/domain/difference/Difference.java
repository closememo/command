package com.closememo.command.domain.difference;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.converters.LineChangesConverter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "differences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Difference {

  public static final int NUMBER_OF_DIFFERENCE_LIMIT = 10;

  @EmbeddedId
  private DifferenceId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @AttributeOverride(name = "id", column = @Column(name = "documentId", nullable = false))
  private DocumentId documentId;
  @Column(nullable = false)
  private long documentVersion;
  @Column(nullable = false, columnDefinition = "JSON")
  @Convert(converter = LineChangesConverter.class)
  private List<LineDelta> lineDeltas;
  @Column(nullable = false)
  private ZonedDateTime createdAt;

  private Difference(DifferenceId id, AccountId ownerId,
      DocumentId documentId, long documentVersion,
      List<LineDelta> lineDeltas, ZonedDateTime createdAt) {
    this.id = id;
    this.ownerId = ownerId;
    this.documentId = documentId;
    this.documentVersion = documentVersion;
    this.lineDeltas = lineDeltas;
    this.createdAt = createdAt;
  }

  public static Difference newOne(DifferenceRepository differenceRepository, AccountId ownerId,
      DocumentId documentId, long documentVersion, List<LineDelta> lineDeltas) {

    validateDifferenceLimit(differenceRepository, documentId);

    DifferenceId id = differenceRepository.nextId();
    ZonedDateTime createdAt = ZonedDateTime.now();

    Events.register(new DifferenceCreatedEvent(id, ownerId, documentId,
        documentVersion, lineDeltas, createdAt));
    return new Difference(id, ownerId, documentId, documentVersion, lineDeltas, createdAt);
  }

  public void delete() {
    Events.register(new DifferenceDeletedEvent(this.id, this.documentId));
  }

  private static void validateDifferenceLimit(DifferenceRepository differenceRepository,
      DocumentId documentId) {
    // NUMBER_OF_DIFFERENCE_LIMIT 을 넘어가면 후처리에서 제거하기 때문에 여기서는 '>' 로 체크
    if (differenceRepository.countByDocumentId(documentId) > NUMBER_OF_DIFFERENCE_LIMIT) {
      throw new DifferenceCountLimitExceededException(
          String.format("the number of differences cannot exceed %d", NUMBER_OF_DIFFERENCE_LIMIT));
    }
  }
}
