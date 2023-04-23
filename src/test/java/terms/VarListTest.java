/**************************************************************************************************
 Copyright 2023 Cynthia Kop

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the License for the specific language governing permissions and limitations under the License.
 *************************************************************************************************/

package cora.terms;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import cora.types.Type;
import cora.types.TypeFactory;

public class VarListTest {
  @Test
  public void testCreationWithThreeSimilarVariables() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Binder("x", TypeFactory.createSort("a"));
    ArrayList<Variable> vars = new ArrayList<Variable>();
    vars.add(x);
    vars.add(y);
    vars.add(x);
    VarList lst = new VarList(vars);
    assertTrue(lst.contains(x));
    assertTrue(lst.contains(y));
    assertTrue(lst.size() == 2);
    vars.add(TermFactory.createVar("z"));
    assertTrue(lst.size() == 2);
  }

  @Test
  public void testSingleCreation() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    VarList lst = new VarList(x);
    assertTrue(lst.contains(x));
    assertTrue(lst.size() == 1);
  }

  @Test
  public void testAdd() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Var("y", TypeFactory.createSort("a"));
    Variable z = new Var("z", TypeFactory.createSort("a"));
    VariableList lst1 = new VarList(x);
    VariableList lst2 = lst1.add(y);
    VariableList lst3 = lst2.add(z);
    assertTrue(lst1.size() == 1);
    assertTrue(lst2.size() == 2);
    assertTrue(lst3.size() == 3);
    assertTrue(lst2.contains(y));
    assertFalse(lst2.contains(z));
  }

  @Test
  public void testRemove() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Var("y", TypeFactory.createSort("a"));
    Variable z = new Var("z", TypeFactory.createSort("a"));
    ArrayList<Variable> vars = new ArrayList<Variable>();
    vars.add(x);
    vars.add(y);
    vars.add(z);
    VariableList lst3 = new VarList(vars);
    VariableList lst2 = lst3.remove(x);
    VariableList lst1 = lst2.remove(z);
    assertTrue(lst1.size() == 1);
    assertTrue(lst2.size() == 2);
    assertTrue(lst3.size() == 3);
    assertTrue(lst3.contains(y));
    assertFalse(lst2.contains(x));
  }

  @Test
  public void testCombineEquals() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Var("y", TypeFactory.createSort("a"));
    Variable z = new Var("z", TypeFactory.createSort("a"));
    ArrayList<Variable> vars = new ArrayList<Variable>();
    vars.add(x);
    vars.add(y);
    VariableList a = new VarList(vars);
    vars.add(z);
    VariableList b = new VarList(vars);
    assertTrue(a.combine(b) == b);
    assertTrue(b.combine(a) == b);
  }

  @Test
  public void testTrueCombination() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Var("y", TypeFactory.createSort("a"));
    Variable z = new Var("z", TypeFactory.createSort("a"));
    ArrayList<Variable> vars = new ArrayList<Variable>();
    vars.add(x);
    vars.add(y);
    VariableList a = new VarList(vars);
    vars.set(1, z);
    VariableList b = new VarList(vars);
    VariableList c = a.combine(b);
    assertTrue(a.size() == 2);
    assertTrue(b.size() == 2);
    assertTrue(c.size() == 3);
    assertTrue(c.contains(x));
    assertTrue(c.contains(y));
    assertTrue(c.contains(z));
   }

   @Test
   public void testOverlap() {
    Variable x = new Var("x", TypeFactory.createSort("a"));
    Variable y = new Var("y", TypeFactory.createSort("a"));
    Variable z = new Var("x", TypeFactory.createSort("a"));
    Variable u = new Binder("u", TypeFactory.createSort("b"));
    ArrayList<Variable> vars1 = new ArrayList<Variable>();
    vars1.add(x);
    vars1.add(y);
    vars1.add(u);
    VariableList l1 = new VarList(vars1);
    ArrayList<Variable> vars2 = new ArrayList<Variable>();
    vars2.add(z);
    vars2.add(y);
    vars2.add(u);
    VariableList l2 = new VarList(vars2);

    TreeSet<Variable> overlap = l1.getOverlap(l2);
    assertTrue(overlap.size() == 2);
    assertTrue(overlap.contains(y));
    assertTrue(overlap.contains(u));
   }

  @Test
  public void testRenaming() {
    Type a = TypeFactory.createSort("a");
    Type b = TypeFactory.createSort("b");
    Type ab = TypeFactory.createArrow(a, b);
    TreeSet<Variable> set = new TreeSet<Variable>();
    Variable x1 = new Var("x", a); set.add(x1);
    Variable x2 = new Var("x", a); set.add(x2);
    Variable x3 = new Binder("x", b); set.add(x3);
    Variable y = new Binder("y", b); set.add(y);
    Variable z1 = new Binder("z", ab); set.add(z1);
    Variable z2 = new Var("z", ab); set.add(z2);
    VarList lst = new VarList(set);
    TreeMap<Variable,String> naming = lst.getUniqueNaming();
    System.out.println("x1 has been named: " + naming.get(x1));
    assertTrue(naming.get(x1).equals("x__1"));
    assertTrue(naming.get(x2).equals("x__2"));
    assertTrue(naming.get(x3).equals("x__3"));
    assertTrue(naming.get(y).equals("y"));
    assertTrue(naming.get(z1).equals("z__2"));  // binders come after non-binders
    assertTrue(naming.get(z2).equals("z__1"));
  }
}

