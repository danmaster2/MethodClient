package Method.Client.utils.Patcher.Events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventBookPage extends Event {


    private String page;

    public EventBookPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

}
