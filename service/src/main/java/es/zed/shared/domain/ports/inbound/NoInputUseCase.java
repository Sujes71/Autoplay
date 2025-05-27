package es.zed.shared.domain.ports.inbound;

@FunctionalInterface
public interface NoInputUseCase<R> {
  R execute();
}