package com.newpick4u.client.advertisement.application.message.producer;

import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;

public interface PointUpdateProducer {

  public void produce(PointRequestMessage message);
}
