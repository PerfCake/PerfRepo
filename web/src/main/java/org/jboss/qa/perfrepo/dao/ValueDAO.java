package org.jboss.qa.perfrepo.dao;

import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Value;

@Named
public class ValueDAO extends DAO<Value, Long> {

    public Value getValue(Long id) {
       return findWithDepth(id, "parameters");
    }

}