package org.perfrepo.web.alerting;

import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;

import java.util.List;

/**
 * Represent all available grouping function usable in alerting DSL.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public enum DslGroupingFunctions {

   AVG("AVG"), MAX("MAX"), MIN("MIN");

   private String stringRepresentation;

   DslGroupingFunctions(String stringRepresentation) {
      this.stringRepresentation = stringRepresentation;
   }

   /**
    * Compares the string representation of Enum with specified string.
    *
    * @param string
    * @return
    */
   public boolean equalsString(String string) {
      if (string == null) {
         return false;
      }

      return string.equalsIgnoreCase(stringRepresentation);
   }

   /**
    * Applies grouping function to values
    *
    * @param values
    * @return
    */
   public double compute(List<Double> values) {
      switch (this) {
         case AVG:
            return DoubleMath.mean(values);
         case MIN:
            return Doubles.min(Doubles.toArray(values));
         case MAX:
            return Doubles.max(Doubles.toArray(values));
      }

      throw new IllegalStateException("There should be associated a group function with the enum.");
   }

   /**
    * Decides if there is an enum with provided string representation.
    *
    * @param stringRepresentation
    * @return
    */
   public static boolean contains(String stringRepresentation) {
      for (DslGroupingFunctions e : values()) {
         if (e.equalsString(stringRepresentation)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Construct an enum from string
    *
    * @param string
    * @return
    */
   public static DslGroupingFunctions parseString(String string) {
      if (string != null) {
         for (DslGroupingFunctions value : DslGroupingFunctions.values()) {
            if (string.equalsIgnoreCase(value.stringRepresentation)) {
               return value;
            }
         }
      }

      throw new IllegalArgumentException("Unsupported grouping function.");
   }

}
