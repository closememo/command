package com.closememo.command.infra.messageing.listener;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.bookmark.DeleteBookmarkCommand;
import com.closememo.command.application.bookmark.RearrangeBookmarkOrderCommand;
import com.closememo.command.domain.bookmark.BookmarkDeletedEvent;
import com.closememo.command.domain.bookmark.BookmarkRepository;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BookmarkEventListener {

  private final BookmarkRepository bookmarkRepository;
  private final MessagePublisher messagePublisher;

  public BookmarkEventListener(
      BookmarkRepository bookmarkRepository,
      MessagePublisher messagePublisher) {
    this.bookmarkRepository = bookmarkRepository;
    this.messagePublisher = messagePublisher;
  }

  @ServiceActivator(inputChannel = "BookmarkDeletedEvent")
  @Transactional
  public void handle(BookmarkDeletedEvent payload) {
    RearrangeBookmarkOrderCommand command = new RearrangeBookmarkOrderCommand(
        SystemCommandRequester.getInstance(), payload.getOwnerId());
    messagePublisher.publish(command);
  }

  @ServiceActivator(inputChannel = "DocumentDeletedEvent")
  @Transactional
  public void handle(DocumentDeletedEvent payload) {
    bookmarkRepository.findByDocumentId(payload.getDocumentId())
        .ifPresent(bookmark -> messagePublisher.publish(
            new DeleteBookmarkCommand(SystemCommandRequester.getInstance(), bookmark.getDocumentId())));
  }
}
