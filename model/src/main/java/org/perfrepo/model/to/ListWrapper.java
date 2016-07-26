package org.perfrepo.model.to;

import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

/**
 * TODO: document this
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
