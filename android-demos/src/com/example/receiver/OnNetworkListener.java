package com.example.receiver;

public interface OnNetworkListener {

	/**
	 * @brief ����������
	 * @param isWifi �Ƿ�Wifi����
	 */
	void onConnected(boolean isWifi);

	/**
	 * @brief ����Ͽ���
	 */
	void onDisconnected();
}