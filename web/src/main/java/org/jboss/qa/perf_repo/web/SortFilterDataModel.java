package org.jboss.qa.perf_repo.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.DataModel;

public class SortFilterDataModel<E> extends DataModel<E> {

   private DataModel<E> model;

   private List<Integer> rows;

   public SortFilterDataModel() {
      setWrappedData(null);
   }

   public SortFilterDataModel(List<E> data) {
      setWrappedData(data);
   }

   public SortFilterDataModel(DataModel<E> model) {
      this.model = model;
      initRows();
   }

   private void initRows() {
      int rowCount = model.getRowCount();
      if (rowCount != -1) {
         rows = new ArrayList<Integer>(rowCount);
         for (int i = 0; i < rowCount; i++) {
            rows.set(i, i);
         }
      }
   }

   private E getData(int row) {
      int origIdx = model.getRowIndex();
      model.setRowIndex(row);
      E rowData = model.getRowData();
      model.setRowIndex(origIdx);
      return rowData;
   }

   public void sortBy(final Comparator<E> dataComparator) {
      Comparator<Integer> rowComp = new Comparator<Integer>() {

         @Override
         public int compare(Integer o1, Integer o2) {
            E e1 = getData(o1);
            E e2 = getData(o2);
            return dataComparator.compare(e1, e2);
         }
      };
      Collections.sort(rows, rowComp);
   }

   @Override
   public boolean isRowAvailable() {
      return model.isRowAvailable();
   }

   @Override
   public int getRowCount() {
      return model.getRowCount();
   }

   @Override
   public E getRowData() {
      return model.getRowData();
   }

   @Override
   public int getRowIndex() {
      return model.getRowIndex();
   }

   @Override
   public void setRowIndex(int rowIndex) {
      if (0 <= rowIndex && rowIndex < rows.size()) {
         model.setRowIndex(rows.get(rowIndex));
      } else {
         model.setRowIndex(rowIndex);
      }

   }

   @Override
   public Object getWrappedData() {
      return model.getWrappedData();
   }

   @Override
   public void setWrappedData(Object data) {
      model.setWrappedData(data);
      initRows();
   }

}
