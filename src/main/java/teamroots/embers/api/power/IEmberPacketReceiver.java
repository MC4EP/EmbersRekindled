package teamroots.embers.api.power;

import teamroots.embers.entity.EntityEmberPacket;

public interface IEmberPacketReceiver {
	boolean isFull();
	boolean isEmpty();
	boolean onReceive(EntityEmberPacket packet);
}
