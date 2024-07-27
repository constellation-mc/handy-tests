package me.melontini.handytests.client;

public interface ClientTestEntrypoint {
  default void onClientTest(ClientTestContext context) {
    context.runAllForEntrypoint(this);
  }
}
