package es.zed.shared.domain.ports.outbound;

import es.zed.shared.domain.model.Message;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OutboundPort {

  private static final Map<String, EventHandler<?, ?>> eventBus = new ConcurrentHashMap<>();

  private OutboundPort() {
  }

  public static <B, R> Mono<R> requestEvent(Message<B> event) {
    EventHandler<B, R> handler = resolve(event.address());
    if (handler == null) {
      return Mono.error(new IllegalArgumentException("No handler found for address: " + event.address()));
    }
    return handler.handle(event.body());
  }

  public static <B, R> void register(String address, EventHandler<B, R> handler) {
    eventBus.put(address, handler);
  }

  @SuppressWarnings("unchecked")
  private static <B, R> EventHandler<B, R> resolve(String address) {
    return (EventHandler<B, R>) eventBus.get(address);
  }

  @FunctionalInterface
  public interface EventHandler<B, R> {
    Mono<R> handle(B body);
  }
}