package me.melontini.handytests.server;

public interface ServerTestEntrypoint {
  default void onServerTest(ServerTestContext context) {
    context.runAllForEntrypoint(this);
  }
}
