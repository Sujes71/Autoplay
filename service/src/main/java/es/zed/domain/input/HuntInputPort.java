package es.zed.domain.input;

import es.zed.domain.output.request.HuntRequestBody;

public interface HuntInputPort {

  void startHunt(final String name);

  void manageAction(final HuntRequestBody requestBody);

  void stopHunt();
}
