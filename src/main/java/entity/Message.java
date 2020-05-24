package entity;

import java.time.LocalDateTime;

public class Message {
  int id;
  int from;
  int to;
  String text;
  LocalDateTime sentTime;

  public Message(int id,int from, int to, String text, LocalDateTime sentTime) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.text = text;
    this.sentTime = sentTime;
  }

  public int getFrom() {
    return from;
  }

  public String getText() {
    return text;
  }

  public LocalDateTime getSentTime() {
    return sentTime;
  }

  public int getId() {
    return id;
  }

  public int getTo() {
    return to;
  }
}
