package es.zed.shared.domain.ports.inbound;

@FunctionalInterface
public interface NoResponseUseCase<I> {
  void execute(I input);
}