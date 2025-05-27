package es.zed.shared.domain.ports.inbound;

@FunctionalInterface
public interface NoInputNoResponseUseCase {
  void execute();
}