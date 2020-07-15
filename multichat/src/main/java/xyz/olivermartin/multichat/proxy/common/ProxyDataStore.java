package xyz.olivermartin.multichat.proxy.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;

/**
 * A proxy data store of settings and other things for MultiChatProxy
 * 
 * <p>These may be updated due to messages received from the local servers</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyDataStore {

	private boolean chatFrozen = false;

	private Map<UUID, TChatInfo> modChatPreferences = new HashMap<UUID, TChatInfo>();
	private Map<UUID, TChatInfo> adminChatPreferences = new HashMap<UUID, TChatInfo>();
	private Map<String, TGroupChatInfo> groupChats = new HashMap<String, TGroupChatInfo>();

	private Map<UUID, String> viewedChats = new HashMap<UUID, String>();
	private Map<UUID, UUID> lastMsg = new HashMap<UUID, UUID>();
	private List<UUID> allSpy = new ArrayList<UUID>();
	private List<UUID> socialSpy = new ArrayList<UUID>();

	public synchronized boolean isChatFrozen() {
		return this.chatFrozen;
	}

	/**
	 * @return the modchatpreferences
	 */
	public Map<UUID, TChatInfo> getModChatPreferences() {
		return modChatPreferences;
	}

	/**
	 * @return the adminchatpreferences
	 */
	public Map<UUID, TChatInfo> getAdminChatPreferences() {
		return adminChatPreferences;
	}

	/**
	 * @return the groupchats
	 */
	public Map<String, TGroupChatInfo> getGroupChats() {
		return groupChats;
	}

	/**
	 * @return the viewedchats
	 */
	public Map<UUID, String> getViewedChats() {
		return viewedChats;
	}

	/**
	 * @return the lastmsg
	 */
	public Map<UUID, UUID> getLastMsg() {
		return lastMsg;
	}

	/**
	 * @return the allspy
	 */
	public List<UUID> getAllSpy() {
		return allSpy;
	}

	/**
	 * @return the socialspy
	 */
	public List<UUID> getSocialSpy() {
		return socialSpy;
	}

	public synchronized void setChatFrozen(boolean frozen) {
		this.chatFrozen = frozen;
	}

	/**
	 * @param modChatPreferences the modchatpreferences to set
	 */
	public void setModChatPreferences(Map<UUID, TChatInfo> modChatPreferences) {
		this.modChatPreferences = modChatPreferences;
	}

	/**
	 * @param adminChatPreferences the adminchatpreferences to set
	 */
	public void setAdminChatPreferences(Map<UUID, TChatInfo> adminChatPreferences) {
		this.adminChatPreferences = adminChatPreferences;
	}

	/**
	 * @param groupChats the groupchats to set
	 */
	public void setGroupChats(Map<String, TGroupChatInfo> groupChats) {
		this.groupChats = groupChats;
	}

	/**
	 * @param viewedChats the viewedchats to set
	 */
	public void setViewedChats(Map<UUID, String> viewedChats) {
		this.viewedChats = viewedChats;
	}

	/**
	 * @param lastMsg the lastmsg to set
	 */
	public void setLastMsg(Map<UUID, UUID> lastMsg) {
		this.lastMsg = lastMsg;
	}

	/**
	 * @param allSpy the allspy to set
	 */
	public void setAllSpy(List<UUID> allSpy) {
		this.allSpy = allSpy;
	}

	/**
	 * @param socialSpy the socialspy to set
	 */
	public void setSocialSpy(List<UUID> socialSpy) {
		this.socialSpy = socialSpy;
	}

}
