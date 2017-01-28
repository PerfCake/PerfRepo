package org.perfrepo.web.dao.search;

/**
 * Abstraction for atom for parameters.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ParameterTerm implements Expression {

    private String name;
    private String value;

    public ParameterTerm(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ParameterTerm{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
