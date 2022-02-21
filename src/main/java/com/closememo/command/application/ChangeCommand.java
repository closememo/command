package com.closememo.command.application;

import com.closememo.command.domain.Identifier;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * 수정이나 삭제를 하는 경우 이 class 를 상속받아 사용한다.
 * multi-thread 환경에서 같은 대상에 대한 수정/삭제 요청이 연속적으로 발생할 때,
 * "@Transactional" 어노테이션으로도 동기화 처리가 되지 않는다.
 * 별도의 lock 을 이용해 동기화 처리를 하려고 하는데, 각 Command 를 구분할 필요가 있어서 만들었다.
 * 수정/삭제를 할 대상의 id 를 파라미터로 받는다.
 * 파라미터로 받은 값의 hash 로 lock 을 구별한다. (같은 hash 를 가진 Command 의 경우 lock 으로 동기화)
 */
@Getter
public class ChangeCommand<T extends Identifier> extends Command {

  private final int hash;

  protected ChangeCommand(CommandRequester requester, T targetId) {
    super(requester);
    this.hash = targetId.getId().hashCode();
  }

  protected ChangeCommand(CommandRequester requester, Collection<T> targetIds) {
    super(requester);
    Collection<String> ids = targetIds.stream()
        .map(Identifier::getId)
        .collect(Collectors.toList());
    hash = hashCode(ids);
  }

  private static int hashCode(Collection<String> strings) {
    if (strings == null) {
      return 0;
    }
    int result = 1;
    for (String str : strings) {
      result = 31 * result + ((str == null) ? 0 : str.hashCode());
    }
    return result;
  }
}
