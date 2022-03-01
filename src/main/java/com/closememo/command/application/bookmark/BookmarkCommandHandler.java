package com.closememo.command.application.bookmark;

import com.closememo.command.application.Command;
import com.closememo.command.application.Success;
import com.closememo.command.domain.AccessDeniedException;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.bookmark.Bookmark;
import com.closememo.command.domain.bookmark.BookmarkId;
import com.closememo.command.domain.bookmark.BookmarkNotFoundException;
import com.closememo.command.domain.bookmark.BookmarkRepository;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentNotFoundException;
import com.closememo.command.domain.document.DocumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkCommandHandler {

  private final BookmarkRepository bookmarkRepository;
  private final DocumentRepository documentRepository;

  public BookmarkCommandHandler(
      BookmarkRepository bookmarkRepository,
      DocumentRepository documentRepository) {
    this.bookmarkRepository = bookmarkRepository;
    this.documentRepository = documentRepository;
  }

  @ServiceActivator(inputChannel = "CreateBookmarkCommand")
  @Transactional
  public BookmarkId handle(CreateBookmarkCommand command) {
    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);
    checkAuthority(command, document.getOwnerId());

    Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository,
        document.getOwnerId(), document.getId());

    Bookmark savedBookmark = bookmarkRepository.save(bookmark);
    return savedBookmark.getId();
  }

  @ServiceActivator(inputChannel = "DeleteBookmarkCommand")
  @Transactional
  public Success handle(DeleteBookmarkCommand command) {
    Bookmark bookmark = bookmarkRepository.findByDocumentId(command.getDocumentId())
        .orElseThrow(BookmarkNotFoundException::new);
    checkAuthority(command, bookmark.getOwnerId());

    bookmark.delete();
    bookmarkRepository.delete(bookmark);

    return Success.getInstance();
  }

  @ServiceActivator(inputChannel = "RearrangeBookmarkOrderCommand")
  @Transactional
  public Success handle(RearrangeBookmarkOrderCommand command) {
    List<Bookmark> bookmarks = bookmarkRepository.findAllByOwnerId(command.getOwnerId())
        .collect(Collectors.toList());

    for (int i = 0; i < bookmarks.size(); i++) {
      Bookmark bookmark = bookmarks.get(i);
      bookmark.update(i + 1);
      bookmarkRepository.save(bookmark);
    }

    return Success.getInstance();
  }

  private static void checkAuthority(Command command, AccountId ownerId) {
    if (command.isReliableRequester()) {
      return;
    }

    if (!command.equalsAccountRequester(ownerId)) {
      throw new AccessDeniedException();
    }
  }
}
