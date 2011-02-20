package ca.xshade.bukkit.xshade;


public class RemovePortalTask implements Runnable {
	PortalPlayerListener listener;
	Portal portal;
	
	
	public RemovePortalTask(PortalPlayerListener listener, Portal portal) {
		this.listener = listener;
		this.portal = portal.newInstance();
	}
	public void run() {
		try {
			listener.destroyPortal(portal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
