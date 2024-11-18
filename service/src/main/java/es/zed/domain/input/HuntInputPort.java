package es.zed.domain.input;

import es.zed.domain.output.request.HuntRequestBody;

public interface HuntInputPort {

  void start(final String name, final HuntRequestBody requestBody);

  void stop();
}
