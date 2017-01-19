package org.perfrepo.web.model.to;

import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

/**
 * This is wrapper class for communication over REST to allow syntax
 * like <tags><tag></tag><tag></tag></tags>
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ListWrapper<T> {

    private List<T> items;

    @XmlAnyElement(lax = true)
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
