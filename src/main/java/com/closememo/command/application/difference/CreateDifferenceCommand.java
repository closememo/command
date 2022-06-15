package com.closememo.command.application.difference;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateDifferenceCommand extends Command {

  private final AccountId ownerId;
  private final DocumentId documentId;
  private final long documentVersion;
  private final List<LineDelta> lineDeltas;

  public CreateDifferenceCommand(CommandRequester requester, AccountId ownerId,
      DocumentId documentId, long documentVersion, List<LineDelta> lineDeltas) {
    super(requester);
    this.ownerId = ownerId;
    this.documentId = documentId;
    this.documentVersion = documentVersion;
    this.lineDeltas = lineDeltas;
  }
}
