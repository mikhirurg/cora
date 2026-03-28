/**************************************************************************************************
 Copyright 2023--2024 Cynthia Kop

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the License for the specific language governing permissions and limitations under the License.
 *************************************************************************************************/

package cora.reduction;

import charlie.terms.Term;
import charlie.terms.FunctionSymbol;
import charlie.theorytranslation.TermAnalyser;
import cora.config.Settings;

import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** This class implements the calculation rule scheme. */
public class CalcReducer implements ReduceObject {

  public static final AtomicIntegerArray MEMORY = new AtomicIntegerArray(Settings.getMemMaxSize());

  private static final Random random = new Random();

  static {
    resetMemory();
  }

  public static Integer GET(int addr) {
    if (addr < 0 || addr >= Settings.getMemMaxSize()) {
      return null;
    }
    return MEMORY.get(addr);
  }

  public static boolean SET(int addr, int val) {
    if (addr >= 0 && addr < Settings.getMemMaxSize()) {
      MEMORY.set(addr, val);
      return true;
    } else {
      return false;
    }
  }

  public static void resetMemory() {
    for (int i = 0; i < Settings.getMemMaxSize(); i++) {
      int randVal = random.nextInt();
      MEMORY.set(i, randVal);
    }
    MEMORY.set(0, 2);
    MEMORY.set(1, 0);
  }

  public boolean applicable(Term t) {
    if (!t.queryType().isBaseType() || !t.queryType().isTheoryType()) return false;
    if (!t.isFunctionalTerm()) return false;
    FunctionSymbol root = t.queryRoot();
    if (root == null || !root.isTheorySymbol() || root.isValue()) return false;
    for (int i = 1; i <= t.numberArguments(); i++) {
      if (!t.queryArgument(i).isValue()) return false;
    }
    return true;
  }

  public Term apply(Term t) {
    if (!t.queryType().isBaseType() || !t.queryType().isTheoryType()) return null;
    if (t.isValue() || !t.isGround() || !t.isTheoryTerm()) return null;
    return TermAnalyser.calculate(t);
  }

  public String toString() {
    return "calc : f(x1,...,xk) → y [f(x1,...,xk) = y] for f ∈ Σ_{theory}";
  }
}

