package com.newpick4u.user.application.message.producer;

import com.newpick4u.user.application.message.request.PointRequestFailureMessage;

public interface PointRequestFailureProducer {

  void produce(PointRequestFailureMessage message);

}
