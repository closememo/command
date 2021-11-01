package com.closememo.command.infra.sequencegenerator;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObjectId {

  private static final int OBJECT_ID_LENGTH = 12;

  private static final int RANDOM_VALUE;

  private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

  private static final char[] HEX_CHARS = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };

  static {
    try {
      SecureRandom secureRandom = new SecureRandom();
      RANDOM_VALUE = secureRandom.nextInt();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final int timestamp;
  private final int counter;
  private final int randomValue;

  public ObjectId() {
    this(new Date());
  }

  public ObjectId(Date date) {
    this(dateToTimestampSeconds(date), NEXT_COUNTER.getAndIncrement());
  }

  public ObjectId(int timestamp, int counter) {
    this(timestamp, RANDOM_VALUE, counter);
  }

  public ObjectId(int timestamp, int randomValue, int counter) {
    this.timestamp = timestamp;
    this.counter = counter;
    this.randomValue = randomValue;
  }

  private static int dateToTimestampSeconds(Date date) {
    return (int) (date.getTime() / 1000);
  }

  private static byte int3(int x) {
    return (byte) (x >> 24);
  }

  private static byte int2(int x) {
    return (byte) (x >> 16);
  }

  private static byte int1(int x) {
    return (byte) (x >> 8);
  }

  private static byte int0(int x) {
    return (byte) (x);
  }

  public byte[] toByteArray() {
    ByteBuffer buffer = ByteBuffer.allocate(OBJECT_ID_LENGTH);
    putToByteBuffer(buffer);
    return buffer.array();
  }

  private void putToByteBuffer(ByteBuffer buffer) {
    buffer.put(int3(timestamp));
    buffer.put(int2(timestamp));
    buffer.put(int1(timestamp));
    buffer.put(int0(timestamp));
    buffer.put(int3(randomValue));
    buffer.put(int2(randomValue));
    buffer.put(int1(randomValue));
    buffer.put(int0(randomValue));
    buffer.put(int2(counter));
    buffer.put(int2(counter));
    buffer.put(int1(counter));
    buffer.put(int0(counter));
  }

  public String toHexString() {
    char[] chars = new char[OBJECT_ID_LENGTH * 2];
    int i = 0;
    for (byte b : toByteArray()) {
      chars[i++] = HEX_CHARS[b >> 4 & 0xF];
      chars[i++] = HEX_CHARS[b & 0xF];
    }
    return new String(chars);
  }
}
